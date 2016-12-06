package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;

import fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload.BulkDownloadManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;

public class ExportPackageWizard extends Wizard {
	protected Downloadable d;

	@Inject
	public ExportPackageWizard(Downloadable dl) {
		this.d = dl;
		setWindowTitle("New Wizard");
	}

	@Override
	public boolean performCancel() {
		d.cancelZipping();
		return super.performCancel();
	}

	@Override
	public void addPages() {
		addPage(new ExportPackageWizardPage1(d));
		addPage(new ExportPackageWizardPage2(d));
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

}
