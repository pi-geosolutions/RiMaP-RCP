package fr.pigeo.rimap.rimaprcp.worldwind;

import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Creatable;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *	Intermediary class between Worldwind internal structure and the RCP application
 */
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
	
	/**
	 * @param oldPos
	 * @param destPos
	 * Moves a Layer from oldPos to destPos
	 */
	public void moveLayer(int oldPos, int destPos) {
		if (destPos<0)
			return;
		LayerList list = this.getModel().getLayers();
		Layer dndedLayer=list.get(oldPos);
		list.remove(dndedLayer);
		list.add(destPos, dndedLayer);
		this.getWwd().redraw();
	}

	/**
	 * @param dropTarget
	 * @return the position (index) of a Layer in the WWJ LayersList
	 */
	public int getPositionInLayerlist(Object dropTarget) {
		LayerList list = this.getModel().getLayers();
		return list.indexOf(dropTarget);
	}


	public Layer[] getLayersList() {
		return this.getModel().getLayers().toArray(new Layer[0]);
	}


	public void showCompass(boolean show) {
		LayerList ll = this.getModel().getLayers();
		try {
			List<Layer> layers = ll.getLayersByClass(Class.forName("gov.nasa.worldwind.layers.CompassLayer"));
			Iterator<Layer> layersIterator = layers.iterator();
			if (layersIterator.hasNext()) {
			    Layer l = layersIterator.next();
			    l.setEnabled(show);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	public void initialize(IEclipsePreferences prefs) {
		System.out.println("TODO : code initialize WWJ/prefs setup");
		boolean showcompass = prefs.getBoolean("showcompass", true);
		this.showCompass(showcompass);
	}
}
