package fr.pigeo.rimap.rimaprcp.core.services.resource.internal;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;

public class ResourceServiceImpl implements IResourceService {

	@Inject
	ISecureResourceService secureResourceService;

	@Inject
	Logger logger;

	@Inject
	@Named(RimapConstants.RIMAP_CACHE_PATH_CONTEXT_NAME)
	String cachePath;

	@Inject
	@Optional
	CloseableHttpClient httpClient;

	@Override
	public byte[] getResource(String url, int web_usage_level) {
		return this.getResource(url, "", UrlToFilename(url), web_usage_level);
	}

	@Override
	public byte[] getResourceFromURL(String url) {
		url = cleanURL(url);
		return getResourceFromURL(url, false);
	}

	@Override
	public byte[] getResourceFromFile(String url) {
		url = cleanURL(url);
		return getResourceFromFile(url, false);
	}

	@Override
	public byte[] getResourceFromURL(String url, boolean isFallback) {
		return this.getResourceFromURL(url,"", UrlToFilename(url),isFallback);
	}

	@Override
	public byte[] getResourceFromFile(String url, boolean isFallback) {
		return this.getResourceFromFile(url, "", UrlToFilename(url), isFallback);
	}

	@Override
	public String cleanURL(String url) {
		return url.replaceAll("(?<!(http:|https:))//", "/");
	}

	private static String UrlToFilename(String url) {
		String filename = url.substring(url.indexOf("://") + 3);
		filename = filename.replaceAll("\\W", "_");
		return filename;
	}

	@Override
	public byte[] getResource(String url, String category, String name, int web_usage_level) {
		if (cachePath == null) {
			return null;
		}
		url = cleanURL(url);
		if (web_usage_level > 1 || !secureResourceService.isResourceAvailable(cachePath, category, name)) {
			//logger.info("Should load " + url + " from URL");
			return getResourceFromURL(url, category, name);
		} else {
			// Load from file
			//logger.info("Should load " + url + " from file");
			return getResourceFromFile(url, category, name);
		}
	}

	@Override
	public byte[] getResourceFromURL(String url, String category, String name) {
		return this.getResourceFromURL(url, category, name, false);
	}

	@Override
	public byte[] getResourceFromFile(String url, String category, String name) {
		return this.getResourceFromFile(url, category, name, false);
	}

	@Override
	public byte[] getResourceFromURL(String url, String category, String name, boolean isFallback) {
		byte[] out=null;
		try {
			URL _url = new URL(url);
			if (httpClient != null) {
				HttpGet httpget = new HttpGet(url);
				CloseableHttpResponse response = httpClient.execute(httpget);
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						out = EntityUtils.toByteArray(entity);
					}
				} finally {
					response.close();
				}
			} else {
				// Other method. No authentification is taken into account
				logger.warn("HttpClient is null. This shouldn't occur."
						+ "The application will not be able to access restricted resources."
						+ "Trying to load the resource another way...");
				out = IOUtils.toByteArray(_url);
			}
		} catch (IOException e) {
			String msg = e.getClass()
					.toString() + ": Couldn't load " + url + " from server.";
			if (isFallback) {
				logger.warn(msg);
				return null;
			} else {
				msg += " Trying to fallback on cached file.";
				logger.warn(msg);
				return this.getResourceFromFile(url, category, name, true);
			}
		}
		// save it on disk (in cache location)
		
		secureResourceService.setResource(out, cachePath, category, name);
		return out;
	}

	@Override
	public byte[] getResourceFromFile(String url, String category, String name, boolean isFallback) {
		byte[] out = secureResourceService.getResourceAsByteArray(cachePath, category, name);
		if (out == null && !isFallback) {
			// then we try to load it from the web
			out = getResourceFromURL(url, category, name, true);
		}
		return out;
	}

	@Override
	public boolean deleteResource(String category, String name) {
		return secureResourceService.deleteResource(cachePath, category, name);
	}

	@Override
	public boolean deleteResource(String url) {
		return secureResourceService.deleteResource(cachePath, "", UrlToFilename(url));
	}

	@Override
	public void deleteResources(String category, String regex, boolean recursive) {
		secureResourceService.deleteResources(cachePath, category, regex, recursive);
	}

	@Override
	public void deleteResources(String regex, boolean recursive) {
		secureResourceService.deleteResources(cachePath, regex, recursive);
	}

	@Override
	public void deleteResourcesListed(String category, List<String> resourcesToDelete) {
		secureResourceService.deleteResourcesListed(cachePath, category, resourcesToDelete);
	}

	@Override
	public void deleteResourcesNotListed(String category, List<String> resourcesToKeep) {
		secureResourceService.deleteResourcesNotListed(cachePath, category, resourcesToKeep);
	}

}
