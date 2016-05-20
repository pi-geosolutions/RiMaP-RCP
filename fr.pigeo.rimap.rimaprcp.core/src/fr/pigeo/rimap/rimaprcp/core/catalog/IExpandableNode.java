package fr.pigeo.rimap.rimaprcp.core.catalog;

public interface IExpandableNode extends INode {
	public void setExpanded(boolean fold);

	public boolean getExpanded();

	public void toggleExpanded();
}
