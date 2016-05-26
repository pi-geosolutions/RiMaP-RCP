package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import fr.pigeo.rimap.rimaprcp.worldwind.layers.IQueryableLayer;
import gov.nasa.worldwind.geom.Position;

public class FeatureInfoTarget {
	private IQueryableLayer layer;
	private Position position;

	public FeatureInfoTarget(IQueryableLayer l, Position pos) {
		this.layer = l;
		this.position = pos;
	}

	/**
	 * @return the layer
	 */
	public IQueryableLayer getLayer() {
		return layer;
	}

	/**
	 * @param layer
	 *            the layer to set
	 */
	public void setLayer(IQueryableLayer layer) {
		this.layer = layer;
	}

	/**
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

}
