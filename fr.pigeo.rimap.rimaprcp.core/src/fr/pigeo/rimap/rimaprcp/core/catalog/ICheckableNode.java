package fr.pigeo.rimap.rimaprcp.core.catalog;

import gov.nasa.worldwind.layers.Layer;

/**
 * Interface for checkable nodes
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public interface ICheckableNode extends INode {
	public void setChecked(boolean check);

	public boolean getChecked();

	public void toggleChecked();
	
	/**
	 * getLayer() : gets WWJ Layer instanciated by this node
	 * @return gov.nasa.worldwind.layers.Layer
	 */
	public Layer getLayer();
}
