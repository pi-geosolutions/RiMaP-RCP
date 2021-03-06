package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.IExpandableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.resource.WebUsageLevel;
import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.WWObject;

public class PadreCatalog implements ICatalog {

	@Inject
	ISessionService sessionService;

	@Inject
	IEclipseContext context;

	@Inject
	ISecureResourceService secureResourceService;

	@Inject
	IResourceService resourceService;
	
	@Inject
	IWmsService wmsService;

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
	private IExpandableNode rootNode;
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
		this.secureResourceService = resourceService;
		this.context = context;
		this.wwj = wwj;
		// load();

	}

	@Override
	public boolean load() {
		return this.load(false);
	}

	public boolean load(boolean reload) {
		if (sessionService == null) {
			// we abort
			logger.info("Aborting catalog " + params.getName() + " loading : session is not set yet");
			return false;
		}
		if (reload) {
			wmsService.reset();
		}
		Session session = sessionService.getSession();
		logger.info("[PadreCatalog] Session service instanciated. Session username is " + sessionService.getSession()
				.getUsername());
		this.layertreeAsJsonNode = getLayertree(reload);

		// if null, then it failed. We exit the function.
		if (this.layertreeAsJsonNode == null) {
			logger.error("ERROR parsing layertree (" + this.getClass()
					.getName() + ")");
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
		this.rootNode.setName(params.getName());
		this.catalogState.addExpandedNode(this.rootNode);
		this.rootNode.loadFromJson(this.layertreeAsJsonNode);

		this.checkInitialNodes(this.catalogState.getCheckedNodes());

		return true;
	}

	protected JsonNode getLayertree(boolean fromWeb) {
		JsonNode node = null;

		int sourceCode = fromWeb ? WebUsageLevel.PRIORITY_WEB: params.getWeb_usage_level();
		byte[] b = resourceService.getResource(params.getUrl(), sourceCode);
		if (b != null) {
			String lt = new String(b, StandardCharsets.UTF_8);
			node = this.stringToJson(lt);
		}
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
	public boolean reload() {
		return this.load(true);
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
		Iterator<ICheckableNode> itr = catalogState.getCheckedNodes()
				.iterator();
		while (itr.hasNext()) {
			ICheckableNode node = itr.next();
			WWObject l = node.getLayer();
			if (l != null) {
				wwj.addLayer(node.getLayer());
			}
		}
	}

	@Override
	public void sync() {
		this.checkInitialNodes(this.getCheckedNodes());
	}

	@Override
	public boolean testCredentials(String username, String password, boolean local) {
		// TODO: use provided credentials instead of the current session ones
		// (will add more flexibility : e.g. test different credentials for
		// different passwords, using the mainCatalog creds as master password)
		byte[] lt = resourceService.getResourceFromFile(params.getUrl(), true);
		if (lt != null) {
			return true;
		} // else
		return false;
	}

}
