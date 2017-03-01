package fr.pigeo.rimap.rimaprcp.core;
/**
 * used for translation purposes. See https://techblog.ralph-schuster.eu/2012/12/30/eclipse-e4-using-the-translationservice/
 * Makes use of E4Utils class
 */

import fr.pigeo.rimap.rimaprcp.core.E4Utils;

public class Plugin {
	 
	public static final String SYMBOLIC_NAME = "fr.pigeo.rimap.rimaprcp.core";
	public static final String CONTRIBUTOR_URI = "platform:/plugin/"+SYMBOLIC_NAME;
 
	/**
	 * Translate the given key.
	 * @param key key to translate
	 * @param args variable replacements (will replace {0}, {1},... placeholders)
	 * @return translated value
	 */
	public static String translate(String key, Object... args) {
		return E4Utils.translate(key, CONTRIBUTOR_URI, args);
	}
}