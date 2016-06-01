# RiMaP-RCP Core WMS Resource Service

The Core WMS Resource Service provides implementations for the IWmService interface found in core plugin, in the *wms* package.

 * **WmsServiceImpl**, implements IWmsService

This is the entry point, from a DI point of view. Provides method to access to WMS server functions (mostly Capabilities for now).

WMS Capabilities are used to generate the WorldWind layers from the Catalog's nodes. This allows to get most of the information directly from the WMS server (as opposed to Padre1 online catalog which didn't use them, in order to reduce bandwidth usage).

As WMS Capabilities can be rather large documents, they are loading in the background, i.e. the interface continues to load meanwhile recovering the capabilities using the available bandwidth. This results in a fast UI load while still getting all of the Capabilities information. Most likely, those Capabilities are finished loading when the user will actually need them.

*Note:* for layers that should be displayed at startup, background-loading of their capabilities will not be possible. This means the UI will have to wait for them to load before being able to display the catalog's content. So be careful of this limitation, and whenever possible try to use WMS URLs that will give birth to Capabilities documents the smaller possible (e.g. don't use global WMS URLs when you can use namespace-specific URLs : ~~http://gm-risk.pigeo.fr/geoserver-prod/wms~~ is wrong, you should use  **http://gm-risk.pigeo.fr/geoserver-prod/gm/wms**)
 
WMS Capabilities are stored in local cache, so you won't load them at each application startup : they will be loaded once only.

## Preferences : 

The following preferences settings are used by the WmsServiceImpl and can be overridden in the general settings.ini config file.


* **fr.pigeo.rimap.rimaprcp/web.usage.level=1** : defines the web usage policy. For now, 2 values are recognized : 
  * 9 means priority to web : gets from the web except when not connected
  * 1 means priority to cache : gets the data (layertree, WMS capabilities) from the cache whenever possible
  Web priority means you are always sure to get up-to-date data, but at a higher bandwidth usage cost. Cache priority means you will have low bandwidth usage, but will sometimes fail to get updates in the model.
  
## Dependencies :

The Core WMS Resource Service uses the **Core Session Service**.
You may want to look also at the preferences used by those services as they will also have an influence on the way the Resource Service works. 