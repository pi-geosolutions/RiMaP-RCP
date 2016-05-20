package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.databind.JsonNode;

public interface INode {
	public void loadFromJson(JsonNode node);
	public INode getParent();
	public void setParent(INode parent);
	public boolean isLeaf();
	public boolean hasLeaves();
	public List<INode> getLeaves();
	public boolean isRootNode();
	public String getName();
	public void setName(String name);
	public Image getImage();
	public void changeState();
}
