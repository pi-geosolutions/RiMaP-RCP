package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

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

import fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload.BulkDownloadManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;

public class ExportPackageWizardPage2 extends WizardPage {
	protected Downloadable d;
	protected Text text;

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
				d.zip();
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
}
