package fr.pigeo.rimap.rimaprcp.mapservers;

import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class ServerCapability {
	private String type="wms";
	private String uri;
	private WMSCapabilities capabilities;

	public ServerCapability(String path, WMSCapabilities caps) {
		this.uri = path;
		this.capabilities=caps;
	}

	public String getType() {
		return type;
	}

	public String getUri() {
		return uri;
	}

	public WMSCapabilities getCapabilities() {
		return capabilities;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ServerCapability) {
			ServerCapability c = (ServerCapability) o;
			if (c.getUri().equalsIgnoreCase(this.getUri()))
				return true;
		}
		//else
		return false;
	}

	@Override
	public String toString() {
		String str = "Server capabilities : server type="+this.type+", uri="+this.uri+", loaded="+(this.capabilities!=null);
		return str;
	}

}
