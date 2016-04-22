package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import fr.pigeo.rimap.rimaprcp.riskcatalog.Queryable;
import gov.nasa.worldwind.geom.Position;

public class FeatureInfoTarget {
	private Queryable layer;
	private Position position;

	public FeatureInfoTarget(Queryable l, Position pos) {
		this.layer = l;
		this.position = pos;
	}

	/**
	 * @return the layer
	 */
	public Queryable getLayer() {
		return layer;
	}

	/**
	 * @param layer
	 *            the layer to set
	 */
	public void setLayer(Queryable layer) {
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
