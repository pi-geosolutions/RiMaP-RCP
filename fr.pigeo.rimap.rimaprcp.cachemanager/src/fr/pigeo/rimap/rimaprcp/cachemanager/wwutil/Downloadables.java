package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.retrieve.BulkRetrievable;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *         Manages the downloadables layers list
 */
@Creatable
@Singleton
public class Downloadables {
	protected List<Downloadable> downloadables = null;
	private WWObject[] layers = null;

	@Inject
	WwjInstance wwj;

	@Inject
	IEventBroker evtBroker;

	@Inject
	public Downloadables(WwjInstance wwj) {
		this.wwj = wwj;
	}

	public List<Downloadable> getList() {
		layers = wwj.getLayersListAsArray();
		if (downloadables == null || downloadables.isEmpty()) {
			downloadables = new ArrayList<Downloadable>();
			for (WWObject l : layers) {
				if (l instanceof BulkRetrievable) {// filter-out layers that
													// will not be downloadable
					Downloadable d = new Downloadable((WWObject) l, wwj, evtBroker);
					if (d.isSupported()) {
						downloadables.add(d);
					}
				}
			}
		}
		return downloadables;
	}

	public List<Downloadable> getList(boolean reset) {
		if (reset) {
			for (Downloadable d : downloadables) {
				d.stopThread();
			}
			downloadables.clear();
		}
		return this.getList();
	}

	public List<Downloadable> getDownloadList() {
		// not sure it will work with java < 1.8
		List<Downloadable> list = downloadables.stream()
				.filter(d -> d.doDownload())
				.collect(Collectors.toList());
		return list;
	}

	public void setSector(Sector s) {
		Iterator<Downloadable> it = downloadables.iterator();
		while (it.hasNext()) {
			Downloadable d = it.next();
			d.updateSector(s);
		}
	}

	public long getTotalDownloadSize() {
		long total = 0;
		Iterator<Downloadable> it = downloadables.iterator();
		while (it.hasNext()) {
			Downloadable d = it.next();
			if (d.doDownload()) {
				total += d.getEstimatedSize();
			}
		}
		return total;

	}

}
