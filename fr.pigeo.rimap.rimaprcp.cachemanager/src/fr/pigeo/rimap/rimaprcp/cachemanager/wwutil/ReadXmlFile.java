package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;

import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWXML;

public class ReadXmlFile {

/*	protected final String xmlcacheRootPath;

public ReadXmlFile(String xmlcacheRootPath){

	
protected static Document getXmlDocument(String path)
{
    return WWXML.openDocumentFile(xmlcacheRootPath, null);
}


public static LatLon getLatLon(Element context, String path, XPath xpath)
{
    if (context == null)
    {
        String message = Logging.getMessage("nullValue.ContextIsNull");
        Logging.logger().severe(message);
        throw new IllegalArgumentException(message);
    }

    try
    {
        Element el = path == null ? context : getElement(context, path, xpath);
        if (el == null)
            return null;

        String units = getText(el, "@units", xpath);
        Double lat = getDouble(el, "@latitude", xpath);
        Double lon = getDouble(el, "@longitude", xpath);

        if (lat == null || lon == null)
            return null;

        if (units == null || units.equals("degrees"))
            return LatLon.fromDegrees(lat, lon);

        if (units.equals("radians"))
            return LatLon.fromRadians(lat, lon);

        // Warn that units are not recognized
        String message = Logging.getMessage("XML.UnitsUnrecognized", units);
        Logging.logger().warning(message);

        return null;
    }
    catch (NumberFormatException e)
    {
        String message = Logging.getMessage("generic.ConversionError", path);
        Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
        return null;
    }
}


}*/
}