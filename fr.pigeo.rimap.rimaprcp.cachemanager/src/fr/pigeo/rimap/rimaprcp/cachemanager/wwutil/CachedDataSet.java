package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.util.WWXML;

public class CachedDataSet {

	private File dir;
	private String filename;
	private Document xmldoc;
	private Element elem;

	public CachedDataSet(String filename, File dir) {
		super();
		this.filename = filename;
		this.dir = dir;
		xmldoc = WWXML.openDocumentFile(filename, null);
		elem = xmldoc.getDocumentElement();

	}
	
	public File getPathname(){
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
		return  WWXML.getInteger(elem, "/*/NumLevels/@count", null);
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
		File homepath = new File(dir.getParent());
		long size = FileUtils.sizeOfDirectory(homepath);
		//System.out.println("size    " + size);
		return size;
	}
}
