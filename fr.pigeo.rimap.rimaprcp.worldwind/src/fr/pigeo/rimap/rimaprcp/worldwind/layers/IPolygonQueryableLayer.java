package fr.pigeo.rimap.rimaprcp.worldwind.layers;

public interface IPolygonQueryableLayer {

	public boolean isPolygonQueryable();
	public PolygonQueryableParams getParams();
	public void setParams(PolygonQueryableParams params);
	public String getName();
	public String getWPSUrl();
	public String getWFSUrl();
}
