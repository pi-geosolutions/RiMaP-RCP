package fr.pigeo.rimap.rimaprcp.worldwind;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

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
	
	public void moveLayer(int oldPos, int destPos) {
		LayerList list = this.getModel().getLayers();
		Layer dndedLayer=list.get(oldPos);
		list.remove(dndedLayer);
		if (destPos <0) {
			//something wrong happened. We will restore the previous state and issue an error.
			list.add(oldPos,dndedLayer);
			System.out.println("Error while dropping the layer \n Restored previous state.");
			
		} else {
			list.add(destPos, dndedLayer);
		}
		this.getWwd().redraw();
		/*Layer dndedLayer=null;
		if (wwj !=null && this.dropTarget!=null) {
			System.out.println("drop performed");
			LayerList list = wwj.getModel().getLayers();
			dndedLayer=list.get(dragPos);
			list.remove(dndedLayer);
			int destIndex = this.computeDropPos(list, this.dropTarget);
			if (destIndex <0) {
				//something wrong happened. We will restore the previous state and issue an error.
				list.add(dragPos,dndedLayer);
				System.out.println("Error while dropping the layer \n Restored previous state.");
				
			} else {
				list.add(destIndex, dndedLayer);
			}
		}*/
		/*
		 * ContentProviderTree.INSTANCE.getModel().add(data.toString());
		 * viewer.setInput(ContentProviderTree.INSTANCE.getModel());
		 */
		/*try {
			System.out.println("dropped " + ((Layer) data).getName());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}


	public int getPositionInLayerlist(Object dropTarget) {
		LayerList list = this.getModel().getLayers();
		return list.indexOf(dropTarget);
	}


	public Layer[] getLayersList() {
		return this.getModel().getLayers().toArray(new Layer[0]);
	}
}
