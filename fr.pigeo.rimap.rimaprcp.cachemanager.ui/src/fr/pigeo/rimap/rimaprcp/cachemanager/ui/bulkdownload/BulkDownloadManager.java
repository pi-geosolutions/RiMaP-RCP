package fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards.ExportPackageWizard;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards.ExportPackageWizardPage1;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards.ExportPackageWizardPage2;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadables;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.RenderableManager;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.util.SectorSelector;
import gov.nasa.worldwind.retrieve.BulkRetrievalThread;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *         Central class for BulkDownload. Provides access to DI instances,
 *         dispatches communication
 */
@Creatable
@Singleton
public class BulkDownloadManager {
	private SectorSelector selector;
	@Inject
	IEclipseContext context;

	@Inject
	WwjInstance wwj;

	@Inject
	IEventBroker evtBroker;

	@Inject
	@Named(RimapConstants.RIMAP_CACHE_PATH_CONTEXT_NAME)
	String cachePath;

	@Inject
	Downloadables downloadables;

	@Inject
	UISynchronize sync;

	@Inject
	RenderableManager rmanager;

	@Inject
	Shell shell;

	@Inject
	public BulkDownloadManager(WwjInstance wwjInst, IEventBroker evtBroker, RenderableManager rmanager) {
		// Init sector selector
		this.selector = new SectorSelector(wwjInst.getWwd(), rmanager.getRenderableLayer());
		this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
		this.selector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
		this.selector.setBorderWidth(3);
		this.selector.addPropertyChangeListener(SectorSelector.SECTOR_PROPERTY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null) {
					// means mouse released, means finished drawing
					evtBroker.post(CacheManagerEventConstants.SECTORSELECTOR_FINISHED, selector.getSector());
				} else {
					// means still drawing (mouse not released)
					evtBroker.post(CacheManagerEventConstants.SECTORSELECTOR_DRAWING, selector.getSector());
				}
			}
		});

		wwjInst.getWwd()
				.redraw();
	}

	public void enableSectorSelector() {
		rmanager.clearLayer();
		selector.enable();
	}

	public IEclipseContext getEclipseContext() {
		return context;
	}

	public WwjInstance getWwjInstance() {
		return wwj;
	}

	public IEventBroker getEventBroker() {
		return evtBroker;
	}

	public long getTotalDownloadSize() {
		return downloadables.getTotalDownloadSize();
	}

	public String getCachePath() {
		return cachePath;
	}

	public long getFreeSpace() {
		File file = new File(cachePath);
		long freeSpace = file.getFreeSpace(); // unallocated / free disk space
												// in bytes.
		return freeSpace;
	}

	protected boolean hasActiveDownloadThreads() {
		Iterator<Downloadable> it = downloadables.getDownloadList()
				.iterator();
		while (it.hasNext()) {
			Downloadable d = it.next();
			if (d.isDownloadThreadActive()) {
				return true;
			}
		}
		return false;
	}

	public void startBulkDownload() {
		// get the list of Downloadable set as to-download
		Iterator<Downloadable> it = downloadables.getDownloadList()
				.iterator();
		while (it.hasNext()) {
			Downloadable d = it.next();
			if (d.intersectsCurrentSector()) {
				BulkRetrievalThread brthread = d.startDownloadThread();
			}
			// System.out.println("Bulk retrieval thread (" + d.getLayer()
			// .getName() + ") status : " + brthread.getState());
		}

		Job job = new Job("Update Download Progress") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				while (hasActiveDownloadThreads()) {
					try {
						evtBroker.post(CacheManagerEventConstants.DOWNLOAD_PROGRESS_UPDATE, "");
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return Status.CANCEL_STATUS;
					}
				}
				// Last update
				evtBroker.post(CacheManagerEventConstants.DOWNLOAD_PROGRESS_UPDATE, "");
				/*
				 * // set total number of work units
				 * monitor.beginTask("Doing something time consuming here",
				 * 100);
				 * for (int i = 0; i < 5; i++) {
				 * try {
				 * // sleep a second
				 * TimeUnit.SECONDS.sleep(1);
				 * 
				 * monitor.subTask("I'm doing something here " + i);
				 * 
				 * // report that 20 additional units are done
				 * monitor.worked(20);
				 * } catch (InterruptedException e1) {
				 * e1.printStackTrace();
				 * return Status.CANCEL_STATUS;
				 * }
				 * }
				 */
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	@Inject
	@Optional
	void updateProgress(@UIEventTopic(CacheManagerEventConstants.EXPORT_PACKAGE) Downloadable d,
			IEclipseContext context) {
		// create new context
		IEclipseContext wizardCtx = context.createChild();
		wizardCtx.set(Downloadable.class, d);
		// create WizardPages via CIF
		ExportPackageWizardPage1 page1 = ContextInjectionFactory.make(ExportPackageWizardPage1.class, wizardCtx);
		wizardCtx.set(ExportPackageWizardPage1.class, page1);
		// no context needed for the creation
		ExportPackageWizardPage2 page2 = ContextInjectionFactory.make(ExportPackageWizardPage2.class, wizardCtx);
		wizardCtx.set(ExportPackageWizardPage2.class, page2);

		ExportPackageWizard wizard = ContextInjectionFactory.make(ExportPackageWizard.class, wizardCtx);

		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() == WizardDialog.OK) {
		}
	}
}
