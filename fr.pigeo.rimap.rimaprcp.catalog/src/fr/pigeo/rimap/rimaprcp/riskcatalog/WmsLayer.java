package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

public class WmsLayer extends AbstractLayer {
	private WMSTiledImageLayer layer;
    protected static class LayerInfo
    {
        protected WMSCapabilities caps;
        protected AVListImpl params = new AVListImpl();

        protected String getTitle()
        {
            return params.getStringValue(AVKey.DISPLAY_NAME);
        }

        protected String getName()
        {
            return params.getStringValue(AVKey.LAYER_NAMES);
        }

        protected String getAbstract()
        {
            return params.getStringValue(AVKey.LAYER_ABSTRACT);
        }
    }

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

	static private String wmsImagePath = "icons/wms.png";
	static private String checkedImagePath = "icons/16px-checkbox-checked.png";
	static private String uncheckedImagePath = "icons/16px-checkbox-unchecked.png";
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
			/*WMSLayerCapabilities layerCaps = caps.getLayerByName(this.layers);
			System.out.println(layerCaps.getName());*/
			/*
			
			WMSLayerCapabilities layerCaps = caps.getLayerByName(this.getName());
			Set<WMSLayerStyle> styles = layerCaps.getStyles();
			WMSLayerStyle style = null;
			if (styles != null && styles.size() != 0) {
				style= styles.toArray(new WMSLayerStyle[0])[0];
			}
			LayerInfo layerInfo = createLayerInfo(caps, layerCaps, style);
			AVList layerParams = new AVListImpl();
	        layerParams.setValue(AVKey.LAYER_NAMES, layerCaps.getName());
			this.layer = new WMSTiledImageLayer(caps, null);*/
			AVList layerParams = new AVListImpl();
			System.out.println(this.layers);
	        layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
	        layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
	        try {
			this.layer = new WMSTiledImageLayer(caps, layerParams);
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

    protected LayerInfo createLayerInfo(WMSCapabilities caps, WMSLayerCapabilities layerCaps, WMSLayerStyle style)
    {
        // Create the layer info specified by the layer's capabilities entry and the selected style.

        LayerInfo linfo = new LayerInfo();
        linfo.caps = caps;
        linfo.params = new AVListImpl();
        linfo.params.setValue(AVKey.LAYER_NAMES, layerCaps.getName());
        if (style != null)
            linfo.params.setValue(AVKey.STYLE_NAMES, style.getName());
        String abs = layerCaps.getLayerAbstract();
        if (!WWUtil.isEmpty(abs))
            linfo.params.setValue(AVKey.LAYER_ABSTRACT, abs);

        linfo.params.setValue(AVKey.DISPLAY_NAME, this.getName());

        return linfo;
    }

}
