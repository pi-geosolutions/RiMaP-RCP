package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.catalog.INode;

public abstract class AbstractNode implements INode {
	protected Date parseDate(JsonNode node, String tag) {
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

	protected String parseString(JsonNode node, String tag, String defaultValue) {
		String out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asText();
		return out;
	}

	protected boolean parseBool(JsonNode node, String tag, boolean defaultValue) {
		boolean out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asBoolean(defaultValue);
		return out;
	}

	protected int parseInt(JsonNode node, String tag, int defaultValue) {
		int out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asInt(defaultValue);
		return out;
	}

	protected Double parseDouble(JsonNode node, String tag, Double defaultValue) {
		Double out = defaultValue;
		if (node.has(tag))
			out = node.get(tag).asDouble(defaultValue);
		return out;
	}
}
