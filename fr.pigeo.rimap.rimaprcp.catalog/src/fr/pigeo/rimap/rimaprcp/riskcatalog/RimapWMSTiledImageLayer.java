package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class RimapWMSTiledImageLayer extends WMSTiledImageLayer implements Queryable {

	private WmsLayer parent;
	private AVList params;

	// used in getFeatureInfo requests, for proper requests settings (tile size
	// and extent)
	private TextureTile currentTopLevelTile;

	public RimapWMSTiledImageLayer(AVList params) {
		super(params);
		this.params = params;
		// this.setPickEnabled(true);
		// this.setDrawTileBoundaries(true);
		// this.setDrawTileIDs(true);
	}

	public RimapWMSTiledImageLayer(Document dom, AVList params) {
		this(dom.getDocumentElement(), params);
	}

	public RimapWMSTiledImageLayer(Element domElement, AVList params) {
		this(wmsGetParamsFromDocument(domElement, params));
	}

	public RimapWMSTiledImageLayer(WMSCapabilities caps, AVList params) {
		this(wmsGetParamsFromCapsDoc(caps, params));
	}

	public RimapWMSTiledImageLayer(String stateInXml) {
		super(stateInXml);
	}

	public WmsLayer getParent() {
		return parent;
	}

	public void setParent(WmsLayer parent) {
		this.parent = parent;
	}

	// TODO: Check the queryable attribute from layertree + from Capabilities.
	@Override
	public boolean isQueryable() {
		return true;
	}

	@Override
	public Document retrieveFeatureInfo(Position pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL buildFeatureInfoRequest(Position pos) {
		RimapWMSTiledImageLayer.URLBuilder builder = new RimapWMSTiledImageLayer.URLBuilder(params);
		URL finfoUrl =null;
		if (pos != null && levels.getSector().contains(pos)) {
			try {
				finfoUrl = builder.getFinfoURL(pos, this.currentTopLevelTile.getSector().getDeltaLonDegrees(),
						this.currentTopLevelTile.getWidth());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return finfoUrl;
	}

	/**
	 * Override needed to get the currentTopLevelTile, used for getFeatureInfo
	 * requests
	 */
	@Override
	protected void draw(DrawContext dc) {
		this.assembleTiles(dc); // Determine the tiles to draw.
		if (this.currentTiles.size() >= 1) {
			TextureTile[] sortedTiles = new TextureTile[this.currentTiles.size()];
			sortedTiles = this.currentTiles.toArray(sortedTiles);
			Arrays.sort(sortedTiles, levelComparer);
			this.currentTopLevelTile = sortedTiles[sortedTiles.length - 1];
		}
		super.draw(dc);
	}

	public static class URLBuilder extends WMSTiledImageLayer.URLBuilder {
		private static final String MAX_VERSION = "1.3.0";

		private final String layerNames;
		private final String wmsVersion;
		private final String crs;
		private final String wmsGetMap;
		public String URLTemplate;

		public URLBuilder(AVList params) {
			super(params);
			this.layerNames = params.getStringValue(AVKey.LAYER_NAMES);
			String version = params.getStringValue(AVKey.WMS_VERSION);
			wmsGetMap = params.getStringValue(AVKey.GET_MAP_URL);

			this.wmsVersion = version;
			this.crs = "&srs=EPSG:4326";
		}

		/*
		 * Hack together a get featureinfo request with the template below and
		 * the handy StringBuffer
		 * 
		 * http://stadler.hba.marine.csiro
		 * .au:8080/geoserver/wms?REQUEST=GetFeatureInfo
		 * &EXCEPTIONS=application%2Fvnd
		 * .ogc.se_xml&BBOX=131.17%2C-49.467984%2C159.09
		 * %2C-27.710016&X=342&Y=189&INFO_FORMAT
		 * =text%2Fplain&QUERY_LAYERS=ECOBASE%3AS15_1990_2009_GEOM
		 * &FEATURE_COUNT=100&
		 * Srs=EPSG%3A4326&Layers=ECOBASE%3AS15_1990_2009_GEOM&Styles
		 * =&WIDTH=512&HEIGHT=399&format=image%2Fpng
		 */
		public URL getFinfoURL(Position pos, double delta, int width) throws MalformedURLException {
			// Buffer position
			final double eps = delta;
			final double lat = pos.getLatitude().degrees;
			final double lon = pos.getLongitude().degrees;
			final double maxx = lat;
			final double maxy = lon + eps;
			final double minx = lat - eps;
			final double miny = lon;

			StringBuffer sb = new StringBuffer(wmsGetMap);
			sb.append("?service=wms");
			sb.append("&request=GetFeatureInfo");
			sb.append("&version=").append(this.wmsVersion);
			sb.append(this.crs);
			sb.append("&info_format=text/html");
			sb.append("&bbox=");
			sb.append(minx);
			sb.append(",");
			sb.append(miny);
			sb.append(",");
			sb.append(maxx);
			sb.append(",");
			sb.append(maxy);
			sb.append("&layers=" + layerNames);
			sb.append("&query_layers=" + layerNames);
			sb.append("&width=" + width);
			sb.append("&height=" + width);
			sb.append("&x=0");
			sb.append("&y=0");
			sb.append("&feature_count=50");
			return new URL(sb.toString());
		}
	}

}
