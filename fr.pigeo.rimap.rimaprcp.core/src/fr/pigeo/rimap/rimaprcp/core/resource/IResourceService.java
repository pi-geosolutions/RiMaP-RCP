package fr.pigeo.rimap.rimaprcp.core.resource;

public interface IResourceService {

	public byte[] getResource(String url, int web_usage_level);
	public byte[] getResourceFromURL(String url);
	public byte[] getResourceFromFile(String url);
	public String cleanURL(String url);
}
