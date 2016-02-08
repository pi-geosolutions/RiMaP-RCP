package fr.pigeo.rimap.rimaprcp.catalog;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.mapservers.ServerCapability;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class RiskJfaceCatalogImpl {
	private static List<FolderLayer> expandedFolders; //used to give initial expanded info to jTreeViewer
	private static List<AbstractLayer> initiallyCheckedLayers; //used to give initial layers to check
	private static Map<String,ServerCapability> serverCapabilitiesList; //used to give initial layers to check
	
	private URL baseURL;
	private JsonNode layertree_json;
	private FolderLayer root;	

	/*
	 * Gets layertree data from base URL (e.g.
	 * http://ne-risk.pigeo.fr/ne-risk-gn2_10) TODO : implement recovery from
	 * URL + disk storage + offline capability
	 */
	public RiskJfaceCatalogImpl() {
		//load from URL into a JsonNode (Jackson lib) object
		try {
			
			String path = CatalogProperties.getProperty("catalog.baseurl");
			if (!path.endsWith(".get")) {
				// autocomplete URL if necessary
				path = path + CatalogProperties.getProperty("layertree.servicepath");
			}
			this.baseURL = new URL(path);
			// We create the JsonParser using Jackson
			ObjectMapper objectMapper = new ObjectMapper();
			this.layertree_json = objectMapper.readValue(this.baseURL, JsonNode.class);
		} catch (MalformedURLException e) {
			// TODO maybe try if it is not a file path instead of URL
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// if null, then it failed. We exit the function.
		if (this.layertree_json == null) {
			System.out.println("ERROR parsing layertree (" + this.getClass().getName() + ")");
			return;
		}
		this.root = new FolderLayer(null, true); //null -> root layer has no parents. Snif !
		this.root.loadFromJson(this.layertree_json);
	}
	
	public void loadNodes() {
		
	}

	public FolderLayer getRoot() {
		if (this.root==null) {
			System.out.println("root node is null. Oops !");
			FolderLayer root = new FolderLayer(null, true); //null -> root layer has no parents. Snif !
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
		if (expandedFolders==null)
			return null;
		return expandedFolders.toArray(new FolderLayer[0]);
	}

	public static void addExpandedFolder(FolderLayer folder) {
		if (RiskJfaceCatalogImpl.expandedFolders==null)
			RiskJfaceCatalogImpl.expandedFolders = new ArrayList<FolderLayer>();
		RiskJfaceCatalogImpl.expandedFolders.add(folder);
	}

	public static List<AbstractLayer> getInitiallyCheckedLayers() {
		return initiallyCheckedLayers;
	}
	public static AbstractLayer[] getInitiallyCheckedLayersAsArray() {
		if (initiallyCheckedLayers==null)
			return null;
		return initiallyCheckedLayers.toArray(new AbstractLayer[0]);
	}

	public static void addInitiallyCheckedLayer(AbstractLayer layer) {
		if (RiskJfaceCatalogImpl.initiallyCheckedLayers==null)
			RiskJfaceCatalogImpl.initiallyCheckedLayers = new ArrayList<AbstractLayer>();
		RiskJfaceCatalogImpl.initiallyCheckedLayers.add(layer);
	}

	public static void addServerCapability(String url) {
		url = RiskJfaceCatalogImpl.cleanURL(url);
		RiskJfaceCatalogImpl.getServerCapabilities(url);
		//we don't care about the returned value. Just wanted to add the caps
	}
	
	public static WMSCapabilities getServerCapabilities(String url) {
		url = RiskJfaceCatalogImpl.cleanURL(url);
		if (RiskJfaceCatalogImpl.serverCapabilitiesList==null)
			RiskJfaceCatalogImpl.serverCapabilitiesList = new HashMap<String,ServerCapability>();
		//returns non-null if already stored in the hash
		ServerCapability capability = RiskJfaceCatalogImpl.serverCapabilitiesList.get(url.toLowerCase());
		if (capability==null) {
			//Else we create the capability object and add it to the hash
			//System.out.println("Adding capabilities for server "+url+" ");
			try {
				URI wmsUri = new URI(url);
				WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
				caps.parse();
				capability = new ServerCapability(url,caps);
				RiskJfaceCatalogImpl.serverCapabilitiesList.put(url.toLowerCase(), capability);
				
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
