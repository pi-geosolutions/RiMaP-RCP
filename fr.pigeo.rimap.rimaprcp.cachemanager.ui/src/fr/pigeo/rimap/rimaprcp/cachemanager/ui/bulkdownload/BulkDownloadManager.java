package fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadables;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.RenderableManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.SectorSelector;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
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
    	long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
    	return freeSpace;
	}
	
	public void startBulkDownload() {
		//get the list of Downloadable set as to-download
		Iterator<Downloadable> it = downloadables.getDownloadList().iterator();
		while (it.hasNext()) {
			Downloadable d = it.next();
			BulkRetrievalThread brthread = d.getDownloadThread();
			System.out.println("Bulk retrieval thread ("+d.getLayer().getName()+") status : " +brthread.getState());
		}
	}

}
