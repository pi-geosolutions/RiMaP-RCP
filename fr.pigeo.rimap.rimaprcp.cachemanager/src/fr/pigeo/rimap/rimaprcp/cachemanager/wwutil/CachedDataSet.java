package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.util.WWXML;

public class CachedDataSet {
	private File dir;
	private String filename;
	private Document xmldoc;
	private Element elem;
	private Long dataSize = null;

	/*
	 * Needs evtBroker to notify when the size has been computed (could last some time)
	 */
	public CachedDataSet(String filename, File dir, IEventBroker evtBroker) {
		super();
		this.filename = filename;
		this.dir = dir;
		CachedDataSet me = this;
		xmldoc = WWXML.openDocumentFile(filename, null);
		elem = xmldoc.getDocumentElement();
		Thread computeSize = new Thread(new Runnable() {
			@Override
			public void run() {
				File homepath = new File(dir.getParent());
				dataSize = FileUtils.sizeOfDirectory(homepath);
				evtBroker.send("updatedCDS", me);
			}
		});
		computeSize.start();
	}

	public File getPathname() {
		return dir.getParentFile();
	}

	public String getFilename() {
		String obj = WWXML.getText(elem, "/*/DisplayName", null);
		if (obj == null) {
			obj = WWXML.getText(elem, "/*/DatasetName", null);
			if (obj == null) {
				obj = WWXML.getText(elem, "/*/DataCacheName", null);
			}
		}
		return obj;
	}

	public Integer getNumLevels() {
		return WWXML.getInteger(elem, "/*/NumLevels/@count", null);
	}

	public Double getMinLat() {
		return WWXML.getDouble(elem, "/*/Sector/SouthWest/LatLon/@latitude", null);
	}

	public Double getMinLon() {
		return WWXML.getDouble(elem, "/*/Sector/SouthWest/LatLon/@longitude", null);
	}

	public Double getMaxLat() {
		return WWXML.getDouble(elem, "/*/Sector/NorthEast/LatLon/@latitude", null);
	}

	public Double getMaxLon() {
		return WWXML.getDouble(elem, "/*/Sector/NorthEast/LatLon/@longitude", null);
	}

	public long getLastModif() {
		return dir.lastModified();
	}

	public long getDirectorySize() {
		return dataSize;
	}
}
