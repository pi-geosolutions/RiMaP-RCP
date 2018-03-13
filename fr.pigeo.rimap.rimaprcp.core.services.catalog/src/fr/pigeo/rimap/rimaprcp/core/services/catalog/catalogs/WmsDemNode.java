package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;
import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

public class WmsDemNode extends AbstractNode implements ICheckableNode {
	private static String IMAGE_WMSDEM_ICON = "icons/wmsdem.png";
	private static String IMAGE_CHECKED = "icons/16px-checkbox-checked.png";
	private static String IMAGE_UNCHECKED = "icons/16px-checkbox-unchecked.png";
	private static String IMAGE_DISABLED = "icons/16px-checkbox-disabled.png";

	public static Image checkedImage;
	public static Image uncheckedImage;
	public static Image disabledImage;
	public static Image wmsImage;

	protected double detailhint = 0.1;

	protected LayerType type = LayerType.WMSDEM;
	protected ElevationModel layer;
	protected INode parent = null;
	protected String id;
	protected String name = "unnamed wms DEM layer";
	protected String style = "wmsdem";
	protected String comments = "";
	protected Date lastchanged;
	protected Integer weight = 1;
	protected String url;
	protected String layers;
	protected String metadata_uuid;
	protected String format = "image/bil";
	protected boolean tiled = true;
	protected boolean queryable = false;
	protected boolean checked = false;

	@Inject
	IPreferencesService prefsService;

	@Inject
	IEclipseContext context;

	@Inject
	IEventBroker eventBroker;

	// Custom injected resource
	@Inject
	@Optional
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
		this.style = NodeUtils.parseString(node, "cls", LayerType.WMSDEM.toString());
		this.comments = NodeUtils.parseString(node, "qtip", this.comments);
		this.lastchanged = NodeUtils.parseDate(node, "lastchanged");
		this.weight = NodeUtils.parseInt(node, "weight", this.weight);
		this.url = NodeUtils.parseString(node, "url", this.url);
		this.layers = NodeUtils.parseString(node, "layers", "");
		this.metadata_uuid = NodeUtils.parseString(node, "uuid", "");
		this.format = NodeUtils.parseString(node, "format", this.format);
		//this.tiled = NodeUtils.parseBool(node, "TILED", this.tiled);//should be true
		this.checked = NodeUtils.parseBool(node, "checked", this.checked);

		wmsService.registerServerCapability(this.url);

		if (catalogState != null) {
			if (this.checked) {
				catalogState.addCheckedNode(this);
			}
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
			if (WmsDemNode.disabledImage == null) {
				WmsDemNode.disabledImage = NodeUtils.getImage(WmsDemNode.IMAGE_DISABLED);
			}
			return WmsDemNode.disabledImage;
		}
		if (this.checked) {
			if (WmsDemNode.checkedImage == null) {
				WmsDemNode.checkedImage = NodeUtils.getImage(WmsDemNode.IMAGE_CHECKED);
			}
			return WmsDemNode.checkedImage;
		} else {
			if (WmsDemNode.uncheckedImage == null) {
				WmsDemNode.uncheckedImage = NodeUtils.getImage(WmsDemNode.IMAGE_UNCHECKED);
			}
			return WmsDemNode.uncheckedImage;
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
	public ElevationModel getLayer() {
		return this.getLayer(false);
	}
	
	
	public ElevationModel getLayer(boolean forceReload) {
		if (forceReload) {
			this.layer=null;
		}
		
		if (this.layer == null) {
			try {
				final String GET_CAPABILITIES_URL = this.url + "?request=getCapabilities";
				System.out.println("retrieving caps for DEM data from "+this.url+" for layer "+this.layers);
				WMSCapabilities caps = WMSCapabilities.retrieve(new URI(GET_CAPABILITIES_URL));
				if (caps==null) {
					return null;
				}
				caps.parse();
	
				AVList layerParams = new AVListImpl();
				// System.out.println(this.layers);
				layerParams.setValue(AVKey.LAYER_NAMES, this.layers);
				layerParams.setValue(AVKey.DISPLAY_NAME, this.name);
				layerParams.setValue(AVKey.IMAGE_FORMAT, "application/bil32");
				//layerParams.setValue(AVKey.FORMAT_SUFFIX, ".bil");
				//layerParams.setValue(AVKey.BYTE_ORDER, AVKey.BIG_ENDIAN); //configured to little endian
			
				Factory factory = (Factory) WorldWind.createConfigurationComponent(AVKey.ELEVATION_MODEL_FACTORY);
				this.layer = (ElevationModel) factory.createFromConfigSource(caps, layerParams);
				//this.layer = new RimapWMSTiledImageLayer(caps, layerParams);
				this.layer.setName(this.name);
				//this.layer.setParent(this);
				//redundant with previous...
				this.layer.setValue(RimapAVKey.LAYER_PARENTNODE, this);
				this.layer.setValue(RimapAVKey.HAS_RIMAP_EXTENSIONS, true);
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
		// TODO
		return null;
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

	public void setLayer(ElevationModel layer) {
		this.layer = layer;
	}

	public boolean isQueryable() {
		return this.queryable;
	}

	@Override
	public boolean isAvailable() {
		return (this.getLayer() !=null);
	}

	@Override
	public Image getIcon() {
		return NodeUtils.getImage(WmsDemNode.IMAGE_WMSDEM_ICON);
	}
}
