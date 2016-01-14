package fr.pigeo.rimap.rimaprcp.catalog;

import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;

public class RiskJfaceCatalogImpl {
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
}
