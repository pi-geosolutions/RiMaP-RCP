package fr.pigeo.rimap.rimaprcp.lifecycle;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class LifeCycleManager {
	private Preferences preferences;
	private String preferencesNode = "fr.pigeo.rimap.rimaprcp";

	@Inject
	Logger logger;

	@PostContextCreate
	void postContextCreate(IApplicationContext appContext, Display display,
			IPreferencesService prefService /* default preferences */,
			WwjInstance wwj /*
							 * Needed to instanciate from custom config before
							 * any call to worldwind, like cache file path
							 */) {

		// close the static splash screen
		// appContext.applicationRunning();

		Preferences preferences = InstanceScope.INSTANCE.getNode("fr.pigeo.rimap.rimaprcp");
		/*
		 * get the path where to store persisted data (layertree, etc) for cache
		 * management 1) get WorldWind cache path 2) go up 1 level and create
		 * Padre folder Then write it in the preferences
		 */
		wwj.getWwd();
		FileStore store = new BasicDataFileStore();
		String cacheFolderName = prefService.getString(preferencesNode, "cache.rootname", "RiMaP", null);
		String cachePath = store.getWriteLocation().getParentFile() + File.separator + cacheFolderName;
		initCacheFolder(cachePath);
		Preferences config = preferences.node("config");
		config.put("cachePath", cachePath);
		try {
			preferences.flush();
			logger.info("Rimap cache storage path: " + cachePath);
		} catch (BackingStoreException e) {
			logger.error(e);
		}
	}

	@ProcessAdditions
	void processAdditions(ISessionService sessionService) {
		logger.info("[LIFECYCLEMANAGER] : open session ! ");
		sessionService.openSession(true);
	}
	
	@ProcessRemovals
	void ProcessRemovals(ISecureResourceService resourceService) {
		//TODO: remove next line
		logger.info("[LIFECYCLEMANAGER] : Secure Resource Service loaded");
	}


	/**
	 * Creates the directory and ancestors if needed
	 * 
	 * @param cachePath
	 */
	private void initCacheFolder(String cachePath) {
		File path = (new File(cachePath));
		if (path.isFile()) {
			logger.error("Oops ! Cache path points to a file. It should be a directory (or not exist). Exiting");
			// close the application
			System.exit(-1);
		}
		if (path.isDirectory()) {
			return;
		} else {
			path.mkdirs();
		}
	}
}
