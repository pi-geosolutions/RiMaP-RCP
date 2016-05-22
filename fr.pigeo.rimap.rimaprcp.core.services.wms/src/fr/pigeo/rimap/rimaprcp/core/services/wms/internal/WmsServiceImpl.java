package fr.pigeo.rimap.rimaprcp.core.services.wms.internal;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import fr.pigeo.rimap.rimaprcp.core.services.wms.server.ServerCapability;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class WmsServiceImpl implements IWmsService {

	Map<String, ServerCapability> serverCapabilitiesList = new HashMap();
	Map<String, Thread> threadsList = new HashMap();

	@Override
	public void registerServerCapability(String url) {
		url = cleanURL(url);
		asyncLoadServerCapabilities(url);
	}

	@Override
	public WMSCapabilities getServerCapabilities(String url) {
		url = cleanURL(url);

		ServerCapability capability = serverCapabilitiesList.get(url.toLowerCase());
		if (capability == null) {
			// Else we create the capability object and add it to the hash
			// System.out.println("Adding capabilities for server "+url+" ");
			try {
				URI wmsUri = new URI(url);
				WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
				caps.parse();
				capability = new ServerCapability(url, caps);
				serverCapabilitiesList.put(url.toLowerCase(), capability);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return capability.getCapabilities();
	}

	/**
	 * load the capabilities asynchronously
	 * 
	 * @param url
	 * @return
	 */
	private void asyncLoadServerCapabilities(final String url) {
		ServerCapability capability = serverCapabilitiesList.get(url.toLowerCase());
		if (capability.getCapabilities() != null) {
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
						URI wmsUri = new URI(url);
						WMSCapabilities caps = WMSCapabilities.retrieve(wmsUri);
						caps.parse();
						ServerCapability c = new ServerCapability(url, caps);
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

	public static String cleanURL(String url) {
		return url.replaceAll("(?<!(http:|https:))//", "/");
	}
}
