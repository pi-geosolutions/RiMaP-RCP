package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import java.awt.Component;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.IStructuredSelection;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.wwj.PolygonBuilder;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.SurfacePolygon;

@Creatable
@Singleton
public class PolygonQuery {
	// PQ settings
	// nb digits in decimal degree representation
	int digits = 10;
	// Image format used in the WPS process
	String imageFormat = "image/tiff";

	@Inject
	Logger logger;
	@Inject
	WwjInstance wwj;
	@Inject
	IEclipseContext context;
	@Inject
	IEventBroker eventBroker;

	private List<IPolygonQueryableLayer> layers = new ArrayList();
	private boolean enabled;
	private PolygonBuilder pb;

	@Inject
	public PolygonQuery() {
	}

	/**
	 * @return the queryable layers list
	 */
	public List<IPolygonQueryableLayer> getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(List<IPolygonQueryableLayer> layers) {
		this.layers = layers;
	}

	/**
	 * @return the PolygonQuery tool status
	 */
	public boolean isEnabled() {
		return enabled;
	}

	private PolygonBuilder getPolygonBuilder() {
		// Lazy init of the polygonBuilder instance
		if (this.pb == null) {
			this.pb = new PolygonBuilder(wwj.getWwd(), null, null);
			ContextInjectionFactory.inject(this.pb, context);
		}
		return this.pb;
	}

	/**
	 * @param enabled
	 *            enable PolygonQuery tool
	 */
	public void setEnabled(boolean enabled) {
		// clear the layers list
		this.layers.clear();

		PolygonBuilder p = getPolygonBuilder();

		this.enabled = enabled;
		logger.info("PolygonQuery button status is " + enabled);

		if (this.enabled) {
			p.clear();
			p.setArmed(true);
			((Component) wwj.getWwd()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else {
		}
	}

	private void getStats(String postdata) {
		System.out.println("TODO : code the PQ internals. Send");

		System.out.println(postdata);
	}

	/**
	 * @param enabled
	 *            toggles the PolygonQuery tool status
	 */
	public void toggleEnabled() {
		this.setEnabled(!this.isEnabled());
	}

	@Inject
	@Optional
	void polygonClosed(@UIEventTopic(QueryEventConstants.POLYGONQUERY_POLYGON_CLOSED) SurfacePolygon poly) {
		((Component) wwj.getWwd()).setCursor(Cursor.getDefaultCursor());
		if (context == null) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) context
				.get(RimapConstants.RIMAP_SELECTEDLAYERS_CONTEXT_NAME);
		if (selection == null) {
			// TODO display an info message : one need to select eligible layers
			// in order for this to work
			return;
		}
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			// takes the first selected PolygonQueryable layer (for now)
			// TODO : query all eligible selected layers
			Object o = it.next();
			if (o instanceof IPolygonQueryableLayer) {
				IPolygonQueryableLayer pql = (IPolygonQueryableLayer) o;
				if (pql.isPolygonQueryable()) {
					this.layers.add(pql);
					/*
					 * PolygonQueryableParams params = pql.getParams();
					 * String xml = this.buildWPSRequest(params.getLayernames(),
					 * poly);
					 * getStats(xml);
					 * break;
					 */
				}
			}
		}
		if (eventBroker != null) {
			eventBroker.post(QueryEventConstants.POLYGONQUERY_READY, this);
		} 
	}

