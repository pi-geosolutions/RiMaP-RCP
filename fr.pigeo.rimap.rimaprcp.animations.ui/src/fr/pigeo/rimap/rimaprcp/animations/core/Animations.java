package fr.pigeo.rimap.rimaprcp.animations.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import fr.pigeo.rimap.rimaprcp.animations.AnimationsEventConstants;
import fr.pigeo.rimap.rimaprcp.animations.constants.AnimationsConstants;
import fr.pigeo.rimap.rimaprcp.animations.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.resource.WebUsageLevel;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

/**
 * Animations : Central class for the Animations functionality. All other
 * Animations-related classes report to it and get most of the intel. from it
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
@Creatable
@Singleton
public class Animations {

	@Inject
	Logger logger;
	@Inject
	WwjInstance wwj;
	@Inject
	IEclipseContext context;
	@Inject
	IEventBroker eventBroker;
	@Inject
	CloseableHttpClient httpClient;
	@Inject
	IPreferencesService prefsService;
	@Inject
	IResourceService resourceService;

	@Inject
	@Translation
	Messages messages;

	private String animationsServiceUrl, animationsListfileServiceUrl, animationsListfileParamName, 
	animationsGetImageServiceUrl,animationsGetImageParamName,animationsGetImageParamPath;
	private List<AnimationsSource> AnimationDatasets;

	@Inject
	public Animations(IPreferencesService prefsService) {
		// defines animationsServiceUrl, used for the 1st step : get the list of
		// available datasets for animations
		String baseurl = prefsService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT, null);
		String animationsService = prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_LIST_RELPATH_PREF_TAG,
				AnimationsConstants.ANIMATIONS_LIST_RELPATH_PREF_DEFAULT, null);
		animationsServiceUrl = baseurl + animationsService;

		// defines animationsListfileServiceUrl & animationsListfileParamName
		// used for 2nd step :
		// list the files available for a given dataset
		String animationsListfilesService = prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_LISTFILES_RELPATH_PREF_TAG,
				AnimationsConstants.ANIMATIONS_LISTFILES_RELPATH_PREF_DEFAULT, null);
		animationsListfileServiceUrl = baseurl + animationsListfilesService;
		animationsListfileParamName = prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_LISTFILES_PARAM_NAME_PREF_TAG,
				AnimationsConstants.ANIMATIONS_LISTFILES_PARAM_NAME_PREF_DEFAULT, null);

		animationsGetImageServiceUrl = baseurl + prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_GETIMAGE_RELPATH_PREF_TAG,
				AnimationsConstants.ANIMATIONS_GETIMAGE_RELPATH_PREF_DEFAULT, null);
		animationsGetImageParamName = prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_GETIMAGE_PARAM_NAME_PREF_TAG,
				AnimationsConstants.ANIMATIONS_GETIMAGE_PARAM_NAME_PREF_DEFAULT, null);
		animationsGetImageParamPath = prefsService.getString(AnimationsConstants.PREFERENCES_NODE,
				AnimationsConstants.ANIMATIONS_GETIMAGE_PARAM_PATH_PREF_TAG,
				AnimationsConstants.ANIMATIONS_GETIMAGE_PARAM_PATH_PREF_DEFAULT, null);
	}

	/**
	 * Gets the info online and loads the service.
	 * Be sure the DI has already been performed BEFORE calling this method.
	 * 
	 * @return
	 */
	public boolean load() {
		if (AnimationDatasets != null && !AnimationDatasets.isEmpty()) {
			// we assume it has already been loaded
			logger.info("Animations Datasets have already been loaded. Loading from memory...");
			return true;
		}

		if (this.httpClient == null) {
			logger.error(messages.animations_service_error_httpclient);
			return false;
		}
		if (this.animationsServiceUrl == null) {
			logger.error(messages.animations_service_error_urlnotset);
			return false;
		}

		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead
		// TODO: use resource service instead

		HttpGet httpget = new HttpGet(animationsServiceUrl);
		CloseableHttpResponse response;
		Document doc = null;
		List<AnimationsSource> aslist = new ArrayList();
		try {
			response = httpClient.execute(httpget);
			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				SAXBuilder sxb = new SAXBuilder();
				try {
					if (entity != null) {
						/*
						 * // System.out.println(response.toString());
						 * InputStream in = response.getEntity()
						 * .getContent();
						 * String body = IOUtils.toString(in);
						 * logger.info(body);
						 */

						doc = sxb.build(entity.getContent());
						Element root = doc.getRootElement();
						Element datasets = root.getChild("datasets");
						List<Element> datasetlist = datasets.getChildren("dataset");
						Iterator<Element> it = datasetlist.iterator();
						while (it.hasNext()) {
							Element ds = it.next();
							// AnimationsSource as = new AnimationsSource(ds);
							aslist.add(new AnimationsSource(ds));
						}
					}
				} catch (UnsupportedOperationException e) {
					logger.error(e.getLocalizedMessage());
					return false;
				} catch (JDOMException e) {
					logger.error(e.getLocalizedMessage());
					return false;
				} finally {
					response.close();
				}
			}
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return false;
		}
		this.AnimationDatasets = aslist;
		return true;
	}

	/**
	 * Get the list of available files for a given dataset. The service returns
	 * a JSON format
	 * 
	 * @param dataset
	 * @return
	 */
	public boolean loadDataset(AnimationsSource dataset) {
		if (this.httpClient == null) {
			logger.error(messages.animations_service_error_httpclient);
			return false;
		}
		if (this.animationsListfileServiceUrl == null || this.animationsListfileParamName == null) {
			logger.error(messages.animations_service_error_urlnotset);
			return false;
		}
		String getUrl = this.animationsListfileServiceUrl + "?" + this.animationsListfileParamName + "="
				+ dataset.getId();
		logger.info("[Animations] Querying " + getUrl);
		HttpGet httpget = new HttpGet(getUrl);
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(httpget);
			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK) {
				InputStream in = response.getEntity()
						.getContent();
				String body = IOUtils.toString(in);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode json = objectMapper.readTree(body);
				dataset.setServerPath(json.get("path")
						.asText());
				dataset.setExtension(json.get("extension")
						.asText());
				ArrayNode records = (ArrayNode) json.get("record");
				Iterator<JsonNode> it = records.elements();
				List<String> filenames = new ArrayList();
				while (it.hasNext()) {
					JsonNode rec = it.next();
					filenames.add(rec.get("name")
							.asText());
					//System.out.println(rec.get("name").asText());
				}
				dataset.setFilenames(filenames);
				eventBroker.send(AnimationsEventConstants.ANIMATIONS_DATASET_CONFIGURED, dataset);
				this.preloadImages(dataset);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
			return false;
		}

		return true;
	}

	/**
	 * Loads the images through ResourceService, to make sure they are locally
	 * available before we begin to play the animation
	 * 
	 * @param dataset
	 */
	private void preloadImages(AnimationsSource dataset) {
		Iterator<String> it = dataset.getFilenames()
				.iterator();
		String url = animationsGetImageServiceUrl + "?" 
				+ animationsGetImageParamPath + "=" + dataset.getServerPath() +"&"
				+ animationsGetImageParamName + "=";
		int count = 0;
		while (it.hasNext()) {
			String name = it.next();
			resourceService.getResource(url+name, WebUsageLevel.PRIORITY_LOCAL);
			count++;
			eventBroker.send(AnimationsEventConstants.ANIMATIONS_FILES_LOAD_PROGRESS, count);
		}

		eventBroker.send(AnimationsEventConstants.ANIMATIONS_FILES_LOAD_COMPLETE, dataset);
	}

	public List<AnimationsSource> getSources() {
		return AnimationDatasets;
	}

}
