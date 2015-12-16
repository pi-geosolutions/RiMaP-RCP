/**
 * 
 */
package fr.pigeo.rimap.rimaprcp.riskcatalog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Jean Pommier (jean.pommier@pi-geosolutions.fr)
 *
 */

public abstract class AbstractLayer {
	protected AbstractLayer parent=null;
	private LayerType type = LayerType.ABSTRACT;

	private String name = "abstract layer"; // should never appear !

	public AbstractLayer() {

	}

	public void loadFromJson(JsonNode node) {
		// implement in inherited Layer objects
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractLayer getParent() {
		return parent;
	}

	public void setParent(AbstractLayer parent) {
		this.parent = parent;
	}
	
	public LayerType getType() {
		return type;
	}

	public Date parseDate(JsonNode node, String tag) {
		Date date = null;
		if (node.has(tag)) {
			// parsing lastchange date as Date
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String datestr = node.get(tag).asText();
			try {
				date = df.parse(datestr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return date;
	}

	public String parseString(JsonNode node, String tag, String defaultValue) {
		String out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asText();
		return out;
	}
	
	public boolean parseBool(JsonNode node, String tag, boolean defaultValue) {
		boolean out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asBoolean(defaultValue);
		return out;
	}

	public int parseInt(JsonNode node, String tag, int defaultValue) {
		int out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asInt(defaultValue);
		return out;
	}
	public Double parseDouble(JsonNode node, String tag, Double defaultValue) {
		Double out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asDouble(defaultValue);
		return out;
	}
}
