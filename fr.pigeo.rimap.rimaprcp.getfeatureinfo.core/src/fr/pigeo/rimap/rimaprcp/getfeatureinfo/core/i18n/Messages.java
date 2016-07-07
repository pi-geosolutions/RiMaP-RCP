package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n;

import java.lang.reflect.Field;

public class Messages {
	// WPS Strings
	public String polygonquery_count;
	public String polygonquery_min;
	public String polygonquery_max;
	public String polygonquery_sum;
	public String polygonquery_avg;
	public String polygonquery_stddev;
	public String polygonquery_header;
	public String polygonquery_header_template;
	public String polygonquery_template;
	public String polygonquery_presentation;

	/**
	 * Translates the values using Eclipse's i18n translation system +
	 * reflection to match the keys with the variables names in class Messages
	 * 
	 * @param key
	 * @return the translated value if available. If not or if an error is
	 *         raised, returns the raw value (key)
	 */
	public String translate(String key) {
		Field field;
		String translated;
		try {
			field = this.getClass()
					.getDeclaredField(key);
			field.setAccessible(true);
			translated = (String) field.get(this);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// e.printStackTrace();
			return key;
		}
		return translated;
	}
}