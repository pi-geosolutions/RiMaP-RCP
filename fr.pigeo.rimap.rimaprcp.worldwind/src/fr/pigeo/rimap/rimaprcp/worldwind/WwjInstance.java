package fr.pigeo.rimap.rimaprcp.worldwind;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

@Creatable
@Singleton
public class WwjInstance {
	private WorldWindowGLCanvas wwd;
	private Model model;
	

	public WwjInstance () {
		System.setProperty(
	            "gov.nasa.worldwind.app.config.document",
	            "customconfig/worldwind.xml");
//        Configuration.setValue(
//        		"gov.nasa.worldwind.config.file",
//	            "fr/pigeo/rimap/rimaprcp/config/wwj/worldwind.xml");
		
		this.wwd = new WorldWindowGLCanvas();
		model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		wwd.setModel(model);
	}


	public WorldWindowGLCanvas getWwd() {
		return wwd;
	}


	public Model getModel() {
		return model;
	}
}
