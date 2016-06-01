# RiMaP-RCP Core Resource Service

The Resource Service provides implementations for the IResourceService interface found in core plugin, in the *resource* package.

 * **ResourceServiceImpl**, implements IResourceService

This is the entry point, from a DI point of view. Provides method to get resources. One should not have to worry about of it gets the resources.
Basically, URL-related resources wll be directly recovered by the service, while file-related ones will be delegated to the Secure Resource Service implementation.
Depending on the web usage level, the priority will be given either to URL or file-based resources.

## Preferences : 

The following preferences settings are used by the Resource Service and can be overridden in the general settings.ini config file.

* **fr.pigeo.rimap.rimaprcp.core/web.usage.level=1** (used indirectly) : defines the web usage policy. For now, 2 values are recognized : 
  * 9 means priority to web : gets from the web except when not connected
  * 1 means priority to cache : gets the data (layertree, WMS capabilities) from the cache whenever possible
  
* **fr.pigeo.rimap.rimaprcp.core/cache.rootname=Padre** (used indirectly) : used in the definition of the Cache full path. This is the name of the root padre cache folder, set on the same level as WorldWind cache folder (the actual full path depends on how WorldWind determines its cache full path)

## Dependencies :

The Core Resource Service uses the **Core Security Service**, which itself uses the **Core Session Service**.
You may want to look also at the preferences used by those services as they will also have an influence on the way the Resource Service works. 