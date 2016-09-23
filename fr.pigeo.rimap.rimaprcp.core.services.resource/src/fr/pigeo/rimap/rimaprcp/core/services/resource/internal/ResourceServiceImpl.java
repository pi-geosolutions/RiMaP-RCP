package fr.pigeo.rimap.rimaprcp.core.services.resource.internal;

import java.io.IOException;
import java.net.URL;

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
		return this.getResource(url, "", web_usage_level);
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
		return this.getResourceFromURL(url,"",isFallback);
	}

	@Override
	public byte[] getResourceFromFile(String url, boolean isFallback) {
		byte[] out = secureResourceService.getResourceAsByteArray(cachePath, UrlToFilename(url));
		if (out == null && !isFallback) {
			// then we try to load it from the web
			out = getResourceFromURL(url, true);
		}
		return out;
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
	public byte[] getResource(String url, String category, int web_usage_level) {
		if (cachePath == null) {
			return null;
		}
		url = cleanURL(url);
		if (web_usage_level > 1 || !secureResourceService.isResourceAvailable(cachePath, category, UrlToFilename(url))) {
			logger.info("Should load " + url + " from URL");
			return getResourceFromURL(url, category);
		} else {
			// Load from file
			logger.info("Should load " + url + " from file");
			return getResourceFromFile(url, category);
		}
	}

	@Override
	public byte[] getResourceFromURL(String url, String category) {
		return this.getResourceFromURL(url, category, false);
	}

	@Override
	public byte[] getResourceFromFile(String url, String category) {
		return this.getResourceFromFile(url, category, false);
	}

	@Override
	public byte[] getResourceFromURL(String url, String category, boolean isFallback) {
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
				return this.getResourceFromFile(url, category, true);
			}
		}
		// save it on disk (in cache location)
		
		secureResourceService.setResource(out, cachePath, category, UrlToFilename(url));
		return out;
	}

	@Override
	public byte[] getResourceFromFile(String url, String category, boolean isFallback) {
		byte[] out = secureResourceService.getResourceAsByteArray(cachePath, category, UrlToFilename(url));
		if (out == null && !isFallback) {
			// then we try to load it from the web
			out = getResourceFromURL(url, category, true);
		}
		return out;
	}

}
