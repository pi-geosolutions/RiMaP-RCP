package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;

public class PadreCatalog implements ICatalog {

	@Inject
	ISessionService sessionService;

	@Inject
	ISecureResourceService resourceService;

	@Inject
	Logger logger;

	@Inject
	IEventBroker broker;

	@Inject
	CatalogParams params;

	private JsonNode layertree_json;

	/*
	 * @Inject @Optional
	 * private void
	 * update(@UIEventTopic(RiMaPEventConstants.SESSION_SERVER_VALIDATED)
	 * Session session) {
	 * logger.info("################# %s ################",
	 * RiMaPEventConstants.SESSION_SERVER_VALIDATED);
	 * logger.info(session.getUsername()+", "+session.getSessionID());
	 * }
	 */

	@Inject
	public PadreCatalog(CatalogParams params, ISessionService sessionService, Logger logger,
			ISecureResourceService resourceService) {
		this.params = params;
		this.sessionService = sessionService;
		this.logger = logger;
		this.resourceService = resourceService;
		load();
	}

	public boolean load() {
		if (sessionService == null) {
			// we abort
			logger.info("Aborting catalog %s loading : session is not set yet", params.getName());
			return false;
		}
		Session session = sessionService.getSession();
		logger.info("[PadreCatalog] Session service instanciated. Session username is "
				+ sessionService.getSession().getUsername());
		this.layertree_json = getLayertree();

		// if null, then it failed. We exit the function.
		if (this.layertree_json == null) {
			logger.error("ERROR parsing layertree (" + this.getClass().getName() + ")");
			return false;
		}

		logger.info("Loading Padre Catalog version service");
		/*
		 * this.root = new FolderLayer(null, true); // null -> root layer has no
		 * // parents. Snif !
		 * this.root.loadFromJson(this.layertree_json);
		 * if (this.root == null) {
		 * return false;
		 * }
		 */
		return true;
	}

	protected JsonNode getLayertree() {
		JsonNode node = null;

		if ((params.getWeb_usage_level() > 1)
				|| !resourceService.isResourceAvailable(params.getCachePath(), params.getName())) {
			// Load from URL
			logger.info("Should load layertree from URL");
			//node = getLayertreeFromURL(path, user, pwd, cachedLayertreeFile);
		} else {
			// Load from file
			logger.info("Should load layertree from file");
			//node = getLayertreeFromFile(cachedLayertreeFile, user, pwd);
		}

		return node;
	}

	protected JsonNode getLayertree(String path, int web_usage_level, String user, String pwd, String cachePath) {

		/*
		 * JsonNode node;
		 * 
		 * File cachedLayertreeFile = new
		 * File(getLocalLayertreeFilePath(cachePath, user));
		 * boolean isLtCached = cachedLayertreeFile.isFile();
		 * if ((web_usage_level > 1) || !isLtCached) {
		 * // Load from URL
		 * node = getLayertreeFromURL(path, user, pwd, cachedLayertreeFile);
		 * } else {
		 * // Load from file
		 * node = getLayertreeFromFile(cachedLayertreeFile, user, pwd);
		 * }
		 * 
		 * return node;
		 */
		return null;
	}

	@Override
	public INode getRootNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload() {
		this.load();
	}

	@Override
	public INode getNodeById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INode getNodeByName(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<INode> getCheckedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<INode> getOpenFolders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<INode> getUnfoldedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

}
