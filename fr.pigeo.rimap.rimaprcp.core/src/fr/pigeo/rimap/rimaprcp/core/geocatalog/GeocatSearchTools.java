package geocatalog;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;

@Creatable
@Singleton
public class GeocatSearchTools {

	@Inject
	@Optional
	CloseableHttpClient httpClient;
	
	String baseUrl;
	String resourcesServicePath;
	
	@Inject
	public GeocatSearchTools(IPreferencesService prefService) {
		baseUrl = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT,
				null);
		resourcesServicePath = baseUrl +
				prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.CATALOG_RESOURCES_SERVICE_PREF_TAG,
				RimapConstants.CATALOG_RESOURCES_SERVICE_PREF_DEFAULT, null);
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

	public GeocatSearchResultSet search(String text) {
		//TODO : aggregate the URL using prefs & form text comtent
		//TODO : support gn2.10 also (provides only XML search service => convert to json then parse. 
		// 		Service URL is different also
		String searchUrl = this.baseUrl+"srv/fre/q?_content_type=json&facet.q=&fast=index&from=1&resultType=details&sortBy=relevance&sortOrder=&to=20&any="+text;
		System.out.println(searchUrl);
		try {
			if (httpClient != null) {
				HttpGet httpget = new HttpGet(searchUrl);
				CloseableHttpResponse response = httpClient.execute(httpget);
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						
						String json = EntityUtils.toString(entity);
						//System.out.println(json);
						ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
						GeocatSearchResultSet resultSet = mapper.readValue(json, GeocatSearchResultSet.class);

						System.out.println("Nb results: "+resultSet.getSummary().get_count());
						return resultSet;
					}
				} finally {
					response.close();
				}
			} else {
				System.out.println("oops, httpclient is null");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getResourcesServicePath() {
		return resourcesServicePath;
	}
	public String getFullResourcesServicePath() {
		return baseUrl + resourcesServicePath;
	}

	public String getBaseUrl() {
		return baseUrl;
	}
}
