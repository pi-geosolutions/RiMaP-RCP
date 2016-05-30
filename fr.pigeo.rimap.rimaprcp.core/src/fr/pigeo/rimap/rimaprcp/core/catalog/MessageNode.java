package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Very specific implementation of INode, dedicated to creating a temp
 * "loading..." node, while waiting for the catalog to properly load
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class MessageNode implements INode {
	private String image_path = "icons/loading16x16.gif";
	private Image image;
	String name = "loading...";

	public MessageNode() {
	}

	public MessageNode(String txt) {
		this.name = txt;
	}

	/**
	 * 
	 * @param txt
	 * @param imagepath
	 *            : path relative to this bundle. If you want to use
	 *            non-standard image, use a fragment in order to make it
	 *            available
	 */
	public MessageNode(String txt, String imagepath) {
		this.name = txt;
		this.image_path = imagepath;
	}

	@Override
	public void loadFromJson(JsonNode node) {
	}

	@Override
	public INode getParent() {
		return null;
	}

	@Override
	public void setParent(INode parent) {
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
		return true;
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
		if (image == null && this.image_path !=null) {
			Bundle bundle = FrameworkUtil.getBundle(MessageNode.class);
			URL url = FileLocator.find(bundle, new Path(this.image_path), null);
			if (url==null) {
				return null;
			}
			ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
			image = imageDescr.createImage();
		}

		return image;
	}

	@Override
	public void changeState() {
	}

	public void dispose() {
		if (this.image != null) {
			this.image.dispose();
		}
	}

	public void setMessage(String msg) {
		this.setName(msg);
	}

	public String getMessage() {
		return this.getName();
	}

}
