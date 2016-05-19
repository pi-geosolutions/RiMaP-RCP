package fr.pigeo.rimap.rimaprcp.core.catalog;

public interface IExpandable extends INode {
	public void setExpanded(boolean fold);

	public boolean getExpanded();

	public void toggleExpanded();
}
