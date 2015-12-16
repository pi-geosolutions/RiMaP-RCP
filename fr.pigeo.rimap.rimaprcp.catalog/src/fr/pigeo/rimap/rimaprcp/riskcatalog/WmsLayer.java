package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class WmsLayer extends AbstractLayer {
	private LayerType type = LayerType.WMS;
	private String id;
	private String name = "new wms layer";
	private String style = "wms";
	private String comments = "";
	private Date lastchanged;
	private Integer weight = 1;
	private Double opacity = 1.0;
	private String url;
	private String layers;
	private String legendurl;
	private String metadata_uuid;
	private String format = "image/png";
	private boolean tiled = true;
	private boolean queryable = true;
	private boolean checked = false;
	
	static private String wmsImagePath="icons/wms.png";
	static private Image wmsImage; 

	public WmsLayer(AbstractLayer parent) {
		this.parent = parent;
	}

	public WmsLayer(AbstractLayer parent, JsonNode node) {
		this.parent = parent;
		this.loadFromJson(node);
	}

	@Override
	public void loadFromJson(JsonNode node) {
		if (node == null) {
			System.out.println("ERROR: error parsing JsonNode in " + this.getClass().getName());
			return;
		}
		if (!node.get("type").asText().equalsIgnoreCase(LayerType.WMS.toString())) {
			System.out.println("ERROR: wrong node type encountered while parsing JsonNode in "
					+ this.getClass().getName() + ".(type is " + node.get("type").asText() + ")");
			return;
		}
			this.id = this.parseString(node, "id", null);
			this.name = this.parseString(node, "text", this.name);
			this.style = this.parseString(node, "cls", LayerType.WMS.toString());
			this.comments= this.parseString(node, "qtip", this.comments);
			this.lastchanged = this.parseDate(node, "lastchanged");
			this.weight = this.parseInt(node, "weight", this.weight);
			this.opacity = this.parseDouble(node, "opacity", this.opacity);
			this.url= this.parseString(node, "url", this.url);
			this.layers = this.parseString(node, "layers", "");
			this.legendurl = this.parseString(node, "legend", "");
			this.metadata_uuid = this.parseString(node, "uuid", "");
			this.format = this.parseString(node, "format", this.format);
			this.tiled = this.parseBool(node, "TILED", this.tiled);
			this.queryable = this.parseBool(node, "queryable", this.queryable);
			this.checked = this.parseBool(node, "checked", this.checked);
		//System.out.println("loaded WMS layer parameters " + this.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Image getImage() {
		if (WmsLayer.wmsImage==null) {
			Bundle bundle = FrameworkUtil.getBundle(WmsLayer.class);
			URL url = FileLocator.find(bundle, new Path(WmsLayer.wmsImagePath), null);
			ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
			WmsLayer.wmsImage = imageDcr.createImage();
		}
		return WmsLayer.wmsImage;
	}

}
