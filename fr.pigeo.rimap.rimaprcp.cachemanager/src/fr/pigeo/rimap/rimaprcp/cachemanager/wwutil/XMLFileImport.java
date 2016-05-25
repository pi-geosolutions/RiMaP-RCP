package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheDataSet;

public class XMLFileImport {

	private FileStore store = new BasicDataFileStore();
	// private File cacheRoot = store.getWriteLocation();
	private List<CacheDataSet> xfl = new ArrayList();

	public XMLFileImport() {

	}

	public List<CacheDataSet> listXML() {
		scanFile(store.getWriteLocation());
		return xfl;
	}

	// test sur la présence d'un fichier xml ou présence de fichier 2 digits
	// numeric

	private void scanFile(File dir) {
		// System.out.println("test0 " + dir.getPath());
		if (!dir.isDirectory()) {
			return;
		}

		if (isSingleDataSet(dir.listFiles())) {
			CacheDataSet xmldoc = findxml(dir);
			if (xmldoc != null) {
				xfl.add(xmldoc);
			}
		}

		else {
			for (File sd : dir.listFiles()) {
				scanFile(sd);
			}
		}
		// return XMLFileList;
	}

	private CacheDataSet findxml(File dir) {
		for (File sd : dir.listFiles()) {
			if (sd.isFile()) {

				// File pathname = sd.getAbsoluteFile();
				String filename = sd.getPath();

				System.out.println("test_filename " + filename);

				// String ext = filename.substring(filename.lastIndexOf(".") +
				// 1, filename.length());

				if (filename.endsWith(".xml"))

				{

					// System.out.println("test0 " + filename);

					return new CacheDataSet(filename, sd);
				}
			}
		}
		return null;
	}

	/**
	 * Determines if a list of sub-directories should be treated as a single
	 * data set. This implementation returns {@code true} if all of the
	 * sub-directories have numeric names. In this case, the numeric directories
	 * are most likely used by the cache implementation to group files in a
	 * single data set. The numeric directory names do not provide meaningful
	 * grouping to the user.
	 *
	 * @param subDirs
	 *            List of sub-directories to test.
	 *
	 * @return {@code true} if the directories should be treated as a single
	 *         data set.
	 */
	protected static boolean isSingleDataSet(File[] subDirs) {
		boolean onlyNumericDirs = true;

		for (File sd : subDirs) {
			if (sd.isDirectory()) {
				if (!isNumeric(sd.getName()))
					onlyNumericDirs = false;
			}
		}

		return onlyNumericDirs;
	}

	/**
	 * Determines if a string contains only digits.
	 *
	 * @param s
	 *            String to test.
	 *
	 * @return {@code true} if {@code s} contains only digits.
	 */
	protected static boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

}
