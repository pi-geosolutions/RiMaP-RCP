package fr.pigeo.rimap.rimaprcp.core.services.wms.internal;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;

import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.services.wms.server.ServerCapability;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.CapabilitiesRequest;

public class WmsServiceImpl implements IWmsService {

	@Inject
	IResourceService resourceService;

	@Inject
	Logger logger;

	int web_usage_level = 1;

	Map<String, ServerCapability> serverCapabilitiesList = new HashMap();
	Map<String, Thread> threadsList = new HashMap();

	@Override
	public void registerServerCapability(String url) {
		url = resourceService.cleanURL(url);
		asyncLoadServerCapabilities(url);
	}

	@Override
	public WMSCapabilities getServerCapabilities(String url) {
		url = resourceService.cleanURL(url);

		ServerCapability capability = serverCapabilitiesList.get(url.toLowerCase());
		if (capability == null) {
			capability=makeCapability(url);
		}
		return capability.getCapabilities();
	}

	private ServerCapability makeCapability(String url) {
		url = resourceService.cleanURL(url);
		ServerCapability capability = null;
		// we create the capability object and add it to the hash
		// System.out.println("Adding capabilities for server "+url+" ");
		try {
			URI wmsUri = new URI(url);

			// WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
			try {

				CapabilitiesRequest request = new CapabilitiesRequest(wmsUri);

				String address = request.getUri().toURL().toString();
				System.out.println(request.getUri().toURL().toString());
				byte[] b;
				if (resourceService != null) {
					logger.info("Recovering getCapabilities using ResourceService plugin");
					b = resourceService.getResource(address, web_usage_level);
				} else {
					logger.info("ResourceService plugin unavailable. Recovering getCapabilities directly from URL");
					b = IOUtils.toByteArray(request.getUri().toURL());
				}
				WMSCapabilities caps = new WMSCapabilities(new ByteArrayInputStream(b));

				// caps = new WMSCapabilities(request);
				System.out.println(caps.getClass());
				caps.parse();
				capability = new ServerCapability(url, caps);
				serverCapabilitiesList.put(url.toLowerCase(), capability);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
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
						serverCapabilitiesList.put(url.toLowerCase(), c);

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
}
