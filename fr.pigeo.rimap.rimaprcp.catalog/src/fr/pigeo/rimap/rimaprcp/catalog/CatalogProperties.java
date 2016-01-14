package fr.pigeo.rimap.rimaprcp.catalog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class CatalogProperties {
	private static InputStream inputStream;
	private static Properties configProperties;
	
	private static void loadProperties() {
		configProperties = new Properties();
		try {
			String propFileName = "resources/config.properties";
			inputStream = CatalogProperties.class.getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				configProperties.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getProperty (String key) {
		if (configProperties==null) {
			loadProperties();
		}
		return configProperties.getProperty(key);
	}
}
