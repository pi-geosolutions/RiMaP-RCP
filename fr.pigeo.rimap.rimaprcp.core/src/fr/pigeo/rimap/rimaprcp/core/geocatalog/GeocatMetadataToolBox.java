package fr.pigeo.rimap.rimaprcp.core.geocatalog;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.translation.Messages;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;

@Creatable
@Singleton
public class GeocatMetadataToolBox {

	@Inject
	@Optional
	CloseableHttpClient httpClient;

	@Inject
	@Translation
	Messages tsn;

	String baseUrl;
	String resourcesServicePath;
	String catalogVersion;
	String mtdService;

	@Inject
	public GeocatMetadataToolBox(IPreferencesService prefService) {
		baseUrl = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT, null);
		resourcesServicePath = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.CATALOG_RESOURCES_SERVICE_PREF_TAG,
				RimapConstants.CATALOG_RESOURCES_SERVICE_PREF_DEFAULT, null);

		mtdService = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.CATALOG_METADATA_BY_UUID_RELPATH_PREF_TAG,
				RimapConstants.CATALOG_METADATA_BY_UUID_PREF_DEFAULT, null);

		catalogVersion = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.CATALOG_VERSION_PREF_TAG, RimapConstants.CATALOG_VERSION_PREF_DEFAULT, null);
	}

	public static String[] getAnysearchAutocompleteProposals(String text) {
		String[] proposals = new String[5];
		if (text == null || text.length() < 3) {
			return new String[] { "" };
		} else {
			for (int i = 0; i < 5; i++)
				proposals[i] = text + i;
		}
		return proposals;
	}
	
	public GeocatSearchResultSet search(String text, String sortBy, int from, int to, boolean downloadable,
			boolean dynamic, Sector sector, String extentRelation, String facetsAsSubstring) {
		// TODO : support gn2.10 also (provides only XML search service =>
		// convert to json then parse.
		// Service URL is different also

		String searchUrl = buildSearchURL(text, from, to, sortBy, "", downloadable, dynamic, sector, extentRelation,facetsAsSubstring);
		// String searchUrl =
		// this.baseUrl+"srv/"+tsn.iso3_code+"/q?_content_type=json&facet.q=&fast=index&from=1&resultType=details&sortBy=relevance&sortOrder=&to=20&any="+text;
		//System.out.println(searchUrl);
		try {
			if (httpClient != null) {
				HttpGet httpget = new HttpGet(searchUrl);
				CloseableHttpResponse response = httpClient.execute(httpget);
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						String json;
						if (catalogIsPadre1()) {
							// then we will get results in XML and have to
							// transform to json
							/*
							 * String xml = EntityUtils.toString(entity);
							 * JSONObject xmlJSONObj = XML.toJSONObject(xml);
							 * json = xmlJSONObj.toString(2);
							 */

							//
							// TODO: find a proper implementation that would
							// output exactly the same way as in GN3
							// (this one below does not convene)
							//
							String xml = EntityUtils.toString(entity);
							XmlMapper xmlMapper = new XmlMapper();
							List entries = xmlMapper.readValue(xml, List.class);

							ObjectMapper jsonMapper = new ObjectMapper();
							jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
							json = jsonMapper.writeValueAsString(entries);
						} else {
							json = EntityUtils.toString(entity);
						}

						// String json = EntityUtils.toString(entity);
						//System.out.println(json);
						ObjectMapper mapper = new ObjectMapper(); // can reuse,
																	// share
																	// globally
						GeocatSearchResultSet resultSet = mapper.readValue(json, GeocatSearchResultSet.class);

						//System.out.println("Nb results: " + resultSet.getSummary().get_count());
						return resultSet;
					}
				} finally {
					response.close();
				}
			} else {
				System.out.println("oops, httpclient is null");
			}
		} catch (Exception e) {
			// TODO: display relevant information on the result panel
			System.out.println("oops, error");
			// e.printStackTrace();
			return new GeocatSearchResultSet(e);
		}
		return null;
	}

	private String buildSearchURL(String text, int fromIndex, int toIndex, String sortBy, String sortOrder,
			boolean downloadable, boolean dynamic, Sector sector, String extentRelation, String facetsAsSubstring) {
		String extras = (dynamic ? "&dynamic=true" : "") + (downloadable ? "&download=true" : "")
				+ getExtentAsString(sector, extentRelation) + (facetsAsSubstring.length() >0 ? "&facet.q="+facetsAsSubstring:"");

		String request = this.baseUrl + getLocalizedSRVPathFragment() + "q?";
		if (catalogIsPadre1()) {
			request += "facet.q=";
		} else {
			// if (catalogVersion.equals("3") ||
			// catalogVersion.startsWith("3.")) {
			request += "_content_type=json&facet.q=&resultType=details";
		}
		request += "&fast=index&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&from=" + fromIndex + "&to=" + toIndex
				+ "&any=" + text;
		request += extras;
		return request;
	}

	protected String getExtentAsString(Sector sector, String extentRelation) {
		String extent = "";
		if (sector != null) {
			extent = "&geometry=POLYGON((";
			for (LatLon ll : sector.getCorners()) {
				extent += ll.getLongitude().degrees + "+" + ll.getLatitude().degrees + ",";
			}
			// close the loop
			LatLon ll = sector.getCorners()[0];
			extent += ll.getLongitude().degrees + "+" + ll.getLatitude().degrees;
			extent += "))";
			if (extentRelation != null) {
				extent += "&relation=" + extentRelation;
			}
		}
		return extent;
	}

	public String getResourcesServicePath() {
		return resourcesServicePath;
	}

	public String getFullMetadataViewPath(String uuid) {
		return baseUrl + getLocalizedSRVPathFragment() + mtdService + uuid;
	}

	public String getFullResourcesServicePath(String filename, String mtdId) {
		return baseUrl + getLocalizedSRVPathFragment() + resourcesServicePath + "fname=" + filename
				+ "&access=public&id=" + mtdId;
	}

	public String getLocalizedSRVPathFragment() {
		return "srv/" + tsn.iso3_code + "/";
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public boolean catalogIsPadre1() {
		return catalogVersion.equals("2") || catalogVersion.startsWith("2.");
	}

	public static String getMetadataWebLink(GeocatMetadataEntity entity) {
		String link = "http://ne-risk.pigeo.fr/geonetwork/srv/fre/md.viewer#/pigeo_simple_view/"
				+ entity.get_geonet_info()
						.getId();
		return link;
	}

	public static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
