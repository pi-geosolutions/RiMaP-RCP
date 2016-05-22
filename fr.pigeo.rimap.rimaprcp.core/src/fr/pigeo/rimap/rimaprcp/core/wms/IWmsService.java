package fr.pigeo.rimap.rimaprcp.core.wms;

import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public interface IWmsService {
	public void registerServerCapability(String url);
	public WMSCapabilities getServerCapabilities(String url);
}
