package fr.pigeo.rimap.rimaprcp.catalog;

import java.util.List;
import java.util.Map;

import fr.pigeo.rimap.rimaprcp.mapservers.ServerCapability;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;

public class AbstractCatalog implements ICatalog {
	protected static List<FolderLayer> expandedFolders; //used to give initial expanded info to jTreeViewer
	protected static List<AbstractLayer> initiallyCheckedLayers; //used to give initial layers to check
	protected static Map<String,ServerCapability> serverCapabilitiesList; //used to give initial layers to check
}
