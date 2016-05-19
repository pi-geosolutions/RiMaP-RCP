package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckable;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;
import fr.pigeo.rimap.rimaprcp.riskcatalog.RimapWMSTiledImageLayer;

public class WmsNode extends AbstractNode implements ICheckable {
	private static String IMAGE_WMSICON = "icons/wms.png";
	private static String IMAGE_CHECKED = "icons/16px-checkbox-checked.png";
	private static String IMAGE_UNCHECKED = "icons/16px-checkbox-unchecked.png";

	public static Image checkedImage;
	public static Image uncheckedImage;
	public static Image wmsImage;

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
		if (eventBroker != null) {
			eventBroker.post(RiMaPEventConstants.WMSNODE_CHECKCHANGE, this);
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

}
