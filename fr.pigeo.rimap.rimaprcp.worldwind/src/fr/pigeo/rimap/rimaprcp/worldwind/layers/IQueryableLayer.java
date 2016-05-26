package fr.pigeo.rimap.rimaprcp.worldwind.layers;

import java.net.URL;

import gov.nasa.worldwind.geom.Position;

public interface IQueryableLayer {

	public boolean isQueryable();
	public URL buildFeatureInfoRequest(Position pos, String locale);
	public String getName();
}
