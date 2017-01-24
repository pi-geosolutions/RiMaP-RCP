package geocatalog;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Creatable
@Singleton
public class GeocatSearchTools {

	@Inject
	@Optional
	CloseableHttpClient httpClient;
	
	@Inject
	public GeocatSearchTools() {

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
		String searchUrl = "http://ne-risk.pigeo.fr/geonetwork/srv/fre/q?_content_type=json&facet.q=&fast=index&from=1&resultType=details&sortBy=relevance&sortOrder=&to=20&any=population";
		byte[] out = null;
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

						System.out.println("Selected results: "+resultSet.get_selected());
						return resultSet;
						//out = EntityUtils.toByteArray(entity);
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
}
