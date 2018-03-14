package fr.pigeo.rimap.rimaprcp.worldwind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *         Intermediary class between Worldwind internal structure and the RCP
 *         application
 */
@Creatable
@Singleton
public class WwjInstance {

	private final String WIDGET_PREF_PREFIX = "show_";

	private WorldWindowGLCanvas wwd;
	private Model model;
	private List<Widget> widgetList = new ArrayList<Widget>();

	@Inject
	public WwjInstance(IPreferencesService prefs) {
		System.setProperty("gov.nasa.worldwind.app.config.document", "customconfig/worldwind.xml");
		// Configuration.setValue(
		// "gov.nasa.worldwind.config.file",
		// "fr/pigeo/rimap/rimaprcp/config/wwj/worldwind.xml");
		double lat = prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_LATITUDE", 0, null);
		double lon = prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_LONGITUDE", 0, null);
		double alt = prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_ALTITUDE", 19.07e6, null);

		Configuration.setValue(AVKey.INITIAL_LATITUDE,
				prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_LATITUDE", 0, null));
		Configuration.setValue(AVKey.INITIAL_LONGITUDE,
				prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_LONGITUDE", 0, null));
		Configuration.setValue(AVKey.INITIAL_ALTITUDE,
				prefs.getDouble("fr.pigeo.rimap.rimaprcp.worldwind", "INITIAL_ALTITUDE", 19.07e6, null));

		this.wwd = new WorldWindowGLCanvas();
		model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		System.out.println(WorldWind.getValue(AVKey.INITIAL_LATITUDE));
		Iterator it = model.getValues()
				.iterator();
		while (it.hasNext()) {
			System.out.println(it.next()
					.toString());
		}
		wwd.setModel(model);

		// Setup a select listener for the worldmap click-and-go feature
		this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));
		this.addViewControls();

		this.getCompassLayer(this.wwd)
				.setIconFilePath("customconfig/img/Rose_des_vents.png");
		this.getCompassLayer(this.wwd)
				.setIconScale(1);
		/*
		 * double minlat =
		 * prefService.getDouble("fr.pigeo.rimap.rimaprcp.worldwind",
		 * "START_BBOX_MINLAT", -90, null);
		 * double minlon =
		 * prefService.getDouble("fr.pigeo.rimap.rimaprcp.worldwind",
		 * "START_BBOX_MINLON", -180, null);
		 * double maxlat =
		 * prefService.getDouble("fr.pigeo.rimap.rimaprcp.worldwind",
		 * "START_BBOX_MAXLAT", 90, null);
		 * double maxlon =
		 * prefService.getDouble("fr.pigeo.rimap.rimaprcp.worldwind",
		 * "START_BBOX_MAXLON", 180, null);
		 */

	}

	public WorldWindowGLCanvas getWwd() {
		return wwd;
	}

	public Model getModel() {
		return model;
	}

