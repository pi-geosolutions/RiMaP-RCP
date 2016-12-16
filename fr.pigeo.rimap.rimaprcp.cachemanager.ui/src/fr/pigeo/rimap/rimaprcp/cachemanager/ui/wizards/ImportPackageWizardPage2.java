package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ImportPackageWizardPage2 extends WizardPage {

	@Inject
	public ImportPackageWizardPage2() {
		super("Import Packages, page 2");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

	}

}
