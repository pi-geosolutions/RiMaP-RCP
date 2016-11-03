package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;
import gov.nasa.worldwind.geom.Position;

public class PolygonQueryTarget {
	private IPolygonQueryableLayer layer;
	private Position position;

	public PolygonQueryTarget(IPolygonQueryableLayer l, Position pos) {
		this.layer = l;
		this.position = pos;
	}

	/**
	 * @return the layer
	 */
	public IPolygonQueryableLayer getLayer() {
		return layer;
	}

	/**
	 * @param layer
	 *            the layer to set
	 */
	public void setLayer(IPolygonQueryableLayer layer) {
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
