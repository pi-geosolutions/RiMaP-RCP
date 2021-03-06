package fr.pigeo.rimap.rimaprcp.cachemanager.events;

public interface CacheManagerEventConstants {
	String DOWNLOADABLE_SIZE_UPDATED			="fr/pigeo/rimap/rimaprcp/cachemanager/downloadable/size/updated";
	String SECTORSELECTOR_DRAWING			="fr/pigeo/rimap/rimaprcp/cachemanager/sectorselector/drawing";
	String SECTORSELECTOR_FINISHED			="fr/pigeo/rimap/rimaprcp/cachemanager/sectorselector/finished";
	String MAXRESOLUTION_CHANGED			="fr/pigeo/rimap/rimaprcp/cachemanager/maxresolution/changed";
	String DOWNLOAD_PROGRESS_UPDATE			="fr/pigeo/rimap/rimaprcp/cachemanager/download/progress/update";
	String CACHEDDATASET_UPDATE			="fr/pigeo/rimap/rimaprcp/cachemanager/CachedDataset/update";
	String BULKDOWNLOAD_TABLE_RELOAD			="fr/pigeo/rimap/rimaprcp/cachemanager/bulkdownloadtable/reload";
	String EXPORT_PACKAGE			="fr/pigeo/rimap/rimaprcp/cachemanager/bulkdownloadtable/package/export";
	String EXPORT_PACKAGE_CONSOLE_MESSAGE="fr/pigeo/rimap/rimaprcp/cachemanager/bulkdownloadtable/package/export/console/message";
	String EXPORT_PACKAGE_PROGRESS_UPDATE="fr/pigeo/rimap/rimaprcp/cachemanager/bulkdownloadtable/package/export/progress/update";
	String IMPORT_PACKAGE_CONSOLE_MESSAGE ="fr/pigeo/rimap/rimaprcp/cachemanager/package/import/console/message";
	String IMPORT_PACKAGE_PROGRESS_UPDATE ="fr/pigeo/rimap/rimaprcp/cachemanager/package/import/progress/update";
}
