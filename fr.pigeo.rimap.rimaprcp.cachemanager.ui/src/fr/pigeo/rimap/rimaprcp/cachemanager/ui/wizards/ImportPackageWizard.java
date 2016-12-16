package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;

public class ImportPackageWizard extends Wizard {
	@Inject
	ImportPackageWizardPage1 page1;
	@Inject
	ImportPackageWizardPage2 page2;

	@Inject
	public ImportPackageWizard() {
		setWindowTitle("Import Packages");
	}
	

	@Override
	public void addPages() {
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
