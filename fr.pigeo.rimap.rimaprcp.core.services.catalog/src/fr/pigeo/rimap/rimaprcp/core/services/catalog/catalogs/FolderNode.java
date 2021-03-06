package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.IExpandableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.CatalogConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;
import fr.pigeo.rimap.rimaprcp.core.utils.JsonUtils;

public class FolderNode extends AbstractNode implements IExpandableNode {
	private static String IMAGE_FOLDERICON = "icons/folder.gif";
	private static String IMAGE_FOLDERICON_OPEN = "icons/folder-open.gif";

	public static Image foldedImage;
	public static Image unfoldedImage;

	protected LayerType type = LayerType.FOLDER;
	private INode parent = null;
	private List<INode> leaves = null;
	private String id;
	private String name = "new folder";
	private boolean expanded = true;
	private String style = "folder";
	private String comments = "";
	private Date lastchanged;
	private Integer weight = 1;

	@Inject
	IPreferencesService prefsService;

	@Inject
	IEclipseContext context;

	@Inject
	IEventBroker eventBroker;

	@Inject
	Logger logger;

	// Custom injected resource
	@Inject
	PadreCatalogState catalogState;

	private String childrentag = "children";

	@Inject
	public FolderNode(@Optional IPreferencesService prefsService) {
		this.prefsService = prefsService;
		// TODO: test this (including preference override)
		if (prefsService != null) {
			childrentag = prefsService.getString(CatalogConstants.PREFERENCES_NODE, CatalogConstants.CHILDREN_PREF_TAG,
					CatalogConstants.CHILDREN_PREF_DEFAULT, null);
		}
	}

	public FolderNode() {

	}

	@Override
	public void loadFromJson(JsonNode node) {
		if (!NodeUtils.isValid(node, this.type)) {
			if (logger != null)
				logger.error("ERROR: error parsing JsonNode in " + this.getClass()
					.getName());
			return;
		}
		if (!this.isRootNode()) {
			this.id = JsonUtils.parseString(node, "id", null);
			this.name = JsonUtils.parseString(node, "text", "unnamed folder");
			this.expanded = JsonUtils.parseBool(node, "expanded", false);
			this.lastchanged = JsonUtils.parseDate(node, "lastchanged");
			// System.out.println("Loaded node "+this.name);
		}
		if (node.has(this.childrentag)) {

			JsonNode children = node.get(this.childrentag);
			if (children.isArray())
				this.leaves = this.loadLeaves(children);
		}
		// TODO: implement alternative to this
		// if (this.expanded) {
		// PadreCatalog.addExpandedFolder(this);
		// }
		if (catalogState != null) {
			if (this.expanded) {
				catalogState.addExpandedNode(this);
			}
		} else {
			if (logger != null)
				logger.error("################ catalogState context var is null #################");
		}
	}

	private List<INode> loadLeaves(JsonNode list) {
		// System.out.println("loading leaves for node "+this.name);
		AbstractNode layer = null;
		List<INode> layers = new ArrayList();
		Iterator<JsonNode> itr = list.iterator();
		while (itr.hasNext()) {
			JsonNode child = itr.next();
			LayerType type = null;
			try {
				type = LayerType.valueOf(child.get("type")
					.asText()
					.toUpperCase()); // convert
										// e.g.
										// 'folder'
										// to
										// LayerType.FOLDER
			} catch (Exception ex) {
				logger.error("Found unknown type "+child.get("type").asText()+" while parsing the layertree");
				logger.error(ex.getLocalizedMessage());
				break;
			}
			switch (type) {
			case FOLDER:
				layer = ContextInjectionFactory.make(FolderNode.class, context);
				layer.setParent(this);
				layer.loadFromJson(child);
				layers.add(layer);
				break;
			case WMS:
				layer = ContextInjectionFactory.make(WmsNode.class, context);
				layer.setParent(this);
				layer.loadFromJson(child);
				layers.add(layer);
				break;
			case WMSDEM:
				layer = ContextInjectionFactory.make(WmsDemNode.class, context);
				layer.setParent(this);
				layer.loadFromJson(child);
				layers.add(layer);
				break;
			case CHART:
				System.out.println("TODO : load chart layers");
				break;
			default:
				// System.out.println("encountered weird layertree node while
				// parsing Layertree from json: " + type.toString());
				break;
			}
		}
		return layers;
	}

	@Override
	public INode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean hasLeaves() {
		return (leaves != null && !leaves.isEmpty());
	}

	@Override
	public List<INode> getLeaves() {
		return leaves;
	}

	@Override
	public boolean isRootNode() {
		return parent == null;
	}

	@Override
	public void setParent(INode parent) {
		this.parent = parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Image getImage() {
		// return NodeUtils.getImage(FolderNode.IMAGE_FOLDERICON);
		if (this.expanded) {
			if (FolderNode.unfoldedImage == null) {
				FolderNode.unfoldedImage = NodeUtils.getImage(FolderNode.IMAGE_FOLDERICON_OPEN);
			}
			return FolderNode.unfoldedImage;
		} else {
			if (FolderNode.foldedImage == null) {
				FolderNode.foldedImage = NodeUtils.getImage(FolderNode.IMAGE_FOLDERICON);
			}
			return FolderNode.foldedImage;
		}
	}

	@Override
	public void setExpanded(boolean expand) {
		if (expand == this.expanded) {
			return;
		}
		this.expanded = expand;
		if (eventBroker != null) {
			eventBroker.post(RiMaPEventConstants.FOLDERNODE_EXPANDCHANGE, this);
		}
	}

	@Override
	public void toggleExpanded() {
		setExpanded(!expanded);
	}

	@Override
	public void changeState() {
		// System.out.println("State changed for node "+this.getName());
		toggleExpanded();
	}

	@Override
	public boolean getExpanded() {
		return expanded;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public String getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMetadata_uuid() {
		// TODO Auto-generated method stub
		return null;
	}

}
