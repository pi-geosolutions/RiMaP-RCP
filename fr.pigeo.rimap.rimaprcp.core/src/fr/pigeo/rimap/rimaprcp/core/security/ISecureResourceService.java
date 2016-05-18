package fr.pigeo.rimap.rimaprcp.core.security;

import java.io.InputStream;

/**
 * Helps manage file resources possibly stored securely using encryption.
 * Encryption uses the current
 * {@link fr.pigeo.rimap.rimaprcp.core.security.Session Session} if available
 * (if not, anonymous usage is supposed, then no encryption is done).
 * By the way, provides some caching mechanism for those files.
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public interface ISecureResourceService {

	public byte[] getResourceAsByteArray(String resourcePath, String resourceName);

	public String getResourceAsString(String resourcePath, String resourceName);

	public InputStream getResourceAsStream(String resourcePath, String resourceName);

	public boolean setResource(byte[] input, String resourcePath, String resourceName);

	public boolean setResource(String input, String resourcePath, String resourceName);

	public boolean setResource(InputStream input, String resourcePath, String resourceName);

	public boolean isResourceEncrypted(String resourcePath, String resourceName);

	public boolean currentSessionCanDecrypt(String resourcePath, String resourceName);
	
	public boolean isResourceAvailable(String resourcePath, String resourceName);
}
