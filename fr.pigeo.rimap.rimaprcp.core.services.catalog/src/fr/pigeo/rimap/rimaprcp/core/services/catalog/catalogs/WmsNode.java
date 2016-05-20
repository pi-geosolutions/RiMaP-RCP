package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.catalog.CatalogProperties;
import fr.pigeo.rimap.rimaprcp.catalog.PadreCatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class WmsNode extends AbstractNode implements ICheckableNode {
	private static String IMAGE_WMSICON = "icons/wms.png";
	private static String IMAGE_CHECKED = "icons/16px-checkbox-checked.png";
	private static String IMAGE_UNCHECKED = "icons/16px-checkbox-unchecked.png";

	public static Image checkedImage;
	public static Image uncheckedImage;
	public static Image wmsImage;

	protected LayerType type = LayerType.WMS;
	protected WMSTiledImageLayer layer;
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
	protected String pq_layer;
	protected boolean tiled = true;
	protected boolean queryable = true;
	protected boolean checked = false;

	@Inject
	@Optional
	IPreferencesService prefsService;

	@Inject
	@Optional
	IEclipseContext context;

	@Inject
	@Optional
	IEventBroker eventBroker;

	// Custom injected resource
	@Inject
	@Optional
	PadreCatalogState catalogState;

	@Override
	public void loadFromJson(JsonNode node) {
		if (!NodeUtils.isValid(node, this.type)) {
			System.out.println("ERROR: error parsing JsonNode in " + this.getClass().getName());
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
		this.pq_layer = NodeUtils.parseString(node, "pq_layer", null);

		// TODO : implement equivalent, using context ?
		/*
		 * if (this.checked)
		 * PadreCatalog.addInitiallyCheckedLayer(this);
		 * 
		 * if (Boolean.getBoolean(CatalogProperties.getProperty(
		 * "catalog.loadcapabilitiesatstartup"))) {
		 * PadreCatalog.addServerCapability(this.url);
		 * }
		 */

		if (catalogState != null) {
			if (this.checked) {
				catalogState.addCheckedNode(this);
			}
		} else {
			System.out.println("################ catalogState context var is null #################");
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

	// TODO: purge old code from this (capabilities, RIMAPSWMSLayer)
	@Override
	public Layer getLayer() {
		if (this.layer == null) {
			// System.out.println("first, creating layer");
			WMSCapabilities caps = PadreCatalog.getServerCapabilities(this.url);
			if (caps == null) {
				try {
					URI wmsUri = new URI(this.url);
					caps = WMSCapabilities.retrieve(wmsUri);
					caps.parse();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			AVList layerParams = new AVListImpl();
			// System.out.println(this.layers);
			layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
			layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
			layerParams.setValue(AVKey.TILE_WIDTH, 256);
			layerParams.setValue(AVKey.TILE_HEIGHT, 256);
			layerParams.setValue(AVKey.DETAIL_HINT,
					Double.parseDouble(CatalogProperties.getProperty("wmslayer.defaultdetailhint")));
			try {
				this.layer = new WMSTiledImageLayer(caps, layerParams);
				this.layer.setName(this.name);
				this.layer.setValue(RimapAVKey.LAYER_PARENTNODE, this);
				this.layer.setValue(RimapAVKey.HAS_RIMAP_EXTENSIONS, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		layer.setEnabled(this.checked);
		return this.layer;
	}
}
