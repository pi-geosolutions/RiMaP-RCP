package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.List;

public interface ICatalog {
	public INode getRootNode();
	public void reload();
	public INode getNodeById(String id);
	public INode getNodeByName(String id);
	public List<INode> getCheckedNodes();
	public List<INode> getOpenFolders();
	//probably alias to getOpenFolders()
	public List<INode> getUnfoldedNodes();
	
}
