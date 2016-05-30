package fr.pigeo.rimap.rimaprcp.core.resource;

public interface IResourceService {

	public byte[] getResource(String url, int web_usage_level);

	public byte[] getResourceFromURL(String url);

	public byte[] getResourceFromFile(String url);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromURL method if this one fails)
	 * @return
	 */
	byte[] getResourceFromFile(String url, boolean isFallback);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromFile method if this one fails)
	 * @return
	 */

	byte[] getResourceFromURL(String url, boolean isFallback);

	public String cleanURL(String url);
}
