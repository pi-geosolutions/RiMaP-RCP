package fr.pigeo.rimap.rimaprcp.core.services.resource.internal;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
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
	@Named(RimapConstants.RIMAP_CACHE_PATH)
	String cachePath;

	@Override
	public byte[] getResource(String url, int web_usage_level) {
		if (cachePath == null) {
			return null;
		}
		url = cleanURL(url);
		if (web_usage_level > 1 || !secureResourceService.isResourceAvailable(cachePath, UrlToFilename(url))) {
			logger.info("Should load " + url + " from URL");
			return getResourceFromURL(url);
		} else {
			// Load from file
			logger.info("Should load " + url + " from file");
			return getResourceFromFile(url);
		}
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

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromFile method if this one fails)
	 * @return
	 */

	private byte[] getResourceFromURL(String url, boolean isFallback) {
		byte[] out;
		try {
			URL _url = new URL(url);
			out = IOUtils.toByteArray(_url);
		} catch (IOException e) {
			String msg = e.getClass().toString() + ": Couldn't load " + url + " from server.";
			if (isFallback) {
				logger.warn(msg);
				return null;
			} else {
				msg += " Trying to fallback on cached file.";
				logger.warn(msg);
				return this.getResourceFromFile(url, true);
			}
		}
		// save it on disk (in cache location)
		// IOCacheUtil.store(lt, cacheDestination, pwd);
		secureResourceService.setResource(out, cachePath, UrlToFilename(url));
		return out;
	}

	/**
	 * 
	 * @param isFallback
	 *            Tells if this is already the fallback method (i.e. we won't
	 *            fallback on the getFromURL method if this one fails)
	 * @return
	 */
	private byte[] getResourceFromFile(String url, boolean isFallback) {
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
}
