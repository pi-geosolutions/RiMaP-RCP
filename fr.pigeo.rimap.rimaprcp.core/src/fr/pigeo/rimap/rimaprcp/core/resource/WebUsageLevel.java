package fr.pigeo.rimap.rimaprcp.core.resource;

/**
 * defines available web usage level values
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class WebUsageLevel {
	public static int OFFLINE = 0;
	//tries first to get locally, and if not available tries on the web
	public static int PRIORITY_LOCAL = 1;
	//tries first on the web, and if not available (not connected) tries locally
	public static int PRIORITY_WEB = 9;
	
}
