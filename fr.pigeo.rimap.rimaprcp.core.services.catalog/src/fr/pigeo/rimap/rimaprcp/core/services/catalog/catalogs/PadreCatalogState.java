package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.util.ArrayList;
import java.util.List;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.catalog.IExpandableNode;

public class PadreCatalogState {
	private List<IExpandableNode> expandedNodes = new ArrayList();
	private List<ICheckableNode> checkedNodes = new ArrayList();

	public List<IExpandableNode> getExpandedNodes() {
		return expandedNodes;
	}

	public List<ICheckableNode> getCheckedNodes() {
		return checkedNodes;
	}

	public void addExpandedNode(IExpandableNode node) {
		expandedNodes.add(node);
	}

	public void addCheckedNode(ICheckableNode node) {
		checkedNodes.add(node);
	}
}
