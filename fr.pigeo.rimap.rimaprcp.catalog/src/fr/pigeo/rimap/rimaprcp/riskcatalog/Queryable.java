package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.URL;

import org.w3c.dom.Document;

import gov.nasa.worldwind.geom.Position;
/**
 * Interface used to determine if a WWJ layer is queryable and to perform the query (getFeatureInfo)
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public interface Queryable {
	public boolean isQueryable();
	public Document retrieveFeatureInfo(Position pos);
	public URL buildFeatureInfoRequest(Position pos);
	public String getName();
}
