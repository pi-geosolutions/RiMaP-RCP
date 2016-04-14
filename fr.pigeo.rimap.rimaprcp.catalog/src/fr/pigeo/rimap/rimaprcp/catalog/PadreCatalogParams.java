package fr.pigeo.rimap.rimaprcp.catalog;

public class PadreCatalogParams implements ICatalogParams {
	String catalog_url;
	String catalog_servicepath;
	
	public PadreCatalogParams(String url) {
		this.catalog_url=url;
	}
	
	public PadreCatalogParams(String url, String servicepath) {
		this.catalog_url=url;
		this.catalog_servicepath=servicepath;
	}

	public String getFullPath() {
		String path = this.catalog_url;
		if (!path.endsWith(".get") && this.catalog_servicepath!=null) {
			// autocomplete URL if necessary
			path = path + "/" + this.catalog_servicepath;
		}
		return path;
	}
	
	public ICatalog buildCatalogInstance() {
		String path = this.getFullPath();
		if (path!=null) { 
			return new PadreCatalog(path);
		}
		System.err.println("Could not build PadreCatalog instance: improper Padre Catalog path");
		return null;
	}
}
