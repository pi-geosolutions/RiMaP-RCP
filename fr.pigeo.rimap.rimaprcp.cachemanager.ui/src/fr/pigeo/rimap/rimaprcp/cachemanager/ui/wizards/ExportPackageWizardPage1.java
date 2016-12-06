package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;

public class ExportPackageWizardPage1 extends WizardPage {
	private Downloadable d;
	private Text fileName;
	private final String[] FILTER_NAMES = { "Zip files (*.zip)", "All Files (*.*)" };

	// These filter extensions are used to filter which files are displayed.
	private final String[] FILTER_EXTS = { "*.zip", "*.*" };

	protected ExportPackageWizardPage1(Downloadable downloadable) {
		super("Export Package");
		this.d = downloadable;
		this.setPageComplete(false);
		this.
		setTitle("Export Package for layer \n" + d.getLayer()
				.getName());
		setDescription("Extracts and packages the corresponding cache file in a ZIP archive");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Select file name and destination");
		new Label(container, SWT.NONE);

		fileName = new Text(container, SWT.BORDER);
		fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fileName.setText(d.getPackageDestination());
		fileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				d.setPackageDestination(fileName.getText());
				getWizard().getContainer().updateButtons();
			}
		});

		Button btnFile = new Button(container, SWT.NONE);
		GridData gd_btnFile = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnFile.widthHint = 50;
		btnFile.setLayoutData(gd_btnFile);
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// User has selected to save a file
				FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				String fn = dlg.open();
				if (fn != null) {
					fileName.setText(fn);
				}
			}
		});
	}

	@Override
	public boolean canFlipToNextPage() {
		//return Files.isRegularFile(Paths.get(fileName.getText()));
		return !fileName.getText().isEmpty();
	}

}
