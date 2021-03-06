package fr.pigeo.rimap.rimaprcp.core.services.wms.internal;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.services.log.Logger;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.services.wms.server.ServerCapability;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.CapabilitiesRequest;

public class WmsServiceImpl implements IWmsService {

	@Inject
	IResourceService resourceService;

	@Inject
	IPreferencesService prefService;

	@Inject
	Logger logger;

	private int web_usage_level = -1; // unset

	Map<String, ServerCapability> serverCapabilitiesList = new HashMap();
	Map<String, Thread> threadsList = new HashMap();

	private int getWebUsageLevel() {
		if (this.web_usage_level < 0) { // i.e. unset
			this.web_usage_level = prefService.getInt(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
					RimapConstants.WEB_USAGE_LEVEL_PREF_TAG, RimapConstants.WEB_USAGE_LEVEL_PREF_DEFAULT, null);
		}
		return this.web_usage_level;
	}

	@Override
	public void registerServerCapability(String url) {
		url = resourceService.cleanURL(url);
		asyncLoadServerCapabilities(url);
	}

	@Override
	public WMSCapabilities getServerCapabilities(String url, boolean reload) {
		url = resourceService.cleanURL(url);
		if (reload) {
			serverCapabilitiesList.remove(url.toLowerCase());
		}
		
		ServerCapability capability = serverCapabilitiesList.get(url.toLowerCase());
		if (capability == null) {
			capability = makeCapability(url, reload);
		}
		if (capability == null) {
			// means no way we can get it
			return null;
		}
		return capability.getCapabilities();
	}

	private ServerCapability makeCapability(String url) {
		return makeCapability(url, false);
	}

	private ServerCapability makeCapability(String url, boolean reload) {
		url = resourceService.cleanURL(url);
		ServerCapability capability = null;
		// we create the capability object and add it to the hash
		// System.out.println("Adding capabilities for server "+url+" ");
		try {
			URI wmsUri = new URI(url);

			// WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
			try {

				CapabilitiesRequest request = new CapabilitiesRequest(wmsUri);

				String address = request.getUri()
						.toURL()
						.toString();

				byte[] b;
				if (resourceService != null) {
					if (reload) {
						resourceService.deleteResource(address);
					}
					logger.info("Recovering getCapabilities using ResourceService plugin");
					b = resourceService.getResource(address, getWebUsageLevel());
				} else {
					logger.info("ResourceService plugin unavailable. Recovering getCapabilities directly from URL");
					b = IOUtils.toByteArray(request.getUri()
							.toURL());
				}
				WMSCapabilities caps = new WMSCapabilities(new ByteArrayInputStream(b));

				// caps = new WMSCapabilities(request);
				try {
					caps.parse();
					capability = new ServerCapability(url, caps);
					serverCapabilitiesList.put(url.toLowerCase(), capability);
				} catch (XMLStreamException e) {
					logger.error("XMLStreamException : could not retrieve capabilities from " + url);
					// we delete the corrupted resource
					resourceService.deleteResource(address);
					return null;

				}
			} catch (URISyntaxException | MalformedURLException e) {
				e.printStackTrace();
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return capability;
	}

	/**
	 * load the capabilities asynchronously
	 * 
	 * @param url
	 * @return
	 */
	private void asyncLoadServerCapabilities(final String url) {
		ServerCapability capability = serverCapabilitiesList.get(url.toLowerCase());
		if (capability != null) {
			return;
		}
		// else
		// we load it using an async thread. And we keep track of the threads in
		// order not to launch multiple threads for a identical URL
		Thread t = threadsList.get(url.toLowerCase());
		if (t == null) {
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ServerCapability c = makeCapability(url);
						if (c != null) {
							serverCapabilitiesList.put(url.toLowerCase(), c);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			threadsList.put(url.toLowerCase(), t);
		}
		if (!t.isAlive()) {
			t.start();
		}
	}
	
	public void reset() {
		serverCapabilitiesList.clear();
		threadsList.forEach((key, thread) -> thread.interrupt());
		threadsList.clear();
	}
}
