package fr.pigeo.rimap.rimaprcp.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

	public static Date parseDate(JsonNode node, String tag) {
		Date date = null;
		if (node.has(tag)) {
			// parsing lastchange date as Date
			// Padre v1 date format
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String datestr = node.get(tag)
					.asText();
			try {
				date = df.parse(datestr);
			} catch (ParseException e) {
				// Padre v2 date format
				DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
				try {
					date = df2.parse(datestr);
				} catch (ParseException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
		return date;
	}

	public static String parseString(JsonNode node, String tag, String defaultValue) {
		String out = defaultValue;
		if (node.has(tag))
			out = node.get(tag)
					.asText();
		return out;
	}

	public static boolean parseBool(JsonNode node, String tag, boolean defaultValue) {
		boolean out = defaultValue;
		if (node.has(tag))
			out = node.get(tag)
					.asBoolean(defaultValue);
		return out;
	}

	public static int parseInt(JsonNode node, String tag, int defaultValue) {
		int out = defaultValue;
		if (node.has(tag))
			out = node.get(tag)
					.asInt(defaultValue);
		return out;
	}

	public static Double parseDouble(JsonNode node, String tag, Double defaultValue) {
		Double out = defaultValue;
		if (node.has(tag))
			out = node.get(tag)
					.asDouble(defaultValue);
		return out;
	}
}
