package fr.pigeo.rimap.rimaprcp.core.resource;

import java.util.List;

public interface IResourceService {

	public byte[] getResource(String url, int web_usage_level);
	
	/**
	 * getResource (String url, String category, int web_usage_level)
	 * @param url				URL of the resource
	 * @param category			structured like a folder structure. Defines the storage place in the cache folder. Avoids putting it all at the root of cache folder
	 * @param web_usage_level	see preferences
	 * @return
	 */
	public byte[] getResource(String url, String category, String name, int web_usage_level);

	public byte[] getResourceFromURL(String url);
	
	public byte[] getResourceFromURL(String url, String category, String name);	

	public byte[] getResourceFromFile(String url);

	public byte[] getResourceFromFile(String url, String category, String name);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromURL method if this one fails)
	 * @return
	 */
	byte[] getResourceFromFile(String url, boolean isFallback);

	byte[] getResourceFromFile(String url, String category, String name, boolean isFallback);

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromFile method if this one fails)
	 * @return
	 */

	byte[] getResourceFromURL(String url, boolean isFallback);

	byte[] getResourceFromURL(String url, String category, String name, boolean isFallback);

	public String cleanURL(String url);

	public boolean deleteResource(String category, String name);
	public boolean deleteResource(String URL);

	public void deleteResources(String category, String regex, boolean recursive);
	public void deleteResources(String regex, boolean recursive);

	public void deleteResourcesListed(String category, List<String> resourcesToDelete);
	public void deleteResourcesNotListed(String category, List<String> resourcesToKeep);
}
