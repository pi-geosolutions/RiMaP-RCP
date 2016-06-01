# RiMaP-RCP Core Secure Resource Service

The Core Secure Resource Service provides implementations for the ISecureResourceService interface found in core plugin, in the *security* package.

 * **SecureFileServiceImpl**, implements ISecureResourceService

This is the entry point, from a DI point of view. Provides method to get file-based resources.
 
* If the current session is a guest session, it will be simple file access (read/write).
* If a user is logged in, the user session (see Session Service) is used to provide access to the resources : the cached files are stored in a subfolder named after the user's name. The files in this folder are encrypted using the user's password. If the provided password is erroneous, it will not be able to decrypt the files. The application will then degrade to using cache data from the guest session, if any available. This behaviour can be disabled by setting the gracefully_fallback_on_anonymous setting to false. 

## Preferences : 

The following preferences settings are used by the SecureFileService and can be overridden in the general settings.ini config file.

* **fr.pigeo.rimap.rimaprcp.core.services.security/gracefully_fallback_on_anonymous=true** : determines if the application should look for appropriate files in the guest session if the provided password fails to decrypt the encrypted files.

## Dependencies :

The Core Secure Resource Service uses the **Core Session Service**.
You may want to look also at the preferences used by those services as they will also have an influence on the way the Resource Service works. 