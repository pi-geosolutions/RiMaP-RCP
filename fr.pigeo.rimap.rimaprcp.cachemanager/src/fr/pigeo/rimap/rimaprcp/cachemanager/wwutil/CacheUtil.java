package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import gov.nasa.worldwind.cache.FileStore;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
@Creatable
@Singleton
public class CacheUtil {
	@Inject
	IEclipseContext context;
	
	@Inject
	IEventBroker evtBroker;

	public List<CachedDataSet> listCachedLayers(FileStore store) {
		return scanFile(store.getWriteLocation());
	}

	private List<CachedDataSet> scanFile(File dir) {
		if (!dir.isDirectory()) {
			return null;
		}

		List<CachedDataSet> xfl = new ArrayList();

		if (isSingleDataSet(dir.listFiles())) {
			CachedDataSet cds = findDatasetDefinition(dir);
			if (cds != null) {
				xfl.add(cds);
			}
		}

		else {
			for (File sd : dir.listFiles()) {
				xfl.addAll(scanFile(sd));
			}
		}
		return xfl;
	}

	/*
	 * Looks for an .xml file to get the dataset definition. 
	 * If no xml file found, it won't consider this as a dataset
	 */
	private CachedDataSet findDatasetDefinition(File dir) {
		for (File sd : dir.listFiles()) {
			if (sd.isFile()) {
				String filename = sd.getPath();
				//System.out.println("test_filename " + filename);

				if (filename.endsWith(".xml")) {
					
					CachedDataSet cds = new CachedDataSet(filename, sd, evtBroker);
					/*ContextInjectionFactory.inject(cds, context);*/
					//CachedDataSet cds = ContextInjectionFactory.make(CachedDataSet.class, context);
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
	protected boolean isSingleDataSet(File[] subDirs) {
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
	protected boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

}
