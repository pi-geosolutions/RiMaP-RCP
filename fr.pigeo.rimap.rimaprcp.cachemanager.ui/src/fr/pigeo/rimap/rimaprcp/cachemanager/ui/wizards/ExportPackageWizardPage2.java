package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
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

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;

public class ExportPackageWizardPage2 extends WizardPage {

	@Inject Downloadable d;
	protected Text txtConsole;
	protected ProgressBar progressBar;
	protected Composite container;

	@Inject
	public ExportPackageWizardPage2() {
		super("Export Package");
		setTitle("Export Package");
		setDescription("Extracts and packages the corresponding cache file in a ZIP archive");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
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

		progressBar = new ProgressBar(container, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		ExpandBar expandBar = new ExpandBar(container, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		ExpandItem expandItem1 = new ExpandItem(expandBar, SWT.NONE);
		expandItem1.setExpanded(true);
		expandItem1.setText("Details");

		Composite composite = new Composite(expandBar, SWT.NONE);
		expandItem1.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		txtConsole = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtConsole.setText(d.getConsoleHeader());
		txtConsole.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtConsole.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		expandItem1.setHeight(200);
		expandBar.computeSize(NONE, NONE, true);
	}
	
	public void updateConsole() {
		txtConsole.setText(d.getConsoleHeader());
	}

	@Inject
	@Optional
	protected void updateConsole(@UIEventTopic(CacheManagerEventConstants.EXPORT_PACKAGE_CONSOLE_MESSAGE) String message) {
		if (txtConsole != null && !txtConsole.isDisposed()) {
			txtConsole.append(message);
			txtConsole.redraw();
			txtConsole.getParent().layout();
		}
	}
}
