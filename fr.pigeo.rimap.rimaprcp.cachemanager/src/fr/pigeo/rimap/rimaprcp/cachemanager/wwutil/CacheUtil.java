package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.eclipse.e4.core.services.events.IEventBroker;

public class CacheUtil {
	public static String linebreak = System.getProperty("line.separator"); // linebreak
	
	
	/*
	 * Looks for an .xml file to get the dataset definition. 
	 * If no xml file found, it won't consider this as a dataset
	 */
	public static CachedDataSet findDatasetDefinition(File dir, IEventBroker evtBroker) {
		for (File sd : dir.listFiles()) {
			if (sd.isFile()) {
				String filename = sd.getPath();
				//System.out.println("test_filename " + filename);

				if (filename.endsWith(".xml")) {
					
					CachedDataSet cds = new CachedDataSet(filename, sd, evtBroker);
					return cds;
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
	public static boolean isSingleDataSet(File[] subDirs) {
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
	public static boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	
    public static String makeSizeDescription(long size)
    {
        double sizeInMegaBytes = ((double)size) / 1024 / 1024;
        if (sizeInMegaBytes < 1)
            return String.format("%,.1f KB", sizeInMegaBytes*1024);
        if (sizeInMegaBytes < 1024)
            return String.format("%,.1f MB", sizeInMegaBytes);
        else if (sizeInMegaBytes < 1024 * 1024)
            return String.format("%,.1f GB", sizeInMegaBytes / 1024);
        return String.format("%,.1f TB", sizeInMegaBytes / 1024 / 1024);
    }

}
