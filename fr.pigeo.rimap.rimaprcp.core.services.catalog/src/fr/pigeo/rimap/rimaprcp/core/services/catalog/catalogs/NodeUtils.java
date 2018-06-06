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

}
