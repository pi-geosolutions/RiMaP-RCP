package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;

public abstract class AbstractNode implements INode {
	protected LayerType type = LayerType.ABSTRACT;

	public abstract String getComments();
	public abstract String getMetadata_uuid();
}
