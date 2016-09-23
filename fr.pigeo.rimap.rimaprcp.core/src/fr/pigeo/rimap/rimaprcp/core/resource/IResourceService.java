package fr.pigeo.rimap.rimaprcp.core.resource;

public interface IResourceService {

	public byte[] getResource(String url, int web_usage_level);
	
	/**
	 * getResource (String url, String category, int web_usage_level)
	 * @param url				URL of the resource
	 * @param category			structured like a folder structure. Defines the storage place in the cache folder. Avoids putting it all at the root of cache folder
	 * @param web_usage_level	see preferences
	 * @return
	 */
	public byte[] getResource(String url, String category, int web_usage_level);

	public byte[] getResourceFromURL(String url);
	
	public byte[] getResourceFromURL(String url, String category);	

	public byte[] getResourceFromFile(String url);

	public byte[] getResourceFromFile(String url, String category);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromURL method if this one fails)
	 * @return
	 */
	byte[] getResourceFromFile(String url, boolean isFallback);

	byte[] getResourceFromFile(String url, String category, boolean isFallback);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromFile method if this one fails)
	 * @return
	 */

	byte[] getResourceFromURL(String url, boolean isFallback);

	byte[] getResourceFromURL(String url, String category, boolean isFallback);

	public String cleanURL(String url);
}
