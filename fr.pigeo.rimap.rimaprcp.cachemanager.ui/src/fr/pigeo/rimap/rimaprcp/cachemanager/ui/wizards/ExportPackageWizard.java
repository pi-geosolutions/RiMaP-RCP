package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;

public class ExportPackageWizard extends Wizard {
	@Inject
	Downloadable d;

	@Inject
	ExportPackageWizardPage1 page1;
	@Inject
	ExportPackageWizardPage2 page2;

	@Inject
	public ExportPackageWizard() {
		setWindowTitle("Export Package Wizard");
	}

	@Override
	public boolean performCancel() {
		d.cancelZipping();
		return super.performCancel();
	}

	@Override
	public void addPages() {
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean canFinish() {
		return ((getContainer().getCurrentPage()==page2) && page2.canFinish());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

}