	public String buildWPSRequest(String layername, SurfacePolygon poly) {
		String xml = "";
		BoundingBox bb = computeEnclosingBounds(poly);
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">"
				+ "  <ows:Identifier>ras:RasterZonalStatistics</ows:Identifier>" + "  <wps:DataInputs>"
				+ "    <wps:Input>" + "      <ows:Identifier>data</ows:Identifier>"
				+ "      <wps:Reference mimeType=\"image/tiff\" xlink:href=\"http://geoserver/wcs\" method=\"POST\">"
				+ "        <wps:Body>" + "          <wcs:GetCoverage service=\"WCS\" version=\"1.1.1\">"
				+ "            <ows:Identifier>" + layername + "</ows:Identifier>" + "            <wcs:DomainSubset>"
				+ "              <gml:BoundingBox crs=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">"
				+ "                <ows:LowerCorner>" + a2s(bb.minLon) + " " + a2s(bb.minLat) + "</ows:LowerCorner>"
				+ "                <ows:UpperCorner>" + a2s(bb.maxLon) + " " + a2s(bb.maxLat) + "</ows:UpperCorner>"
				+ "              </gml:BoundingBox>" + "            </wcs:DomainSubset>"
				+ "            <wcs:Output format=\"" + imageFormat + "\"/>" + "          </wcs:GetCoverage>"
				+ "        </wps:Body>" + "      </wps:Reference>" + "    </wps:Input>" + "    <wps:Input>"
				+ "      <ows:Identifier>zones</ows:Identifier>" + "      <wps:Data>"
				+ "        <wps:ComplexData mimeType=\"application/json\">" + "			<![CDATA["
				+ this.polygonToStringRep(poly) + "]]>" + "		 </wps:ComplexData>" + "      </wps:Data>"
				+ "    </wps:Input>" + "  </wps:DataInputs>" + "  <wps:ResponseForm>"
				+ "    <wps:RawDataOutput mimeType=\"application/json\">"
				+ "      <ows:Identifier>statistics</ows:Identifier>" + "    </wps:RawDataOutput>"
				+ "  </wps:ResponseForm>" + "</wps:Execute>";
		return xml;
	}

	private String polygonToStringRep(SurfacePolygon p) {
		String json = "	{\"type\":\"FeatureCollection\", "
				+ "\"crs\":{\"type\":\"EPSG\",\"properties\":{\"code\":\"4326\"}},"
				+ "\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[";
		Iterator it = p.getOuterBoundary()
				.iterator();
		while (it.hasNext()) {
			LatLon l = (LatLon) it.next();
			json += "[" + a2s(l.getLongitude()) + "," + a2s(l.getLatitude()) + "],";
		}
		json = json.substring(0, json.length() - 2);
		json += "]]}}]}";

		return json;
	}

	/**
	 * Computes the enclosing bounding rectangle for a given SurfacePolygon
	 * instance
	 * 
	 * @param poly
	 * @return a BoundingBox instance containing the bounding values
	 */
	private BoundingBox computeEnclosingBounds(SurfacePolygon poly) {
		BoundingBox bb = new BoundingBox();
		;
		Iterator it = poly.getOuterBoundary()
				.iterator();
		while (it.hasNext()) {
			LatLon latlon = (LatLon) it.next();
			// initialize (first entry in the iterator)
			if (bb.minLat == null) {
				bb.minLat = latlon.getLatitude();
				bb.maxLat = latlon.getLatitude();
				bb.minLon = latlon.getLongitude();
				bb.maxLon = latlon.getLongitude();
			}

			// lat values
			bb.minLat = Angle.min(bb.minLat, latlon.getLatitude());
			bb.maxLat = Angle.max(bb.maxLat, latlon.getLatitude());
			// lon values
			bb.minLon = Angle.min(bb.minLon, latlon.getLongitude());
			bb.maxLon = Angle.max(bb.maxLon, latlon.getLongitude());
		}
		return bb;

	}

	/**
	 * a2s : Angle to String function
	 * 
	 * @param angle
	 * @return String representation of the Angle, in decimal degrees notation
	 */
	private String a2s(Angle angle) {
		return Double.toString(angle.getDegrees());
	}

	/**
	 * Just a basic container for bounding box Angle values
	 * 
	 * @author jean.pommier@pi-geosolutions.fr
	 *
	 */
	private class BoundingBox {
		public Angle minLat = null, maxLat = null, minLon = null, maxLon = null;
	}

}
