package fr.pigeo.rimap.rimaprcp.core.ui.animations;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.core.ui.core.Plugin;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.worldwind.layers.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.util.SectorSelector;
import fr.pigeo.rimap.rimaprcp.worldwind.util.ViewUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;

public class AnimationsExtent {
	private WwjInstance wwj;
	private IEventBroker eventBroker;
	private RenderableLayer renderable = new RenderableLayer();
	private SectorSelector selector;
	private Sector currentSector = null;

	public AnimationsExtent(WwjInstance wwj, IEventBroker eventBroker) {
		this.wwj = wwj;
		this.eventBroker = eventBroker;
	}

	public void setFullExtent(RimapWMSTiledImageLayer layer) throws InvalidExtentException {
		Sector sector = (Sector) layer.getValue(AVKey.SECTOR);
		if (sector==null) {
			throw new InvalidExtentException(Plugin.translate("animations.extent.exception.unknown"));
		}
		currentSector = new Sector(sector); // clone sector, so that it won't be
											// edited
		drawSector(sector);
		
	}

	/**
	 * Computes, if possible, the viewable extent based on the current view
	 * epsilon is used to get slightly inner boundaries, so that the user sees
	 * the box
	 */
	public void setViewExtent() throws InvalidExtentException {
		View view = this.wwj.getWwd()
				.getView();
		if (view != null) {
			int epsilon = 10;
			Sector sector = ViewUtils.getViewExtentAsSector(view, epsilon);
			if (sector !=null && sector.isWithinLatLonLimits()) {
				currentSector = new Sector(sector); // clone sector, so that it
													// won't be edited
				drawSector(sector);
			} else {
				throw new InvalidExtentException(Plugin.translate("animations.extent.exception.offglobesector"));
			}
		}
	}

	public Sector getSector() {
		return this.currentSector;
	}

	/**
	 * Draw the current sector if it exists
	 */
	public void drawSector() {
		if (currentSector != null) {
			drawSector(this.currentSector);
		}
	}

	/**
	 * Draw the sector as a sectorselector instance (editable sector)
	 * 
	 * @param sector
	 */
	private void drawSector(Sector sector) {
		renderable.removeAllRenderables();
		this.selector = new SectorSelector(wwj.getWwd(), sector, renderable);
		this.selector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
		this.selector.setBorderColor(new Color(0f, 0f, 1f, 0.5f));
		this.selector.setBorderWidth(5);
		this.selector.addPropertyChangeListener(SectorSelector.SECTOR_PROPERTY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null) {
					// means mouse released, means finished drawing
					eventBroker.post(RimapEventConstants.ANIMATIONS_SECTORSELECTOR_SECTOR_CHANGED,
							selector.getSector());
					currentSector = selector.getSector();
				} else {
					// means still drawing (mouse not released)
					// evtBroker.post(CacheManagerEventConstants.SECTORSELECTOR_DRAWING,
					// selector.getSector());
				}
			}
		});
		// selector.enable();
		this.selector.setEditable();
		wwj.addLayer(renderable);
		wwj.getWwd()
				.redraw();
	}

	public void removeRenderables() {
		wwj.removeLayer(renderable);

	}

	/**
	 * Formats the extent to be injected in a WMS 1.3.0 request (BBOX parameter)
	 * 
	 * @return
	 */
	public String getExtentAsWmsBBOXString() {
		return this.getExtentAsWmsBBOXString("1.3.0");
	}

	/**
	 * Formats the extent to be injected in a WMS request (BBOX parameter)
	 * Handled differently depending on the WMS version
	 * (see http://docs.geoserver.org/stable/en/user/services/wms/basics.html)
	 * 
	 * @return
	 */
	public String getExtentAsWmsBBOXString(String wmsversion) {
		// if (this.selector == null || this.selector.getSector() == null ||
		// !this.selector.getSector()
		// .isWithinLatLonLimits()) {
		// return "";
		// }
		// Sector sector = this.selector.getSector();
		int degreesApprox = 2;
		String bbox = "";
		if (wmsversion.equals("1.1.1")) {
			bbox += formatAngle(currentSector.getMinLongitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMinLatitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMaxLongitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMaxLatitude(), degreesApprox);
		} else { // WMS 1.3.0
			bbox += formatAngle(currentSector.getMinLatitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMinLongitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMaxLatitude(), degreesApprox) + ",";
			bbox += formatAngle(currentSector.getMaxLongitude(), degreesApprox);
		}
		return bbox;
	}

	private String formatAngle(Angle angle, int digits) {
		return String.format(Locale.US, "%." + digits + "f", angle.degrees);
	}
	
	public class InvalidExtentException extends Exception {
		public InvalidExtentException() {
			super();
		}
		public InvalidExtentException(String message) {
			super(message);
		}
	}

}
