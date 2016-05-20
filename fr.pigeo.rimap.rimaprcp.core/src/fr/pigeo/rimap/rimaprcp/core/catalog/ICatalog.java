package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.List;

public interface ICatalog {
	public INode getRootNode();
	public boolean load();
	public void reload();
	public INode getNodeById(String id);
	public INode getNodeByName(String id);
	public List<ICheckableNode> getCheckedNodes();
	public List<IExpandableNode> getOpenFolders();
	//probably alias to getOpenFolders()
	public List<IExpandableNode> getExpandedNodes();
	public void sync();
	
}
