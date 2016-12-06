package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import gov.nasa.worldwind.geom.Sector;

public class ExportPackageWizardPage2 extends WizardPage {
	Downloadable d;
	private Text text;

	protected ExportPackageWizardPage2(Downloadable downloadable) {
		super("Export Package");
		this.d = downloadable;
		setTitle("Export Package for layer \n" + d.getLayer()
				.getName());
		setDescription("Extracts and packages the corresponding cache file in a ZIP archive");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		Button btnProceed = new Button(container, SWT.NONE);
		btnProceed.setText("Proceed");
		btnProceed.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				zip();
			}

		});

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 65, 15);
		lblNewLabel.setText("Export progress");

		ProgressBar progressBar = new ProgressBar(container, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		ExpandBar expandBar = new ExpandBar(container, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		ExpandItem expandItem1 = new ExpandItem(expandBar, SWT.NONE);
		expandItem1.setText("Details");

		Composite composite = new Composite(expandBar, SWT.NONE);
		expandItem1.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		text = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setText(
				" Composite composite = new Composite(bar, SWT.NONE);\n    GridLayout layout = new GridLayout();\n    layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;\n    layout.verticalSpacing = 10;\n    composite.setLayout(layout);\n    Button button = new Button(composite, SWT.PUSH);\n    button.setText(\"SWT.PUSH\");\n    button = new Button(composite, SWT.RADIO);\n    button.setText(\"SWT.RADIO\");\n    button = new Button(composite, SWT.CHECK);\n    button.setText(\"SWT.CHECK\");\n    button = new Button(composite, SWT.TOGGLE);\n    button.setText(\"SWT.TOGGLE\");\n    ExpandItem item0 = new ExpandItem(bar, SWT.NONE, 0);\n    item0.setText(\"What is your favorite button\");\n    item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);\n    item0.setControl(composite);\n    item0.setImage(image);");
		text.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		expandItem1.setHeight(200);
		expandBar.computeSize(NONE, NONE, true);
	}

	private void zip() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("create", "true");
		try {			
			String pd = d.getPackageDestination();
			URI zipFile = URI.create("jar:file:" + d.getPackageDestination());
			try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes);) {
				Path dest = d.getCacheLocation(false);
				Path src = d.getCacheLocation(true);
				Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
						Integer.MAX_VALUE, new ExtractAndCopyTiles(src, dest,
								zipFileSys));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ExtractAndCopyTiles extends SimpleFileVisitor<Path> {
		private Path source;
		private Path target;
		private FileSystem zipFileSys;

		public ExtractAndCopyTiles(Path source, Path target, FileSystem zipFileSys) {
			this.source = source;
			this.target = target;
			this.zipFileSys = zipFileSys;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			return copyToZip(file);
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
			return copyToZip(directory);
		}

		private FileVisitResult copyToZip(Path p) {
			if (Files.isDirectory(p)) {
				return FileVisitResult.CONTINUE;
			}
			if (!d.putThisTileInThePacket(p)) {
				System.out.println("Excluding file "+p+" (off limits)");
				return FileVisitResult.CONTINUE;
			}
			Path targetInZip = zipFileSys.getPath(target.resolve(source.relativize(p))
					.toString());
			try {
				if (Files.isDirectory(p)) {
					// create non-already created intermediary directories
					Files.createDirectories(targetInZip);
					System.out.println("creating directory " + targetInZip + " (in FS " + targetInZip.getFileSystem() + ")");
				} else {
					// create non-already created intermediary directories : due to the filter in the beginning of the function,
					// directories will be filtered out
					Files.createDirectories(targetInZip.getParent());
					System.out.println("copying file " + p + " to " + targetInZip.toString() + "(in FS "+ targetInZip.getFileSystem() + ")");
					Files.copy(p, targetInZip, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return FileVisitResult.CONTINUE;
		}
	}
}