	public void addViewControls() {
		// Create and install the view controls layer and register a controller
		// for it with the World Window.
		ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
		// viewControlsLayer.setName(viewControlsLayer.getName()+"Widget");
		viewControlsLayer.setName("ViewControlsWidget");
		ApplicationTemplate.insertBeforeCompass(getWwd(), viewControlsLayer);
		this.getWwd()
				.addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));
	}

	/**
	 * @param dropTarget
	 * @return the position (index) of a Layer in the WWJ LayersList
	 */
	public int getPositionInLayerlist(Object dropTarget) {
		LayerList list = this.getModel()
				.getLayers();
		return list.indexOf(dropTarget);
	}

	/*
	 * Use this method to get all inputs for a JFace widget
	 * To get only layers, just change the method called internally to getLayersOnlyListAsArray
	 */
	public WWObject[] getLayersListAsArray() {
		return getLayersAndDemListAsArray();
	}
	
	public Layer[] getLayersOnlyListAsArray() {
		return this.getModel()
				.getLayers()
				.toArray(new Layer[0]);
	}

	public ElevationModel[] getElevationModelsListAsArray() {
		return getElevationModelsList().toArray(new ElevationModel[0]);
	}
	
	public List<ElevationModel> getElevationModelsList() {
		CompoundElevationModel model = (CompoundElevationModel) this.getModel().getGlobe().getElevationModel();
		return model.getElevationModels();
	}
	
	/*
	 * Produces an array concatenating the layers and the DEMs
	 */
	public WWObject[] getLayersAndDemListAsArray() {
		WWObject[] ll = getLayersOnlyListAsArray();
		ArrayList<WWObject> layerslist = new ArrayList<>(Arrays.asList(ll));
		List<ElevationModel> eml = getElevationModelsList();
		Collections.reverse(eml);
		layerslist.addAll(eml);
		return layerslist.toArray(new WWObject[0]);
	}

	public void showWidget(boolean show, String widgetRef) {
		LayerList ll = this.getModel()
				.getLayers();
		try {
			List<Layer> layers = ll.getLayersByClass(Class.forName(widgetRef));
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
		this.initializeWidgets(prefs);
	}

	/**
	 * @param prefs
	 *            Finds all the layers whose name ends with "Widget" and reads
	 *            the prefs for everyone of them
	 *            TODO : generate the menu entries from there
	 */
	private void initializeWidgets(IEclipsePreferences prefs) {
		LayerList ll = this.getModel()
				.getLayers();
		Iterator<Layer> layersIterator = ll.iterator();
		while (layersIterator.hasNext()) {
			Layer l = layersIterator.next();
			if (Widget.isWidget(l)) {
				Widget w = new Widget(l);
				w.initialize(prefs.getBoolean(WIDGET_PREF_PREFIX + w.getWidgetClassName(), true));
				widgetList.add(w);
				// this.initializeWidget(prefs, l);
			}
		}
	}

	private CompassLayer getCompassLayer(WorldWindow wwd) {
		LayerList layers = wwd.getModel()
				.getLayers();
		for (Layer l : layers) {
			if (l instanceof CompassLayer)
				return (CompassLayer) l;
		}
		return null;
	}

	/*
	 * private void initializeWidget(IEclipsePreferences prefs, Layer l ) {
	 * boolean show = prefs.getBoolean(WIDGET_PREF_PREFIX +
	 * l.getClass().getName(), true);
	 * l.setEnabled(show);
	 * 
	 * //create the menu entry
	 * //IMenuManager displayMenu =
	 * }
	 */

	public List<Widget> getWidgetsClassLists() {
		return this.widgetList;
	}

	@Inject
	@Optional
	void checkHandler(@UIEventTopic(RiMaPEventConstants.CHECKABLENODE_CHECKCHANGE) ICheckableNode node) {
		if (node == null) {
			return;
		}
		WWObject layer = node.getLayer();
		if (layer != null) {
			updateLayer(layer, true);
		}
	}

	/**
	 * @param oldPos
	 * @param destPos
	 *            Moves a Layer from oldPos to destPos
	 */
	public void moveLayer(int oldPos, int destPos) {
		if (destPos < 0)
			return;
		LayerList list = this.getModel()
				.getLayers();
		Layer dndedLayer = list.get(oldPos);
		list.remove(dndedLayer);
		list.add(destPos, dndedLayer);
		this.getWwd()
				.redraw();
	}

	public void addLayer(WWObject layer) {
			updateLayer(layer, true);
	}

	private void updateLayer(WWObject obj, boolean removeIfDisabled) {
		if (obj instanceof Layer) {
			Layer layer = (Layer) obj;
			LayerList layers = wwd.getModel()
					.getLayers();
			if (layer.isEnabled()) {
				if (!layers.contains(layer)) {
					ApplicationTemplate.insertBeforePlacenames(wwd, layer);
				}
			} else if (removeIfDisabled) {
				layers.remove(layer);
			}
			this.getWwd()
					.redraw();
		} else if (obj instanceof ElevationModel) {
			ElevationModel em = (ElevationModel) obj;
			CompoundElevationModel model = (CompoundElevationModel) this.getModel().getGlobe().getElevationModel();
			model.addElevationModel(em);
		}
	}

	public void removeLayer(Layer layer) {
		LayerList layers = wwd.getModel()
				.getLayers();
		layers.remove(layer);
	}
}
