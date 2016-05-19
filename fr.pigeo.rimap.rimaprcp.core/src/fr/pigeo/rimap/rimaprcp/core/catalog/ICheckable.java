package fr.pigeo.rimap.rimaprcp.core.catalog;

/**
 * Interface for checkable nodes
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public interface ICheckable extends INode {
	public void setChecked(boolean check);

	public boolean getChecked();

	public void toggleChecked();
}
