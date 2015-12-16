package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.catalog.RiskJfaceCatalogImpl;

public class FolderLayer extends AbstractLayer {
	private boolean isRoot = false;
	private LayerType type = LayerType.FOLDER;
	private String id;
	private String name = "new folder";
	private boolean expanded = false;
	private String style = "folder";
	private String comments = "";
	private Date lastchanged;
	private Integer weight = 1;
	private List<AbstractLayer> children;

	static private String folderImagePath = "icons/folder.gif";
	static private Image folderImage;
	static private String folderExpandedImagePath = "icons/folder-open.gif";
	static private Image folderExpandedImage;

	public FolderLayer(AbstractLayer parent) {
		this.parent = parent;
	}

	public FolderLayer(AbstractLayer parent, JsonNode node) {
		this.parent = parent;
		this.loadFromJson(node);
	}

	public FolderLayer(AbstractLayer parent, boolean isroot) {
		this.parent = parent;
		if (isroot)
			this.setIsRoot();
	}

	public FolderLayer(AbstractLayer parent, JsonNode node, boolean isroot) {
		this.parent = parent;
		if (isroot)
			this.setIsRoot();
		this.loadFromJson(node);
	}

	private void setIsRoot() {
		this.isRoot = true;
		this.id = "rootNode" + String.valueOf(Math.random());
		this.name = "root";
		this.expanded = true;
	}

	@Override
	public void loadFromJson(JsonNode node) {
		if (node == null) {
			System.out.println("ERROR: error parsing JsonNode in " + this.getClass().getName());
			return;
		}
		if (!this.isRoot) {
			if (!node.get("type").asText().equalsIgnoreCase(LayerType.FOLDER.toString())) {
				System.out.println("ERROR: wrong node type encountered while parsing JsonNode in "
						+ this.getClass().getName() + ".(type is " + node.get("type").asText() + ")");
				return;
			}

			this.id = this.parseString(node, "id", null);
			this.name = this.parseString(node, "text", "unnamed folder");
			this.expanded = this.parseBool(node, "expanded", this.expanded);
			this.lastchanged = this.parseDate(node, "lastchanged");
		}
		if (node.has(RiskJfaceCatalogImpl.RIMAP_LAYERTREE_CHILDREN_TAG)) {
			JsonNode children = node.get(RiskJfaceCatalogImpl.RIMAP_LAYERTREE_CHILDREN_TAG);
			if (children.isArray())
				this.children = this.loadChildren(children);
		}
	}

	private List<AbstractLayer> loadChildren(JsonNode list) {
		AbstractLayer layer = null;
		List<AbstractLayer> layers = new ArrayList<AbstractLayer>();
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
				layer = new FolderLayer(this, child);
				layers.add(layer);
				break;
			case WMS:
				layer = new WmsLayer(this, child);
				layers.add(layer);
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

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public LayerType getType() {
		return type;
	}

	public void setType(LayerType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
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

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public Date getLastchanged() {
		return lastchanged;
	}

	public List<AbstractLayer> getChildren() {
		if (children == null)
			children = new ArrayList<AbstractLayer>();
		return children;
	}

	public Image getImage(boolean expanded) {
		if (expanded) {
			if (FolderLayer.folderExpandedImage == null) {
				Bundle bundle = FrameworkUtil.getBundle(FolderLayer.class);
				URL url = FileLocator.find(bundle, new Path(FolderLayer.folderExpandedImagePath), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				FolderLayer.folderExpandedImage = imageDcr.createImage();
			}
			return FolderLayer.folderExpandedImage;
		} else {
			if (FolderLayer.folderImage == null) {
				Bundle bundle = FrameworkUtil.getBundle(FolderLayer.class);
				URL url = FileLocator.find(bundle, new Path(FolderLayer.folderImagePath), null);
				ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
				FolderLayer.folderImage = imageDcr.createImage();
			}
			return FolderLayer.folderImage;
		}
	}
}
