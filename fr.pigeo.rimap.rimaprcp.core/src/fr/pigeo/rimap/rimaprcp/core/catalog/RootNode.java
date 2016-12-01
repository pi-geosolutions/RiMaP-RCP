package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Root container for all other nodes : this way, we can add more catalogs. Each
 * catalog root node is added as a child of this one
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class RootNode implements INode {
	private List<INode> leaves = new ArrayList();
	private String name="root";
	
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
		return false;
	}

	@Override
	public boolean hasLeaves() {
		return true;
	}

	@Override
	public List<INode> getLeaves() {
		return this.leaves;
	}
	
	public void addLeaf(INode n) {
		this.leaves.add(n);
	}
	
	public void clear() {
		this.leaves.clear();
	}
	
	public void removeLeaf(INode n) {
		this.leaves.remove(n);
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
		return null;
	}

	@Override
	public void changeState() {
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public Image getIcon() {
		return null;
	}

}
