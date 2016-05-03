package fr.pigeo.rimap.rimaprcp.catalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.mapservers.ServerCapability;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class PadreCatalog extends AbstractCatalog {
	protected String layertreeFileName = "layertree.txt";

	private URL baseURL;
	private JsonNode layertree_json;
	private FolderLayer root;

	private int web_connect_timeout = 2000;
	private int web_read_timeout = 10000;
	private String username = "";
	private String userpassword = "";
	private String cachePath = "";
	private int web_usage_level = 9;

	/*
	 * Gets layertree data from base URL (e.g.
	 * http://ne-risk.pigeo.fr/ne-risk-gn2_10) TODO : implement recovery from
	 * URL + disk storage + offline capability
	 */
	public PadreCatalog(int web_usage_level) {
		Preferences preferences = InstanceScope.INSTANCE.getNode("fr.pigeo.rimap.rimaprcp");
		Preferences user = preferences.node("user");
		username = user.get("name", "");
		userpassword = user.get("password", "");
		Preferences configPref = preferences.node("config");
		cachePath = configPref.get("cachePath", "");

		this.web_usage_level = web_usage_level;
	}

	public boolean load(String path) {
		this.layertree_json = getLayertree(path, web_usage_level, username, userpassword, cachePath);

		// if null, then it failed. We exit the function.
		if (this.layertree_json == null) {
			System.err.println("ERROR parsing layertree (" + this.getClass().getName() + ")");
			return false;
		}
		this.root = new FolderLayer(null, true); // null -> root layer has no
													// parents. Snif !
		this.root.loadFromJson(this.layertree_json);
		if (this.root == null) {
			return false;
		}
		return true;
	}

	protected JsonNode getLayertree(String path, int web_usage_level, String user, String pwd, String cachePath) {
		JsonNode node;

		File cachedLayertreeFile = new File(getLocalLayertreeFilePath(cachePath, user));
		boolean isLtCached = cachedLayertreeFile.isFile();
		if (web_usage_level > 1 || !isLtCached) {
			// Load from URL
			node = getLayertreeFromURL(path, user, pwd, cachedLayertreeFile);
		} else {
			// Load from file
			node = getLayertreeFromFile(cachedLayertreeFile, user, pwd);
		}

		return node;
	}

	private JsonNode getLayertreeFromFile(File cachedLayertreeFile, String user, String pwd) {
		// TODO Deal with data encryption if user & pwd are set

		// load from File into a JsonNode (Jackson lib) object
		JsonNode node = null;
		// We create the JsonParser using Jackson
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			node = objectMapper.readValue(cachedLayertreeFile, JsonNode.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if null, then it failed. We exit the function.
		if (node == null) {
			System.err.println("ERROR parsing layertree (" + this.getClass().getName() + ") from file "
					+ cachedLayertreeFile.getPath());
		}
		System.out.println("Loaded layertree from file " + cachedLayertreeFile.getPath());
		return node;
	}

	private JsonNode getLayertreeFromURL(String path, String user, String pwd, File cacheDestination) {
		try {
			FileUtils.copyURLToFile(new URL(path), cacheDestination, this.web_connect_timeout, this.web_read_timeout);
			if (!cacheDestination.isFile()) {
				return null;
			}
			return getLayertreeFromFile(cacheDestination, user, pwd);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
		/*
		 * JsonNode node = null; // load from URL into a JsonNode (Jackson lib)
		 * object try { this.baseURL = new URL(path); BufferedReader in = new
		 * BufferedReader(new InputStreamReader(baseURL.openStream())); String
		 * lt = "", input; while ((input = in.readLine()) != null) { lt +=
		 * input; } in.close(); if ((lt == null) || (lt.equalsIgnoreCase(""))) {
		 * System.out.println("Empty layertree / layertree loading failure");
		 * return null; }
		 * 
		 * // We create the JsonParser using Jackson ObjectMapper objectMapper =
		 * new ObjectMapper(); node = objectMapper.readValue(lt,
		 * JsonNode.class);
		 * 
		 * //save it on disk (in cache location) this.cacheLayertree(lt,
		 * cacheDestination); } catch (MalformedURLException e) { // TODO maybe
		 * try if it is not a file path instead of URL e.printStackTrace(); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * // if null, then it failed. We exit the function. if (node == null) {
		 * System.err.println("ERROR parsing layertree (" +
		 * this.getClass().getName() + ") from URL " + path); }
		 * 
		 * System.out.println("Loaded layertree from URL "+path); return node;
		 */
	}

	private void cacheLayertree(String lt, File destination)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		if (!destination.isFile()) {
			destination.createNewFile();
		}
		om.writeValue(destination, lt);
	}

	private String getLocalLayertreeFilePath(String cachePath, String user) {
		// if user name is set, introduces intermediary folder
		String path = cachePath + File.separator + user + File.separator + layertreeFileName;
		System.out.println("Local cached LT path is " + path);
		return path;
	}

	public FolderLayer getRoot() {
		if (this.root == null) {
			System.out.println("root node is null. Oops !");
			FolderLayer root = new FolderLayer(null, true);
			// (null -> root layer has no parents. Snif !)
			root.setName("root");
			this.root = root;
			return root;
		}
		return this.root;
	}

	public URL getBaseURL() {
		return baseURL;
	}

	/*
	 * STATIC METHODS
	 */

	public static List<FolderLayer> getExpandedFolders() {
		return expandedFolders;
	}

	public static FolderLayer[] getExpandedFoldersAsArray() {
		if (expandedFolders == null)
			return new FolderLayer[0];
		return expandedFolders.toArray(new FolderLayer[0]);
	}

	public static void addExpandedFolder(FolderLayer folder) {
		if (AbstractCatalog.expandedFolders == null)
			AbstractCatalog.expandedFolders = new ArrayList<FolderLayer>();
		AbstractCatalog.expandedFolders.add(folder);
	}

	public static List<AbstractLayer> getInitiallyCheckedLayers() {
		return initiallyCheckedLayers;
	}

	public static AbstractLayer[] getInitiallyCheckedLayersAsArray() {
		if (initiallyCheckedLayers == null)
			return null;
		return initiallyCheckedLayers.toArray(new AbstractLayer[0]);
	}

	public static void addInitiallyCheckedLayer(AbstractLayer layer) {
		if (AbstractCatalog.initiallyCheckedLayers == null)
			AbstractCatalog.initiallyCheckedLayers = new ArrayList<AbstractLayer>();
		AbstractCatalog.initiallyCheckedLayers.add(layer);
	}

	public static void addServerCapability(String url) {
		url = PadreCatalog.cleanURL(url);
		PadreCatalog.getServerCapabilities(url);
		// we don't care about the returned value. Just wanted to add the caps
	}

	public static WMSCapabilities getServerCapabilities(String url) {
		url = PadreCatalog.cleanURL(url);
		if (AbstractCatalog.serverCapabilitiesList == null)
			AbstractCatalog.serverCapabilitiesList = new HashMap<String, ServerCapability>();
		// returns non-null if already stored in the hash
		ServerCapability capability = AbstractCatalog.serverCapabilitiesList.get(url.toLowerCase());
		if (capability == null) {
			// Else we create the capability object and add it to the hash
			// System.out.println("Adding capabilities for server "+url+" ");
			try {
				URI wmsUri = new URI(url);
				WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
				caps.parse();
				capability = new ServerCapability(url, caps);
				AbstractCatalog.serverCapabilitiesList.put(url.toLowerCase(), capability);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return capability.getCapabilities();
	}

	public static String cleanURL(String url) {
		return url.replaceAll("(?<!(http:|https:))//", "/");
	}
}
