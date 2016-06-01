# RiMaP-RCP Core Catalog Service

The Catalog Service provides implementations for the catalog interfaces found in core plugin, in the *catalog* package.

 * **CatalogServiceImpl**, implements ICatalogService

This is the entry point, from a DI point of view. Provides method to access and manage the catalogs.
For now, there is only one catalog : mainCatalog.

 * **PadreCatalog**, implements ICatalog

This is the main catalog, historically called *layertree*.
It gets the layertree JSON data from the online geocatalog and transforms it in a hierarchy of INodes implementations : FolderNodes for folders (no associated layers, not checkable) and WmsNodes for WMS layers.
ChartNodes will come but are not implemented yet.

## Preferences : 

The following preferences settings are used by the Catalog Service and can be overridden in the general settings.ini config file.

* **fr.pigeo.rimap.rimaprcp.core.services.catalog/childrentag=children** : used in the JSON parsing. This is the tag the parser will look for children nodes (an array)
* **fr.pigeo.rimap.rimaprcp/project.baseurl=http\://sn-risk.pigeo.fr/sn-risk-gn2_10/** : the online Padre catalog base URL
* **fr.pigeo.rimap.rimaprcp/project.services.layertree=srv/fre/pigeo.layertree.get** : the relative path to the layertree service on the online Padre catalog. Combined with project.baseurl, it should give the correct URL to get the layertree as a JSON dataset.
* **fr.pigeo.rimap.rimaprcp/web.usage.level=1** : defines the web usage policy. For now, 2 values are recognized : 
  * 9 means priority to web : gets from the web except when not connected
  * 1 means priority to cache : gets the data (layertree, WMS capabilities) from the cache whenever possible
* **fr.pigeo.rimap.rimaprcp/catalog.metadata_relpath=/apps/geoportal/index.html?uuid=** : used by the WmsNode to provide a link to the related metadata (when a metadata UUID is provided)
* **fr.pigeo.rimap.rimaprcp/catalog.metadata_xml_relpath=/srv/eng/xml.metadata.get?uuid=** : similar. Not used for now. (link to the metadata as XML)
* **fr.pigeo.rimap.rimaprcp/catalog.wms_getlegend_relpath=REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&LAYER=** : used by the WmsNode to provide a link to the related legend (on the WMS server)

## Dependencies :

The Catalog Service uses the **Core WMS Service ** and the **Core Resource Service** for resource access, which in turn uses the **Core Security Service**, which itself uses the **Core Session Service**.
You may want to look also at the preferences used by those services as they will also have an influence on the way the Catalog Service works. 