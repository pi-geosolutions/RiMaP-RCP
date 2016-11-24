package fr.pigeo.rimap.rimaprcp.admintools.core.editors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.eclipse.e4.core.services.log.Logger;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.annotations.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.pigeo.rimap.rimaprcp.admintools.core.constants.AdminToolsConstants;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.NodeUtils;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.PadreCatalog;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.CatalogConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.internal.LayerType;

public class PadreCatalogEditor {
	@Inject
	IResourceService resourceService;

	PadreCatalog catalog;
	JsonNode jsonOriginal, jsonCurrent;
	private String childrentag = "children", layertree_service_url;
	private boolean dirty = false;

	@Inject
	Logger logger;

	@Inject
	public PadreCatalogEditor(PadreCatalog catalog, @Optional IPreferencesService prefsService) {
		if (prefsService != null) {
			childrentag = prefsService.getString(CatalogConstants.PREFERENCES_NODE, CatalogConstants.CHILDREN_PREF_TAG,
					CatalogConstants.CHILDREN_PREF_DEFAULT, null);
			String baseurl = prefsService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
					RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT, null);
			String layertreeService = prefsService.getString(AdminToolsConstants.PREFERENCES_NODE,
					AdminToolsConstants.MAINCATALOG_LAYERTREE_ADMIN_RELPATH_PREF_TAG,
					AdminToolsConstants.MAINCATALOG_LAYERTREE_ADMIN_RELPATH_PREF_DEFAULT, null);
			layertree_service_url = baseurl + layertreeService;
		}
		this.catalog = catalog;
	}

	public String toString(boolean full) {
		String out = catalog.getRootNode()
				.getName();
		if (full) {
			out = this.prettyPrint(getLayertreeAsJson());
		}
		return out;
	}

	private JsonNode getLayertreeAsJson() {
		// Lazy init. We initialize working json (current) to original value
		if (this.jsonCurrent == null) {
			if (this.jsonOriginal == null) {
				// we get the resource from the web (imperative !)
				byte[] b = resourceService.getResource(layertree_service_url, 9);
				if (b != null) {
					String lt = new String(b, StandardCharsets.UTF_8);
					ObjectMapper objectMapper = new ObjectMapper();
					try {
						jsonOriginal = objectMapper.readValue(lt, JsonNode.class);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			jsonCurrent = jsonOriginal.deepCopy();
		}
		return jsonCurrent;
	}

	private String prettyPrint(JsonNode node) {
		String out = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			DefaultIndenter defind = new DefaultIndenter("    ", "\n");
			DefaultPrettyPrinter pp = new DefaultPrettyPrinter().withArrayIndenter(defind)
					.withObjectIndenter(defind);
			out = mapper.writer(pp)
					.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out = e.getLocalizedMessage();
		}
		return out;
	}

	public String toString() {
		return toString(false);
	}

	public void reset() {
		jsonCurrent = null;
		jsonOriginal = null;
		dirty = false;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void fixLayertree() {
		fixNode(this.jsonCurrent);
	}

	public void fixNode(JsonNode node) {
		// special treatment for the rootnode....
		if (!node.has("type") && node.has(this.childrentag)) {
			tryFixChildren(node);
			return;
		}

		if (!node.has("type")) {
			// shouldn't happen
			return;
		}

		// convert e.g. 'folder' to LayerType.FOLDER
		LayerType type = LayerType.valueOf(node.get("type")
				.asText()
				.toUpperCase());

		switch (type) {
		case FOLDER:
			fixFolder(node);
			tryFixChildren(node);
			break;
		case WMS:
			fixWmsNode(node);
			break;
		case CHART:

			logger.info("TODO : fix chart layers");
			break;
		default:
			logger.warn("encountered weird layertree node while parsing Layertree from json: " + type.toString());
			break;
		}

	}

	private void fixWmsNode(JsonNode node) {
		// Fix URL / layername issues
		// e.g. http://afo.pigeo.fr/geoserver-prod/afo/wms? && afo:afolayer
		// present namespace redundancy.
		// http://afo.pigeo.fr/geoserver-prod/wms? && afo:afolayer uses global
		// WMS service.
		// we prefer http://afo.pigeo.fr/geoserver-prod/afo/wms? && afolayer
		String url = NodeUtils.parseString(node, "url", "");
		String layer = NodeUtils.parseString(node, "layers", "");
		String name = NodeUtils.parseString(node, "text", "no name");
		String[] layerparts = layer.split(":");

		//fixing URL
		//replace all double / with single ones, except for http:// part
		ObjectNode o = (ObjectNode) node;
		url = url.replaceAll("(?<!:)(\\/){2}", "/");
		o.put("url", url);
		
		//deal with the Philippe's case : the namespace is given in the layer's anme. 
		//And sometime in the url at the same time...
		if (layerparts.length == 2) { 
			//ObjectNode o = (ObjectNode) node;
			logger.info("Fixing Node " + name + "[LAYERS=" + layer + " / URL=" + url + "]");

			//fixing layer name
			String ns = layerparts[0];
			String l = layerparts[1];
			o.put("layers", l);
			
			String u = url;
			//add the namespace in the URL if Philippe hasn't put it yet, redundantly
			if (!url.matches("(.*)(\\/" + ns + "\\/wms\\?)")) {
				u = url.substring(0, url.length() - 4) + ns + "/wms?";
				o.put("url", u);
			}

			logger.info("       to [LAYERS=" + l + " / URL=" + u + "]");
			this.dirty = true;
		}
	}

	private void fixFolder(JsonNode node) {
		// TODO implement Folder fixing methods
	}

	private void tryFixChildren(JsonNode node) {
		JsonNode children = node.get(this.childrentag);
		if (children == null) {
			return;
		}
		if (children.isArray()) {
			Iterator<JsonNode> itr = children.iterator();
			while (itr.hasNext()) {
				JsonNode child = itr.next();
				fixNode(child);
			}
		}
	}

	/*
	 * JACKSON data manipulation
	 */

	private static final ObjectMapper SORTED_MAPPER = new ObjectMapper();
	static {
		SORTED_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	}

	private String convertNode(final JsonNode node) throws JsonProcessingException {
		final Object obj = SORTED_MAPPER.treeToValue(node, Object.class);
		final String json = SORTED_MAPPER.writeValueAsString(obj);
		return json;
	}

}
