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
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.catalog.CatalogProperties;
import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.CatalogConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;

public class FolderNode extends AbstractNode {
	private INode parent = null;
	private List<INode> leaves = null;
	private String id;
	private String name = "new folder";
	private boolean expanded = false;
	private String style = "folder";
	private String comments = "";
	private Date lastchanged;
	private Integer weight = 1;

	@Inject
	@Optional
	private IPreferencesService prefsService;
	
	@Inject
	@Optional
	IEclipseContext context;
	
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
		// TODO Auto-generated method stub
		if (node == null) {
			System.out.println("ERROR: error parsing JsonNode in " + this.getClass().getName());
			return;
		}
		if (!this.isRootNode()) {
			if (!node.get("type").asText().equalsIgnoreCase(LayerType.FOLDER.toString())) {
				System.out.println("ERROR: wrong node type encountered while parsing JsonNode in "
						+ this.getClass().getName() + ".(type is " + node.get("type").asText() + ")");
				return;
			}

			this.id = this.parseString(node, "id", null);
			this.name = this.parseString(node, "text", "unnamed folder");
			this.expanded = this.parseBool(node, "expanded", this.expanded);
			this.lastchanged = this.parseDate(node, "lastchanged");
			// System.out.println("Loaded node "+this.name);
		}
		if (node.has(CatalogProperties.getProperty("layertree.childrentag"))) {

			// System.out.println("...loading its children");
			JsonNode children = node.get(this.childrentag);
			if (children.isArray())
				this.leaves = this.loadLeaves(children);
		}
		// TODO: implement alternative to this
		// if (this.expanded) {
		// PadreCatalog.addExpandedFolder(this);
		// }
	}

	private List<INode> loadLeaves(JsonNode list) {
		System.out.println("loading leaves for node "+this.name);
		AbstractNode layer = null;
		List<INode> layers = new ArrayList();
		Iterator<JsonNode> itr = list.iterator();
		while (itr.hasNext()) {
			JsonNode child = itr.next();
			LayerType type = LayerType.valueOf(child.get("type").asText().toUpperCase()); // convert
																							// e.g.
																							// 'folder'
																							// to
																							// LayerType.FOLDER
			switch (type) {
			case FOLDER:
				layer = ContextInjectionFactory.make(FolderNode.class, context);
				layer.setParent(this);
				layer.loadFromJson(child);
				layers.add(layer);
				break;
			case WMS:
				// layer = new WmsLayer(this, child);
				// layers.add(layer);
				break;
			case CHART:

				System.out.println("TODO : load chart layers");
				break;
			default:
				System.out.println(
						"encountered weird layertree node while parsing Layertree from json: " + type.toString());
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
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
