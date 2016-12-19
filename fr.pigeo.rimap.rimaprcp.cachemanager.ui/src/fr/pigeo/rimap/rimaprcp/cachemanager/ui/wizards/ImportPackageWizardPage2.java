package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
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
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheUtil;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.ImportableCachePacks;

public class ImportPackageWizardPage2 extends WizardPage {
	@Inject
	UISynchronize sync;

	@Inject
	private ImportableCachePacks cachePacks;
	
	protected Text txtConsole;
	protected ProgressBar progressBar;
	protected Composite container;
	protected Button btnProceed;
	protected ExpandItem consoleExpandItem;
	protected boolean exportFinished = false;
	
	@Inject
	public ImportPackageWizardPage2() {
		super("Import Packages, page 2");
		setDescription("Select and import pregenerated ZIP Cache Packs");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		btnProceed = new Button(container, SWT.NONE);
		btnProceed.setText("Proceed");
		btnProceed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cachePacks.installPacks();
				btnProceed.setEnabled(false);
			}
		});

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 65, 15);
		lblNewLabel.setText("Export progress");

		progressBar = new ProgressBar(container, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBar.setMaximum(100);
		progressBar.setMinimum(1);

		ExpandBar expandBar = new ExpandBar(container, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		consoleExpandItem = new ExpandItem(expandBar, SWT.NONE);
		// consoleExpandItem.setExpanded(true);
		consoleExpandItem.setText("Details");

		Composite composite = new Composite(expandBar, SWT.NONE);
		consoleExpandItem.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		txtConsole = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtConsole.setText("");
		//txtConsole.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//txtConsole.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		//txtConsole.setForeground(SWTResourceManager.getColor(255, 255, 255));
		//txtConsole.setBackground(SWTResourceManager.getColor(0, 0, 0));
		consoleExpandItem.setHeight(200);
		expandBar.computeSize(NONE, NONE, true);

	}
	

	@Inject
	@Optional
	protected void updateConsole(
			@UIEventTopic(CacheManagerEventConstants.IMPORT_PACKAGE_CONSOLE_MESSAGE) String message) {
		if (txtConsole != null && !txtConsole.isDisposed()) {
			txtConsole.append(message);
		}
	}

	@Inject
	@Optional
	protected void updateProgressBar(@UIEventTopic(CacheManagerEventConstants.IMPORT_PACKAGE_PROGRESS_UPDATE) int progress) {
		if (progressBar != null && !progressBar.isDisposed()) {
			progressBar.setSelection(progress);
			if (progress == 100) {
				String message = "Complete"+CacheUtil.linebreak;
				progressBar.setToolTipText(message);
				txtConsole.append(message);
				this.exportFinished = true;
				getWizard().getContainer()
						.updateButtons();
			}
		}
	}

	public boolean canFinish() {
		btnProceed.setEnabled(true);
		return exportFinished;
	}

}
