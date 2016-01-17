package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.URI;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.catalog.CatalogProperties;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

public class WmsLayer extends AbstractLayer {
	private RimapWMSTiledImageLayer layer;


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
	private String pq_layer;
	private boolean tiled = true;
	private boolean queryable = true;
	private boolean checked = false;

	static private String wmsImagePath = CatalogProperties.getProperty("wmslayer.imagepath");
	static private String checkedImagePath = CatalogProperties.getProperty("wmslayer.checkedimagepath");
	static private String uncheckedImagePath = CatalogProperties.getProperty("wmslayer.uncheckedimagepath");
	static private Image checkedImage;
	static private Image uncheckedImage;
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
		this.comments = this.parseString(node, "qtip", this.comments);
		this.lastchanged = this.parseDate(node, "lastchanged");
		this.weight = this.parseInt(node, "weight", this.weight);
		this.opacity = this.parseDouble(node, "opacity", this.opacity);
		this.url = this.parseString(node, "url", this.url);
		this.layers = this.parseString(node, "layers", "");
		this.legendurl = this.parseString(node, "legend", "");
		this.metadata_uuid = this.parseString(node, "uuid", "");
		this.format = this.parseString(node, "format", this.format);
		this.tiled = this.parseBool(node, "TILED", this.tiled);
		this.queryable = this.parseBool(node, "queryable", this.queryable);
		this.checked = this.parseBool(node, "checked", this.checked);
		this.pq_layer = this.parseString(node, "pq_layer", null);
		
		// System.out.println("loaded WMS layer parameters " + this.name);
	}

	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Image getImage() {/*
								 * if (WmsLayer.wmsImage==null) { Bundle bundle
								 * = FrameworkUtil.getBundle(WmsLayer.class);
								 * URL url = FileLocator.find(bundle, new
								 * Path(WmsLayer.wmsImagePath), null);
								 * ImageDescriptor imageDcr =
								 * ImageDescriptor.createFromURL(url);
								 * WmsLayer.wmsImage = imageDcr.createImage(); }
								 */
		if (this.checked) {
			if (WmsLayer.checkedImage == null) {
				Bundle bundle = FrameworkUtil.getBundle(WmsLayer.class);
				URL url = FileLocator.find(bundle, new Path(WmsLayer.checkedImagePath), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				WmsLayer.checkedImage = imageDcr.createImage();
			}
			return WmsLayer.checkedImage;
		} else {
			if (WmsLayer.uncheckedImage == null) {
				Bundle bundle = FrameworkUtil.getBundle(WmsLayer.class);
				URL url = FileLocator.find(bundle, new Path(WmsLayer.uncheckedImagePath), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				WmsLayer.uncheckedImage = imageDcr.createImage();

			}
			return WmsLayer.uncheckedImage;
		}
	}

	@Override
	public void setChecked() {
		this.checked = !this.checked;
	}

	@Override
	public void addToGlobe(WorldWindowGLCanvas wwd) {
		System.out.println("adding layer "+this.name+" to globe");
		if (this.layer == null) {
			System.out.println("first, creating layer");
			WMSCapabilities caps;

			try {
				URI wmsUri = new URI(this.url);
				caps = WMSCapabilities.retrieve(wmsUri);
				caps.parse();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			AVList layerParams = new AVListImpl();
			System.out.println(this.layers);
	        layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
	        layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
	        try {
	        	this.layer = new RimapWMSTiledImageLayer(caps, layerParams);
	        	this.layer.setName(this.name);
	        	this.layer.setParent(this);
	        	this.layer.setValue("isRimapLayer", true);
	        }
	        catch (Exception e) {
				e.printStackTrace();
				return;
	        }
		}
		LayerList layers = wwd.getModel().getLayers();
		this.layer.setEnabled(this.checked);
		if (this.checked)
        {
            if (!layers.contains(this.layer))
            {
                ApplicationTemplate.insertBeforePlacenames(wwd, this.layer);
            }
        }
        else
        {
            layers.remove(this.layer);
        }
		return;
	}

	public LayerType getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getComments() {
		return comments;
	}

	public Double getOpacity() {
		return opacity;
	}

	public String getUrl() {
		return url;
	}

	public String getLegendurl() {
		return legendurl;
	}

	public String getMetadata_uuid() {
		return metadata_uuid;
	}

	public boolean isQueryable() {
		return queryable;
	}

	public String getPq_layer() {
		return pq_layer;
	}
}
