package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.util.List;

public interface ICatalog {
	public INode getRootNode();
	public boolean load();
	public boolean reload();
	public INode getNodeById(String id);
	public INode getNodeByName(String id);
	public List<ICheckableNode> getCheckedNodes();
	public List<IExpandableNode> getOpenFolders();
	//probably alias to getOpenFolders()
	public List<IExpandableNode> getExpandedNodes();
	public void sync();
	// returns true if credentials can be used to recover the layertree
	// local: true -> tries to decrypt the file using the password
	//        false -> TODO : test online
	public boolean testCredentials(String username, String password, boolean local);
}
