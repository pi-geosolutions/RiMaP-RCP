# RiMaP-RCP Core Session Service

The Core Session Service provides an implementation for the ISessionService interface found in core plugin, in the *security* package.

 * **DefaultSessionServiceImpl**, implements ISessionService

   This is the entry point, from a DI point of view. Provides access methods to the current Session. 

   A Session is an object storing information about the session : username, password, validity level (validated against the online padre platform's login service). 

   If the current session is not set, then it opens a Login Dialog to create one : the user can choose between continuing as guest (no credentials provided) or to provide credentials. The credentials are checked against the online padre platform's login service if the internet connection is up. If internet (or the service) is down, it will propose to continue on a local session. 

   *Note:* No local validation is performed. The resource service will simply try to access the files using the provided credentials. If they are wrong, it will fallback on the guest cached files. 


## Preferences : 

The following preferences settings are used by the SecureFileService and can be overridden in the general settings.ini config file.

* **fr.pigeo.rimap.rimaprcp.core.services.session/services.login.relpath=j_spring_security_check** : login service relative address of the online Padre platform's 
* **fr.pigeo.rimap.rimaprcp.core/web.connect.timeout=5** : connection timeouts for web access
* **fr.pigeo.rimap.rimaprcp.core/web.read.timeout=20** : connection timeouts for web access