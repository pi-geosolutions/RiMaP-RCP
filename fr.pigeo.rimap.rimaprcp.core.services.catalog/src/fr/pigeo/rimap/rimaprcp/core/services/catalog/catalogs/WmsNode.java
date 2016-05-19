package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.INode;

public class WmsNode extends AbstractNode {

	@Override
	public void loadFromJson(JsonNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public INode getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParent(INode parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasLeaves() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<INode> getLeaves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRootNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
