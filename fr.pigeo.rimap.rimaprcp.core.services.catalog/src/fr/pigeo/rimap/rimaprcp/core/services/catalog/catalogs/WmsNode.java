package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.worldwind.layers.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.PolygonQueryableParams;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class WmsNode extends AbstractNode implements ICheckableNode {
	private static String IMAGE_WMSICON = "icons/wms.png";
	private static String IMAGE_CHECKED = "icons/16px-checkbox-checked.png";
	private static String IMAGE_UNCHECKED = "icons/16px-checkbox-unchecked.png";
	private static String IMAGE_DISABLED = "icons/16px-checkbox-disabled.png";

	public static Image checkedImage;
	public static Image uncheckedImage;
	public static Image disabledImage;
	public static Image wmsImage;

	protected double detailhint = 0.1;

	protected LayerType type = LayerType.WMS;
	protected RimapWMSTiledImageLayer layer;
	protected INode parent = null;
	protected String id;
	protected String name = "unnamed wms layer";
	protected String style = "wms";
	protected String comments = "";
	protected Date lastchanged;
	protected Integer weight = 1;
	protected Double opacity = 1.0;
	protected String url;
	protected String layers;
	protected String legendurl;
	protected String metadata_uuid;
	protected String format = "image/png";
	protected PolygonQueryableParams polygonQueryParams;
	protected boolean tiled = true;
	protected boolean queryable = true;
	protected boolean checked = false;

	@Inject
	IPreferencesService prefsService;

	@Inject
	IEclipseContext context;

	@Inject
	IEventBroker eventBroker;

	// Custom injected resource
	@Inject
	PadreCatalogState catalogState;

	@Inject
	IWmsService wmsService;
	
	@Inject Logger logger;

	@Override
	public void loadFromJson(JsonNode node) {
		if (!NodeUtils.isValid(node, this.type)) {
			logger.error("ERROR: error parsing JsonNode in " + this.getClass()
					.getName());
			return;
		}
		this.id = NodeUtils.parseString(node, "id", null);
		this.name = NodeUtils.parseString(node, "text", this.name);
		this.style = NodeUtils.parseString(node, "cls", LayerType.WMS.toString());
		this.comments = NodeUtils.parseString(node, "qtip", this.comments);
		this.lastchanged = NodeUtils.parseDate(node, "lastchanged");
		this.weight = NodeUtils.parseInt(node, "weight", this.weight);
		this.opacity = NodeUtils.parseDouble(node, "opacity", this.opacity);
		this.url = NodeUtils.parseString(node, "url", this.url);
		this.layers = NodeUtils.parseString(node, "layers", "");
		this.legendurl = NodeUtils.parseString(node, "legend", "");
		this.metadata_uuid = NodeUtils.parseString(node, "uuid", "");
		this.format = NodeUtils.parseString(node, "format", this.format);
		this.tiled = NodeUtils.parseBool(node, "TILED", this.tiled);
		this.queryable = NodeUtils.parseBool(node, "queryable", this.queryable);
		this.checked = NodeUtils.parseBool(node, "checked", this.checked);
		// polygon query params
		String pq_layer = NodeUtils.parseString(node, "pq_layer", null);
		if (pq_layer != null) {
			int pq_bandnb = NodeUtils.parseInt(node, "pq_bandnb", 0);
			String pq_header = NodeUtils.parseString(node, "pq_header", "");
			int pq_round = NodeUtils.parseInt(node, "pq_round", 0);
			JsonNode n = node.get("pq_rastertype_fields");
			Iterator<Entry<String, JsonNode>> it = n.fields();
			List<String> pq_fields = new ArrayList();

			//System.out.println("    -> polygon query fields:");
			while (it.hasNext()) {
				Entry<String, JsonNode> entry = it.next();
				if (entry.getValue().asBoolean(false)) {
					pq_fields.add(entry.getKey());
					//System.out.println("        - "+entry.getKey());
				}
			}
			this.polygonQueryParams = new PolygonQueryableParams(pq_layer, pq_header, pq_bandnb, pq_round, pq_fields);
			
		}

		wmsService.registerServerCapability(this.url);

		if (catalogState != null) {
			if (this.checked) {
				catalogState.addCheckedNode(this);
			}
		} else {
			logger.error("catalogState context var is null");
		}

		if (prefsService != null) {
			this.detailhint = prefsService.getDouble(RimapConstants.WW_DEFAULT_PREFERENCE_NODE,
					RimapConstants.WW_DEFAULT_LAYER_DETAILSHINT, this.detailhint, null);
		}

	}

	@Override
	public INode getParent() {
		return this.parent;
	}

	@Override
	public void setParent(INode parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean hasLeaves() {
		return false;
	}

	@Override
	public List<INode> getLeaves() {
		return null;
	}

	@Override
	public boolean isRootNode() {
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Image getImage() {
		// return NodeUtils.getImage(WmsNode.IMAGE_WMSICON);
		if (!this.isAvailable()) {
			if (WmsNode.disabledImage == null) {
				WmsNode.disabledImage = NodeUtils.getImage(WmsNode.IMAGE_DISABLED);
			}
			return WmsNode.disabledImage;
		}
		if (this.checked) {
			if (WmsNode.checkedImage == null) {
				WmsNode.checkedImage = NodeUtils.getImage(WmsNode.IMAGE_CHECKED);
			}
			return WmsNode.checkedImage;
		} else {
			if (WmsNode.uncheckedImage == null) {
				WmsNode.uncheckedImage = NodeUtils.getImage(WmsNode.IMAGE_UNCHECKED);
			}
			return WmsNode.uncheckedImage;
		}
	}

	@Override
	public void setChecked(boolean check) {
		if (check == this.checked) {
			return;
		}
		this.checked = check;
		if (this.layer != null) {
			this.layer.setEnabled(check);
		}
		if (eventBroker != null) {
			eventBroker.post(RiMaPEventConstants.CHECKABLENODE_CHECKCHANGE, this);
		}
	}

	@Override
	public void toggleChecked() {
		setChecked(!this.checked);
	}

	@Override
	public void changeState() {
		toggleChecked();
	}

	@Override
	public boolean getChecked() {
		return checked;
	}

	@Override
	public Layer getLayer() {
		if (this.layer == null) {

			WMSCapabilities caps = wmsService.getServerCapabilities(this.url);
			if (caps==null) {
				return null;
			}

			AVList layerParams = new AVListImpl();
			// System.out.println(this.layers);
			layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
			layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
			layerParams.setValue(AVKey.TILE_WIDTH, 256);
			layerParams.setValue(AVKey.TILE_HEIGHT, 256);
			layerParams.setValue(AVKey.DETAIL_HINT, this.detailhint);
			layerParams.setValue(AVKey.FORMAT_SUFFIX, ".png");
			try {
				this.layer = new RimapWMSTiledImageLayer(caps, layerParams);
				this.layer.setName(this.name);
				this.layer.setParent(this);
				//redundant with previous...
				this.layer.setValue(RimapAVKey.LAYER_PARENTNODE, this);
				this.layer.setValue(AVKey.FORMAT_SUFFIX, ".png");
				this.layer.setValue(RimapAVKey.HAS_RIMAP_EXTENSIONS, true);
				if ((this.polygonQueryParams!=null) && (this.polygonQueryParams.isValid())) {
					this.layer.setValue(RimapAVKey.LAYER_ISPOLYGONQUERYABLE, true);
					this.layer.setValue(RimapAVKey.LAYER_POLYGONQUERYPARAMS, this.polygonQueryParams);
				} else {
					this.layer.setValue(RimapAVKey.LAYER_ISPOLYGONQUERYABLE, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.layer != null) {
			// Loading from caps may fail. In this case, layer, will still be
			// null
			layer.setEnabled(this.checked);
		}
		return this.layer;
	}
	/*
	 * Tries to make a layer from scratch, i.e. without the help of the capabilities. 
	 * Some layers are not published in the caps, but exist nonetheless
	 * Of course, it means we won't be able to pre-detect erroneous layers...
	 */
	public Layer guessLayer() {
		AVList layerParams = new AVListImpl();
		// System.out.println(this.layers);
		layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
		layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
		layerParams.setValue(AVKey.DETAIL_HINT, this.detailhint);
		
		//set defaults
		Angle delta = Angle.fromDegrees(36);
		layerParams.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(delta, delta));
		layerParams.setValue(AVKey.TILE_WIDTH, 256);
		layerParams.setValue(AVKey.TILE_HEIGHT, 256);
		layerParams.setValue(AVKey.FORMAT_SUFFIX, ".png");
		layerParams.setValue(AVKey.NUM_LEVELS, 19); // approximately 0.1 meters per pixel
		layerParams.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
		//Sector sector = new Sector();
		//layerParams.setValue(AVKey.SECTOR, sector);
		
		this.layer = new RimapWMSTiledImageLayer(layerParams);
		this.layer.setName(this.name);
		this.layer.setParent(this);
		//redundant with previous...
		this.layer.setValue(RimapAVKey.LAYER_PARENTNODE, this);
		this.layer.setValue(AVKey.FORMAT_SUFFIX, ".png");
		this.layer.setValue(RimapAVKey.HAS_RIMAP_EXTENSIONS, true);
		if ((this.polygonQueryParams!=null) && (this.polygonQueryParams.isValid())) {
			this.layer.setValue(RimapAVKey.LAYER_ISPOLYGONQUERYABLE, true);
			this.layer.setValue(RimapAVKey.LAYER_POLYGONQUERYPARAMS, this.polygonQueryParams);
		} else {
			this.layer.setValue(RimapAVKey.LAYER_ISPOLYGONQUERYABLE, false);
		}
		if (this.layer != null) {
			// Loading from caps may fail. In this case, layer, will still be
			// null
			layer.setEnabled(this.checked);
		}
		return this.layer;
	}

	public LayerType getType() {
		return type;
	}

	public void setType(LayerType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getLastchanged() {
		return lastchanged;
	}

	public void setLastchanged(Date lastchanged) {
		this.lastchanged = lastchanged;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLayers() {
		return layers;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}

	public String getLegendurl() {
		return legendurl;
	}

	public void setLegendurl(String legendurl) {
		this.legendurl = legendurl;
	}

	public String getMetadata_uuid() {
		return metadata_uuid;
	}

	public void setMetadata_uuid(String metadata_uuid) {
		this.metadata_uuid = metadata_uuid;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isTiled() {
		return tiled;
	}

	public void setTiled(boolean tiled) {
		this.tiled = tiled;
	}

	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}

	public void setLayer(RimapWMSTiledImageLayer layer) {
		this.layer = layer;
	}

	public boolean isQueryable() {
		return this.queryable;
	}

	public PolygonQueryableParams getPolygonQueryParams() {
		return polygonQueryParams;
	}

	public void setPolygonQueryParams(PolygonQueryableParams polygonQueryParams) {
		this.polygonQueryParams = polygonQueryParams;
	}

	@Override
	public boolean isAvailable() {
		return (this.getLayer() !=null);
	}

	@Override
	public Image getIcon() {
		return NodeUtils.getImage(WmsNode.IMAGE_WMSICON);
	}
}
