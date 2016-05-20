package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.IExpandableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class PadreCatalog implements ICatalog {

	@Inject
	ISessionService sessionService;

	@Inject
	IEclipseContext context;

	@Inject
	ISecureResourceService resourceService;

	@Inject
	WwjInstance wwj;

	@Inject
	Logger logger;

	@Inject
	IEventBroker broker;

	@Inject
	CatalogParams params;

	private URL baseURL;
	private JsonNode layertreeAsJsonNode;
	private INode rootNode;
	private PadreCatalogState catalogState;

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
			ISecureResourceService resourceService, IEclipseContext context, WwjInstance wwj) {
		this.params = params;
		this.sessionService = sessionService;
		this.logger = logger;
		this.resourceService = resourceService;
		this.context = context;
		this.wwj = wwj;
		// load();

	}

	@Override
	public boolean load() {
		if (sessionService == null) {
			// we abort
			logger.info("Aborting catalog %s loading : session is not set yet", params.getName());
			return false;
		}
		Session session = sessionService.getSession();
		logger.info("[PadreCatalog] Session service instanciated. Session username is "
				+ sessionService.getSession().getUsername());
		this.layertreeAsJsonNode = getLayertree();

		// if null, then it failed. We exit the function.
		if (this.layertreeAsJsonNode == null) {
			logger.error("ERROR parsing layertree (" + this.getClass().getName() + ")");
			return false;
		}

		// create a new local_ context
		IEclipseContext catalogContext = EclipseContextFactory.create();
		catalogState = new PadreCatalogState();
		catalogContext.set(PadreCatalogState.class, catalogState);

		// connect new local context with context hierarchy
		catalogContext.setParent(context);

		this.rootNode = ContextInjectionFactory.make(FolderNode.class, catalogContext);
		// this.rootNode = new FolderNode();
		this.rootNode.loadFromJson(this.layertreeAsJsonNode);

		this.checkInitialNodes(this.catalogState.getCheckedNodes());

		return true;
	}

	protected JsonNode getLayertree() {
		JsonNode node = null;

		if ((params.getWeb_usage_level() > 1)
				|| !resourceService.isResourceAvailable(params.getCachePath(), params.getName())) {
			// Load from URL
			logger.info("Should load layertree from URL");
			node = getLayertreeFromURL();
		} else {
			// Load from file
			logger.info("Should load layertree from file");
			node = getLayertreeFromFile();
		}

		return node;
	}

	private JsonNode getLayertreeFromURL() {
		return getLayertreeFromURL(false);
	}

	private JsonNode getLayertreeFromFile() {
		return getLayertreeFromFile(false);
	}

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromFile method if this one fails)
	 * @return
	 */
	private JsonNode getLayertreeFromURL(boolean isFallback) {
		logger.info("Loading layertree from URL");
		JsonNode node = null; // load from URL into a JsonNode (Jackson lib)
		// object
		String lt = "";
		try {
			this.baseURL = new URL(params.getUrl());
			/*
			 * BufferedReader in = new BufferedReader(new
			 * InputStreamReader(baseURL.openStream())); String input; while
			 * ((input = in.readLine()) != null) { lt += input; } in.close();
			 */
			lt = IOUtils.toString(this.baseURL, StandardCharsets.UTF_8);
			if ((lt == null) || (lt.equalsIgnoreCase(""))) {
				logger.error("Empty layertree / layertree loading failure");
				return null;
			}
			node = this.stringToJson(lt);
		} catch (IOException e) {
			String msg = e.getClass().toString() + ": Couldn't load layertree from server.";
			if (isFallback) {
				logger.warn(msg);
				return null;
			} else {
				msg += " Trying to fallback on cached file.";
				logger.warn(msg);
				return this.getLayertreeFromFile(true);
			}
		}
		System.out.println("Loaded layertree from URL " + params.getUrl());

		// save it on disk (in cache location)
		// IOCacheUtil.store(lt, cacheDestination, pwd);
		resourceService.setResource(lt, params.getCachePath(), params.getName());
		return node;
	}

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromURL method if this one fails)
	 * @return
	 */
	private JsonNode getLayertreeFromFile(boolean isFallback) {
		System.out.println("Loading layertree from file");
		String lt = resourceService.getResourceAsString(params.getCachePath(), params.getName());

		// load from File into a JsonNode (Jackson lib) object
		JsonNode node = null;
		// We create the JsonParser using Jackson
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			node = objectMapper.readValue(lt, JsonNode.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if null, then it failed. We exit the function.
		if (node == null) {
			String msg = "Couldn't load layertree from local file " + params.getCachePath() + "/" + params.getName();
			if (isFallback) {
				logger.warn(msg);
				return null;
			} else {
				msg += " Trying to fallback on the server.";
				logger.warn(msg);
				return this.getLayertreeFromURL(true);
			}

		}
		logger.info("Loaded layertree from file " + params.getCachePath() + "/" + params.getName());
		return node;
	}

	private JsonNode stringToJson(String str) {
		JsonNode node = null;
		// We create the JsonParser using Jackson
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			node = objectMapper.readValue(str, JsonNode.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return node;
	}

	@Override
	public INode getRootNode() {
		return this.rootNode;
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
	public List<ICheckableNode> getCheckedNodes() {
		return this.catalogState.getCheckedNodes();
	}

	@Override
	public List<IExpandableNode> getOpenFolders() {
		return getExpandedNodes();
	}

	@Override
	public List<IExpandableNode> getExpandedNodes() {
		return this.catalogState.getExpandedNodes();
	}

	public void checkInitialNodes(List<ICheckableNode> initiallyCheckedNodes) {
		if (catalogState == null || catalogState.getCheckedNodes() == null) {
			return;
		}
		if (wwj == null) {
			System.out.println("Oops, wwj is null !");
			return;
		}
		Iterator<ICheckableNode> itr = catalogState.getCheckedNodes().iterator();
		while (itr.hasNext()) {
			ICheckableNode node = itr.next();
			wwj.addLayer(node.getLayer());
		}
	}
	
	@Override public void sync() {
		this.checkInitialNodes(this.getCheckedNodes());
	}

}
