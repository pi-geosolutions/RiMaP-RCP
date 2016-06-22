package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.util.WWXML;

public class CacheDataSet {

	private File dir;
	private String filename;
	private Document xmldoc;
	private Element elem;

	public CacheDataSet(String filename, File dir) {
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

	public String getNumLevels() {

		String num = WWXML.getText(elem, "/*/NumLevels/@count");
		return num;
	}

	public String getLatSW() {
		String lalo = WWXML.getText(elem, "/*/Sector/SouthWest/LatLon/@latitude", null);
		return lalo;
	}

	public String getLonSW() {
		String lalo = WWXML.getText(elem, "/*/Sector/SouthWest/LatLon/@longitude", null);
		return lalo;
	}

	public String getLatNE() {
		String lalo = WWXML.getText(elem, "/*/Sector/NorthEast/LatLon/@latitude", null);
		return lalo;
	}

	public String getLonNE() {
		String lalo = WWXML.getText(elem, "/*/Sector/NorthEast/LatLon/@longitude", null);
		return lalo;
	}

	public long getLastModif() {

		return dir.lastModified();
	}

	public long directorySize() {

		File homepath = new File(dir.getParent());
		long size = FileUtils.sizeOfDirectory(homepath);
		System.out.println("size    " + size);
		return size;
	}
	


	/*private long findDirSize(File dir, long[] tab) {
		// System.out.println("test0 " + dir.getPath());
		long sizefile = tab[0];
		long sizedir = tab[1];

		
		// if (!dir.isDirectory()) { return 0; }
		 

		if (isSingleDataSet(dir.listFiles())) {
			sizedir = sizedir + findFileSize(dir, tab)[1];
			tab[1] = sizedir;
			System.out.println("sizedir" + sizedir);
		}

		else {
			for (File sd : dir.listFiles()) {
				findDirSize(sd, tab);
			}
		}
		return sizedir;

	}

	private long[] findFileSize(File dir, long[] tab) {

		long sizefile = tab[0];
		// long sizefile = 0;
		long sizedir = tab[1];
		long temp = 0;

		for (File sd : dir.listFiles()) {
			if (sd.isFile()) {

				// File pathname = sd.getAbsoluteFile();
				// String filename = sd.getPath();
				// sizefile = sizefile + filename.length();

				try {
					temp = Files.size(Paths.get(filename));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sizefile += temp;
				tab[0] = sizefile;
				System.out.println("file_size " + sd + "temp  " + temp + "taille file  " + sizefile);

			} else {
				findDirSize(sd, tab);
			}

			tab[1] = sizedir;
		}
		return (tab);

	}
	*/

	

}
