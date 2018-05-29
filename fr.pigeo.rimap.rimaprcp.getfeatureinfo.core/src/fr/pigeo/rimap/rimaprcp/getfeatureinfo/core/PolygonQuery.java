package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import java.awt.Component;
import java.awt.Cursor;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContActs;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.wwj.PolygonBuilder;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.PolygonQueryableParams;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.SurfacePolygon;

/**
 * PolygonQuery : Central class for the PolygonQuery functionality. All other
 * PolygonQuery-related classes report to it and get most of the intel. from it
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
@Creatable
@Singleton
public class PolygonQuery {
	// PQ settings
	// nb digits in decimal degree representation
	int digits = 10;
	// Image format used in the WPS process
	String imageFormat = "image/tiff";
	String EPSGcode = "4326";

	// connection timeouts in seconds
	int socketTimeout = 20;
	int connectTimeout = 20;
	int requestTimeout = 120;

	@Inject
	Logger logger;
	@Inject
	WwjInstance wwj;
	@Inject
	IEclipseContext context;
	@Inject
	IEventBroker eventBroker;

	@Inject
	@Optional
	CloseableHttpClient httpClient;

	@Inject
	@Translation
	Messages messages;

	private List<IPolygonQueryableLayer> layers = new ArrayList();
	private boolean enabled;
	private PolygonBuilder pb;
	private SurfacePolygon polygon;
	private Locale locale = Locale.ENGLISH;
	private HashMap<IPolygonQueryableLayer, String> wpsResultsCache = new HashMap<>();

	@Inject
	public PolygonQuery(IEclipseContext ctx) {
		locale = (Locale) ctx.get(TranslationService.LOCALE);
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
			p.setArmed(false);
			((Component) wwj.getWwd()).setCursor(Cursor.getDefaultCursor());
		}
	}

	/* Get Contacts object for MobileService (alerting tool)*/
	public ContActs getContActs(IPolygonQueryableLayer clayer) {
		if (this.httpClient == null) {
			logger.error("HttpClient not set");
			return null;
		}
		ContActs ct=null;
		String layername = clayer.getParams()
				.getLayernames();
		PolygonQueryableParams params = clayer.getParams();
		String request = buildContActsWFSRequest(clayer, this.polygon);

		//System.out.println(request);
		String url = clayer.getWFSUrl();
		// perform the WFS query
		StringEntity entity = new StringEntity(request, ContentType.create("text/xml", Consts.UTF_8));
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(this.socketTimeout * 1000)
				.setConnectTimeout(this.connectTimeout * 1000)
				.setConnectionRequestTimeout(this.requestTimeout * 1000)
				.build();

		post.setConfig(requestConfig);

		InputStream in;

		try {
			((Component) wwj.getWwd()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			HttpResponse response = httpClient.execute(post);

			//System.out.println(response.toString());
			in = response.getEntity()
					.getContent();
			ct = new ContActs(in);

			// EntityUtils.consumeQuietly(response.getEntity());
		} catch (ClientProtocolException e) {
			logger.error(e.toString());
		} catch (SocketTimeoutException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		} finally {
			post.releaseConnection();
		}

		((Component) wwj.getWwd()).setCursor(Cursor.getDefaultCursor());
		return ct;
	}
	
	private String buildContActsWFSRequest(IPolygonQueryableLayer clayer, SurfacePolygon poly) {
		String xml = "";
		String layername = clayer.getParams().getLayernames();
		BoundingBox bb = computeEnclosingBounds(poly);
		xml = "<?xml version='1.0' encoding='UTF-8'?> \n" + 
				"<wfs:GetFeature service='WFS' version='2.0.0'\n" + 
				"    xmlns:wfs='http://www.opengis.net/wfs/2.0' xmlns:fes='http://www.opengis.net/fes/2.0'\n" + 
				"    xmlns:gml='http://www.opengis.net/gml/3.2' xmlns:sf='http://www.openplans.org/spearfish'\n" + 
				"    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" + 
				"    xsi:schemaLocation='http://www.opengis.net/wfs/2.0 http://schemas.opengis.net/wfs/2.0/wfs.xsd\n" + 
				"        http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd' outputFormat='application/json'>\n" + 
				"    <wfs:Query typeNames='"+layername+"'>\n" + 
				"        <fes:Filter>\n" + 
				"            <fes:Intersects>\n" + 
				"                    <fes:ValueReference>the_geom</fes:ValueReference>\n" + 
				"                    <gml:Polygon gml:id='polygon.1'\n" + 
				"                        srsName='http://www.opengis.net/gml/srs/epsg.xml#4326'>\n" + 
				"                        <gml:exterior>\n" + 
				"                            <gml:LinearRing>\n" + 
				"                                <!-- pairs must form a closed ring -->\n" + 
				"                                <gml:posList>"+polygonToCoordinatesList(poly, " ", " ", " ")+"</gml:posList>\n" + 
				"                            </gml:LinearRing>\n" + 
				"                        </gml:exterior>\n" + 
				"                    </gml:Polygon>\n" + 
				"            </fes:Intersects>\n" + 
				"        </fes:Filter>\n" + 
				"    </wfs:Query>\n" + 
				"</wfs:GetFeature>\n";
		logger.debug(xml);
		return xml;
	}

	/* Get stats as HTML in case we want rasterstats */
	public String getStats(IPolygonQueryableLayer layer) {
		//manages some caching, to avoid re-querying a result already retrieved once
		if (wpsResultsCache.containsKey(layer)) {
			return wpsResultsCache.get(layer);
		}
		
		if (this.httpClient == null) {
			logger.error("HttpClient not set");
			return messages.polygonquery_result_error;
		}
		String html = "";
		String layername = layer.getParams()
				.getLayernames();
		PolygonQueryableParams params = layer.getParams();
		String request = buildWPSRequest(layer, this.polygon);
		//System.out.println(request);
		String url = layer.getWPSUrl();

		// perform the WPS query
		StringEntity entity = new StringEntity(request, ContentType.create("text/xml", Consts.UTF_8));
		// entity.setChunked(true);
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(this.socketTimeout * 1000)
				.setConnectTimeout(this.connectTimeout * 1000)
				.setConnectionRequestTimeout(this.requestTimeout * 1000)
				.build();

		post.setConfig(requestConfig);

		InputStream in;

		try {
			((Component) wwj.getWwd()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			HttpResponse response = httpClient.execute(post);

			//System.out.println(response.toString());
			in = response.getEntity()
					.getContent();
			String body = IOUtils.toString(in);
			//System.out.println(body);
			html = formatJson2HTML(body, layer);

			// EntityUtils.consumeQuietly(response.getEntity());
		} catch (ClientProtocolException e) {
			logger.error(e.toString());
			return messages.polygonquery_result_error;
		} catch (SocketTimeoutException e) {
			logger.error(e.toString());
			return messages.polygonquery_result_timeout_error;
		} catch (IOException e) {
			logger.error(e.toString());
			return messages.polygonquery_result_error;
		} finally {
			post.releaseConnection();
		}

		((Component) wwj.getWwd()).setCursor(Cursor.getDefaultCursor());
		
		//Sets the result in the cache
		wpsResultsCache.put(layer, html);
		return html;
	}

	private String formatJson2HTML(String body, IPolygonQueryableLayer layer) {
		String html = "";
		
		// print header
		PolygonQueryableParams p = layer.getParams();
		String header = layer.getParams()
				.getHeaders();
		//System.out.println(header);
		if (header == null || header.isEmpty()) {
			header = messages.polygonquery_header;
		}
		html += this.formatWPSHeader(header);
		
		// print results
		ObjectMapper objectMapper = new ObjectMapper();
		layer.getParams()
				.getFields();
		try {
			JsonNode json = objectMapper.readValue(body, JsonNode.class);
			JsonNode stats = json.get("features")
					.get(0)
					.get("properties");
			Iterator<Entry<String, JsonNode>> it = stats.fields();
			while (it.hasNext()) {
				Entry<String, JsonNode> entry = it.next();
				if (layer.getParams()
						.getFields()
						.contains(entry.getKey())) {
					html += formatWPSEntry(entry.getKey(), entry.getValue()
							.asText(),
							layer.getParams()
									.getRoundValue());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		return html;
	}

	/**
	 * Formats the result using a template defined in the bunde.properties file
	 * 
	 * @param key
	 * @param value
	 * @param roundDecimals
	 * @return
	 */
	private String formatWPSEntry(String key, String value, int roundDecimals) {
		Object[] messageArguments = { messages.translate("polygonquery_" + key), tryRound(value, roundDecimals) };
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		formatter.applyPattern(messages.polygonquery_template);
		String output = formatter.format(messageArguments);
		return output;
	}

	/**
	 * Formats the results header using a template defined in the
	 * bunde.properties file
	 * 
	 * @param key
	 * @param value
	 * @param roundDecimals
	 * @return
	 */
	private String formatWPSHeader(String text) {
		Object[] messageArguments = { text };
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		formatter.applyPattern(messages.polygonquery_header_template);
		String output = formatter.format(messageArguments);
		return output;
	}

	/**
	 * Tries to convert value to a float value and round it to the given nb of
	 * decimals. If it fails, returns the original string
	 * 
	 * @param value
	 * @return
	 */
	private String tryRound(String value, int decimals) {
		try {
			BigDecimal bd = new BigDecimal(value).setScale(decimals, RoundingMode.HALF_EVEN);
			return bd.toString();
		} catch (Exception ex) {
			return value;
		}
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
		//Clear the cached results
		wpsResultsCache.clear();
		
		this.polygon = poly;
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

	public String buildWPSRequest(IPolygonQueryableLayer layer, SurfacePolygon poly) {
		String xml = "";
		String layername = layer.getParams()
				.getLayernames();
		String band = String.valueOf(layer.getParams()
				.getBandNumber());
		BoundingBox bb = computeEnclosingBounds(poly);
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><wps:Execute version=\"1.0.0\" service=\"WPS\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" "
				+ "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
				+ "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" "
				+ "xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
				+ "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">"
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
				+ "        </wps:Body>" + "      </wps:Reference>" + "    </wps:Input>"
				+ "<wps:Input><ows:Identifier>band</ows:Identifier><wps:Data><wps:LiteralData>" + band
				+ "</wps:LiteralData></wps:Data></wps:Input>" + "    <wps:Input>"
				+ "      <ows:Identifier>zones</ows:Identifier>" + "      <wps:Data>"
				+ "        <wps:ComplexData mimeType=\"application/json\">" + "			<![CDATA["
				+ this.polygonToGeoJSONStringRep(poly) + "]]>" + "		 </wps:ComplexData>" + "      </wps:Data>"
				+ "    </wps:Input>" + "  </wps:DataInputs>" + "  <wps:ResponseForm>"
				+ "    <wps:RawDataOutput mimeType=\"application/json\">"
				+ "      <ows:Identifier>statistics</ows:Identifier>" + "    </wps:RawDataOutput>"
				+ "  </wps:ResponseForm>" + "</wps:Execute>";
		return xml;
	}

	private String polygonToGeoJSONStringRep(SurfacePolygon p) {
		String json = "	{\"type\":\"FeatureCollection\", " + "\"crs\":{\"type\":\"EPSG\",\"properties\":{\"code\":\""
				+ EPSGcode + "\"}},"
				+ "\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[";
		
		json += polygonToCoordinatesList(p, "[", ",", "]");
		json += "]]}}]}";

		return json;
	}
	
	private String polygonToCoordinatesList(SurfacePolygon p, String Open, String sep, String Close) {
		String list = "";
		Iterator it = p.getOuterBoundary()
				.iterator();
		while (it.hasNext()) {
			LatLon l = (LatLon) it.next();
			list += Open + a2s(l.getLongitude()) + sep + a2s(l.getLatitude()) + Close;
		}
		// close the polygon by returning on the first node
		LatLon l = (LatLon) p.getOuterBoundary()
				.iterator()
				.next();
		list += Open + a2s(l.getLongitude()) + sep + a2s(l.getLatitude()) + Close;

		return list;
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
	
	public void hidePolygon() {
		//this.getPolygonBuilder().clear();
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
