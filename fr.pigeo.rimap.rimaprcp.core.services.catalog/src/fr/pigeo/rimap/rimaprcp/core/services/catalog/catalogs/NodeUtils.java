package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;

public class NodeUtils {
	public static Image getImage(String path) {
		Bundle bundle = FrameworkUtil.getBundle(NodeUtils.class);
		URL url = FileLocator.find(bundle, new Path(path), null);

		ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
		return imageDescr.createImage();
	}

	public static boolean isValid(JsonNode node, LayerType layerType) {
		if (node == null) {
			// System.out.println("ERROR: error parsing JsonNode in " +
			// this.getClass().getName());
			return false;
		}
		if (node.has("type") && !node.get("type").asText().equalsIgnoreCase(layerType.toString())) {
			// System.out.println("ERROR: wrong node type encountered while
			// parsing JsonNode in "
			// + this.getClass().getName() + ".(type is " +
			// node.get("type").asText() + ")");
			return false;
		}
		return true;
	}

	public static Date parseDate(JsonNode node, String tag) {
		Date date = null;
		if (node.has(tag)) {
			// parsing lastchange date as Date
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String datestr = node.get(tag).asText();
			try {
				date = df.parse(datestr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return date;
	}

	public static String parseString(JsonNode node, String tag, String defaultValue) {
		String out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asText();
		return out;
	}

	public static boolean parseBool(JsonNode node, String tag, boolean defaultValue) {
		boolean out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asBoolean(defaultValue);
		return out;
	}

	public static int parseInt(JsonNode node, String tag, int defaultValue) {
		int out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asInt(defaultValue);
		return out;
	}

	public static Double parseDouble(JsonNode node, String tag, Double defaultValue) {
		Double out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asDouble(defaultValue);
		return out;
	}

}
