package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.List;

public interface ICatalogService {
	public ICatalog getMainCatalog();
	public ICatalog newCatalog(String className, String id);
	public ICatalog getCatalog(String id);
	public List<ICatalog> getCatalogs();
	public void deleteCatalog(String id);
}
