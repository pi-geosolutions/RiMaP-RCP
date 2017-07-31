package fr.pigeo.rimap.rimaprcp.core.services.catalog.worldwind.layers;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.WmsNode;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IQueryableLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.PolygonQueryableParams;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerDimension;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWXML;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class RimapWMSTiledImageLayer extends WMSTiledImageLayer implements IQueryableLayer, IPolygonQueryableLayer {
	private WmsNode parent;
	private AVList params;
	private static final String[] formatOrderPreference = new String[] { "image/png", "image/dds", "image/jpeg" };

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
		this(getParamsFromCapsDoc(caps, params));
	}

	public RimapWMSTiledImageLayer(String stateInXml) {
		super(stateInXml);
	}

	/**
	 * Extracts parameters necessary to configure the layer from a WMS
	 * capabilities document.
	 *
	 * @param caps
	 *            the capabilities document.
	 * @param params
	 *            an attribute-value list in which to place the extracted
	 *            parameters. May be null, in which case a
	 *            new attribute-value list is created and returned.
	 *
	 * @return the attribute-value list passed as the second parameter, or the
	 *         list created if the second parameter is
	 *         null.
	 *
	 * @throws IllegalArgumentException
	 *             if the capabilities document reference is null.
	 */
	// @Override
	public static AVList getParamsFromCapsDoc(WMSCapabilities caps, AVList params) {
		if (caps == null) {
			String message = Logging.getMessage("nullValue.WMSCapabilities");
			Logging.logger()
					.severe(message);
			throw new IllegalArgumentException(message);
		}

		if (params == null)
			params = new AVListImpl();

		WMSLayerCapabilities layerCaps = caps.getLayerByName(params.getStringValue(AVKey.LAYER_NAMES));
		if (params.hasKey(AVKey.LAYER_NAMES) && layerCaps != null) {
			try {
				DataConfigurationUtils.getWMSLayerConfigParams(caps, formatOrderPreference, params);
				List<WMSLayerDimension> timeDimensions = layerCaps.getDimensions()
						.stream()
						.filter(dim -> dim.getName()
								.equalsIgnoreCase("time"))
						.collect(Collectors.toList());
				// we shouldn't get more than one time dimension
				WMSLayerDimension timeDimension = timeDimensions.isEmpty() ? null : timeDimensions.get(0);
				if (timeDimension != null) {
					params.setValue(RimapAVKey.LAYER_TIME_DIMENSION_ENABLED, true);
					params.setValue(RimapAVKey.LAYER_TIME_DIMENSION_DEFAULT_VALUE, timeDimension.getDefaultValue());
					params.setValue(RimapAVKey.LAYER_TIME_DIMENSION_VALUES,
							timeDimension.getField("CharactersContent"));
//					System.out.format("Layer %s has time dimension. Its avalable values are %s",
//							params.getStringValue(AVKey.LAYER_NAMES),
//							params.getStringValue(RimapAVKey.LAYER_TIME_DIMENSION_VALUES));
				}
			} catch (IllegalArgumentException e) {
				String message = Logging.getMessage("WMS.MissingLayerParameters");
				Logging.logger()
						.log(java.util.logging.Level.SEVERE, message, e);
				throw new IllegalArgumentException(message, e);
			} catch (WWRuntimeException e) {
				String message = Logging.getMessage("WMS.MissingCapabilityValues");
				Logging.logger()
						.log(java.util.logging.Level.SEVERE, message, e);
				throw new IllegalArgumentException(message, e);
			}
		} else {
			guessLayerParams(caps, params);
		}

		setFallbacks(params);

		// Setup WMS URL builder.
		params.setValue(AVKey.WMS_VERSION, caps.getVersion());
		params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder(params));
		// Setup default WMS tiled image layer behaviors.
		params.setValue(AVKey.USE_TRANSPARENT_TEXTURES, true);

		return params;
	}

	/*
	 * Tries to guess params from scratch, i.e. without the help of the
	 * capabilities.
	 * Some layers are not published in the caps, but exist nonetheless
	 * Of course, it means we won't be able to pre-detect erroneous layers...
	 * The biggest part is taken from
	 * DataConfigurationUtils.getWMSLayerConfigParams
	 */
	public static void guessLayerParams(WMSCapabilities caps, AVList params) {
		// set defaults
		Angle delta = Angle.fromDegrees(36);
		params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(delta, delta));
		params.setValue(AVKey.TILE_WIDTH, 256);
		params.setValue(AVKey.TILE_HEIGHT, 256);
		params.setValue(AVKey.FORMAT_SUFFIX, ".png");
		params.setValue(AVKey.NUM_LEVELS, 19); // approximately 0.1 meters per
												// pixel
		params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);

		// taken from DataConfigurationUtils.getWMSLayerConfigParams:

		params.setValue(AVKey.COORDINATE_SYSTEM, "EPSG:4326");
		// should be already set
		// params.setValue(AVKey.DISPLAY_NAME, "hand made layer");
		String layerNames = params.getStringValue(AVKey.LAYER_NAMES);
		String styleNames = params.getStringValue(AVKey.STYLE_NAMES);
		if (layerNames == null || layerNames.length() == 0) {
			String message = Logging.getMessage("nullValue.WMSLayerNames");
			Logging.logger()
					.severe(message);
			throw new IllegalArgumentException(message);
		}
		params.setValue(AVKey.DATASET_NAME, layerNames);

		// Get the GET_MAP_URL from the WMS getMapRequest URL.
		String mapRequestURIString = caps.getRequestURL("GetMap", "http", "get");
		if (params.getValue(AVKey.GET_MAP_URL) == null) {
			params.setValue(AVKey.GET_MAP_URL, mapRequestURIString);
		}
		mapRequestURIString = params.getStringValue(AVKey.GET_MAP_URL);
		// Throw an exception if there's no GET_MAP_URL property, or no
		// getMapRequest URL in the WMS Capabilities.
		if (mapRequestURIString == null || mapRequestURIString.length() == 0) {
			Logging.logger()
					.severe("WMS.RequestMapURLMissing");
			throw new WWRuntimeException(Logging.getMessage("WMS.RequestMapURLMissing"));
		}

		// Get the GET_CAPABILITIES_URL from the WMS getCapabilitiesRequest URL.
		String capsRequestURIString = caps.getRequestURL("GetCapabilities", "http", "get");
		if (params.getValue(AVKey.GET_CAPABILITIES_URL) == null) {
			params.setValue(AVKey.GET_CAPABILITIES_URL, capsRequestURIString);
		}

		// Define the SERVICE from the GET_MAP_URL property.
		params.setValue(AVKey.SERVICE, params.getValue(AVKey.GET_MAP_URL));
		String serviceURL = params.getStringValue(AVKey.SERVICE);
		if (serviceURL != null) {
			params.setValue(AVKey.SERVICE, WWXML.fixGetMapString(serviceURL));
		}

		// Define the SERVICE_NAME as the standard OGC WMS service string.
		if (params.getValue(AVKey.SERVICE_NAME) == null) {
			params.setValue(AVKey.SERVICE_NAME, OGCConstants.WMS_SERVICE_NAME);
		}

		// Define the WMS VERSION as the version fetched from the Capabilities
		// document.
		String versionString = caps.getVersion();
		if (params.getValue(AVKey.WMS_VERSION) == null) {
			params.setValue(AVKey.WMS_VERSION, versionString);
		}
		// Form the cache path DATA_CACHE_NAME from a set of unique WMS
		// parameters.
		if (params.getValue(AVKey.DATA_CACHE_NAME) == null) {
			try {
				URI mapRequestURI = new URI(mapRequestURIString);
				String cacheName = WWIO.formPath(mapRequestURI.getAuthority(), mapRequestURI.getPath(), layerNames,
						styleNames);
				params.setValue(AVKey.DATA_CACHE_NAME, cacheName);
			} catch (URISyntaxException e) {
				String message = Logging.getMessage("WMS.RequestMapURLBad", mapRequestURIString);
				Logging.logger()
						.log(java.util.logging.Level.SEVERE, message, e);
				throw new WWRuntimeException(message);
			}
		}

		// Determine image format to request.
		if (params.getStringValue(AVKey.IMAGE_FORMAT) == null) {
			String imageFormat = chooseImageFormat(caps.getImageFormats()
					.toArray(), formatOrderPreference);
			params.setValue(AVKey.IMAGE_FORMAT, imageFormat);
		}

		// Throw an exception if we cannot determine an image format to request.
		if (params.getStringValue(AVKey.IMAGE_FORMAT) == null) {
			Logging.logger()
					.severe("WMS.NoImageFormats");
			throw new WWRuntimeException(Logging.getMessage("WMS.NoImageFormats"));
		}

		// TODO: improve : get the capabilities global extent
		params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
	}

	public WmsNode getParent() {
		return parent;
	}

	public void setParent(WmsNode parent) {
		this.parent = parent;
	}

	// TODO: Check the queryable attribute from Capabilities too.
	@Override
	public boolean isQueryable() {
		// default
		boolean isqueryable = true;

		// check on parent (layertree) settings
		if (this.parent != null) {
			isqueryable = isqueryable && this.parent.isQueryable();
		}
		return isqueryable;
	}

	@Override
	public URL buildFeatureInfoRequest(Position pos, String locale) {
		RimapWMSTiledImageLayer.URLBuilder builder = new RimapWMSTiledImageLayer.URLBuilder(params);
		URL finfoUrl = null;
		if (pos != null && levels.getSector()
				.contains(pos)) {
			try {
				finfoUrl = builder.getFinfoURL(pos, this.currentTopLevelTile.getSector()
						.getDeltaLonDegrees(), this.currentTopLevelTile.getWidth(), locale.toLowerCase());
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
		private String currentDateTime = null;
		public String URLTemplate;

		public URLBuilder(AVList params) {
			super(params);
			this.layerNames = params.getStringValue(AVKey.LAYER_NAMES);
			String version = params.getStringValue(AVKey.WMS_VERSION);
			wmsGetMap = params.getStringValue(AVKey.GET_MAP_URL);

			this.wmsVersion = version;
			this.crs = "&srs=EPSG:4326";
		}

		public URL getURL(Tile tile, String altImageFormat) throws MalformedURLException {
			URL url = super.getURL(tile, altImageFormat);
			if (this.currentDateTime!=null) {
				String str = url.toString();
				str += "&TIME=" + this.currentDateTime;
				url = new java.net.URL(str.replace(" ", "%20"));
			}

			return url;
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
		public URL getFinfoURL(Position pos, double delta, int width, String locale) throws MalformedURLException {
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
			sb.append("&lang=" + locale); // custom parameter to enable
											// locale-sensible query results
			sb.append("&version=")
					.append(this.wmsVersion);
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

		public void setDateTime(String currentTime) {
			this.currentDateTime=currentTime;
		}
	}

	@Override
	public boolean isPolygonQueryable() {
		return (boolean) this.getValue(RimapAVKey.LAYER_ISPOLYGONQUERYABLE);
	}

	@Override
	public PolygonQueryableParams getParams() {
		return (PolygonQueryableParams) this.getValue(RimapAVKey.LAYER_POLYGONQUERYPARAMS);
	}

	@Override
	public void setParams(PolygonQueryableParams params) {
		this.setValue(RimapAVKey.LAYER_POLYGONQUERYPARAMS, params);
	}

	@Override
	public String getWPSUrl() {
		// Assumes a GeoServer WMS Url pattern
		String pattern = "(?i)(/wms)(\\?)?$";
		String url = this.parent.getUrl()
				.replaceAll(pattern, "/wps");
		return url;
	}

	protected static String chooseImageFormat(Object[] formats, String[] formatOrderPreference) {
		if (formats == null || formats.length == 0) {
			return null;
		}

		// No preferred formats specified; just use the first in the caps list.
		if (formatOrderPreference == null || formatOrderPreference.length == 0) {
			return formats[0].toString();
		}

		for (String s : formatOrderPreference) {
			for (Object f : formats) {
				if (f.toString()
						.equalsIgnoreCase(s)) {
					return f.toString();
				}
			}
		}

		return formats[0].toString(); // No preferred formats recognized; just
										// use the first in the caps list.
	}

	// for now, takes into account time change (WMSTime layers)
	public void refresh(boolean clearCache) {
		AVList avl = (AVList) this.getValue(AVKey.CONSTRUCTION_PARAMETERS);
		URLBuilder builder = (URLBuilder) avl.getValue(AVKey.TILE_URL_BUILDER);
		if (avl.hasKey(RimapAVKey.LAYER_TIME_DIMENSION_ENABLED)) {
			String currentTime = avl.hasKey(RimapAVKey.LAYER_TIME_DIMENSION_CURRENT_VALUE)
					? avl.getStringValue(RimapAVKey.LAYER_TIME_DIMENSION_CURRENT_VALUE)
					: avl.getStringValue(RimapAVKey.LAYER_TIME_DIMENSION_DEFAULT_VALUE);

			builder.setDateTime(currentTime);
		}
		this.setExpiryTime(System.currentTimeMillis()-1);
	}
}
