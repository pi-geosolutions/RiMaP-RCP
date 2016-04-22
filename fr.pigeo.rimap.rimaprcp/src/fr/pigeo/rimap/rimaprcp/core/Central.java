package fr.pigeo.rimap.rimaprcp.core;

import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.log.Logger;

@Singleton
@Creatable
public class Central {
	@Inject Logger logger;
	
	private Hashtable<String, Object> references;

	public Central() {
		this.references = new Hashtable<String, Object>();
	}
	
	public Object get(String key) {
		return references.get(key);
	}
	
	/**
	 * Sets the value for an already initialized key. The key must be initialized using init(key) first
	 * (avoids crushing accidentally a value already used by another part of the app)
	 * @param key
	 * @param val
	 */
	public void update(String key, Object val) {
		if (!references.containsKey(key)) {
			logger.warn("Key "+key+"entry is not initialized. Please use append function instead");
			return;
		}
		references.put(key, val);
	}
	
	/**
	 * See update function
	 * @param key
	 */
	public void append(String key, Object val) {
		if (references.containsKey(key)) {
			logger.warn("Key "+key+"entry is already initialized. Please check this key is not already used"
					+ "in another part of the app");
			return;
		}
		references.put(key, val);
	}
	
	/**
	 * See update function
	 * @param key
	 */
	public void forceSet(String key, Object val) {
		if (references.containsKey(key)) {
			logger.debug("Key "+key+"entry is already initialized. Replacing value with new one");
		}
		references.put(key, val);
	}
}
