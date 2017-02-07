package fr.pigeo.rimap.rimaprcp.core.constants;

public interface RimapConstants {
	public static String RIMAP_CACHE_PATH_CONTEXT_NAME = "fr/pigeo/rimap/rimaprcp/cache/path";
	public static String RIMAP_SELECTEDLAYERS_CONTEXT_NAME = "fr/pigeo/rimap/rimaprcp/layers/selected";
	
	public static String RIMAP_DEFAULT_PREFERENCE_NODE="fr.pigeo.rimap.rimaprcp.core";
	public static String PREFERENCES_CONFIG_NODE_TAG  ="config";

	//Preferences Tags / default values (keep them by pair)
	public static String PROJECT_BASEURL_PREF_TAG = "project.baseurl";
	public static String PROJECT_BASEURL_PREF_DEFAULT = null;
	
	public static String WEB_USAGE_LEVEL_PREF_TAG = "web.usage.level";
	public static int WEB_USAGE_LEVEL_PREF_DEFAULT = 1;
	
	public static String WEB_CONNECTION_TIMEOUT_PREF_TAG = "web.connect.timeout";
	public static int WEB_CONNECTION_TIMEOUT_PREF_DEFAULT = 5;
	
	public static String WEB_READ_TIMEOUT_PREF_TAG = "web.read.timeout";
	public static int WEB_READ_TIMEOUT_PREF_DEFAULT = 20;
	
	public static String CACHE_ROOTNAME_PREF_TAG  ="cache.rootname";
	public static String CACHE_ROOTNAME_PREF_DEFAULT  ="RiMaP";
	
	public static String CACHE_PATH_PREF_TAG  ="cachePath";
	public static String CACHE_PATH_PREF_DEFAULT  ="cachePath";
	
	public static String WMS_LEGEND_RELPATH_PREF_TAG  =	"catalog.wms.getlegend.relpath";
	public static String WMS_LEGEND_RELPATH_PREF_DEFAULT  =	"REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&LAYER=";

	//Geocatalog-related
	public static String CATALOG_METADATA_BY_UUID_RELPATH_PREF_TAG  =	"catalog.metadata.relpath";
	public static String CATALOG_METADATA_BY_UUID_PREF_DEFAULT  =	"/apps/geoportal/index.html?uuid=";

	public static String CATALOG_RESOURCES_SERVICE_PREF_TAG  =	"catalog.resources.relpath";
	public static String CATALOG_RESOURCES_SERVICE_PREF_DEFAULT  =	"resources.get?";
	
	public static String CATALOG_VERSION_PREF_TAG  =	"catalog.version";
	public static String CATALOG_VERSION_PREF_DEFAULT  =	"3.2";
	
	// WorldWind preferences Tags
	public static String WW_DEFAULT_PREFERENCE_NODE="fr.pigeo.rimap.rimaprcp.worldwind";
	public static String WW_DEFAULT_LAYER_DETAILSHINT="defaults.layer.detailshint";
}
