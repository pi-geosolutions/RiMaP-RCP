package fr.pigeo.rimap.rimaprcp.core.ui.tools;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.WaterLevelLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.util.SectorSelector;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;

@Creatable
@Singleton
public class WaterLevel {
	@Inject
	IEventBroker eventBroker;
	@Inject
	WwjInstance wwj;

	SectorSelector selector = null;
	WaterLevelLayer waterLayer = null;

	@Inject
	@Optional
	void drawSector(@UIEventTopic(RiMaPEventConstants.WATER_SECTORSELECTOR_DRAW) Object obj) {
		if (this.selector == null) {
			this.selector = new SectorSelector(wwj.getWwd(), this.getLayer());
			this.selector.setInteriorColor(new Color(0, 201, 232, 120));
			this.selector.setBorderColor(new Color(5, 56, 232, 150));
			this.selector.setBorderWidth(1);
			this.selector.addPropertyChangeListener(SectorSelector.SECTOR_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue() == null) {
						eventBroker.send(RiMaPEventConstants.WATER_SECTORSELECTOR_DRAWN, selector.getSector());
						selector.freeze(true);
					} else {
						// means still drawing (mouse not released)
						// evtBroker.post(CacheManagerEventConstants.SECTORSELECTOR_DRAWING,
						// selector.getSector());
					}
				}
			});

		} else {
			this.selector.clearList();
		}
		selector.enable();
	}

	@Inject
	@Optional
	void floodEvent(@UIEventTopic(RiMaPEventConstants.WATER_DIALOG_FLOOD) Object obj) {
		Sector sector = this.selector.getSector();
		if (sector==null) {
			return ;
		}
		this.selector.freeze(true);
		this.waterLayer.createWaterPolygon(sector);
		this.waterLayer.setOpacity(0.5);
		wwj.addLayer(this.waterLayer);
	}
	
	@Inject
	@Optional
	void waterHeightChanged(@UIEventTopic(RiMaPEventConstants.WATER_HEIGHT_CHANGED) double height) {
		this.waterLayer.setElevation(height);
		wwj.getWwd().redraw();
	}

	
	/*
	 * Cleanup trailing objects
	 */
	@Inject
	@Optional
	void onDialogClose(@UIEventTopic(RiMaPEventConstants.WATER_DIALOG_CLOSED) Object obj) {
		if (this.waterLayer != null) {
			wwj.removeLayer(this.waterLayer);
			this.waterLayer.dispose();
			this.selector=null;
		}
	}

	protected RenderableLayer getLayer() {
		if (this.waterLayer == null) {
			this.waterLayer = new WaterLevelLayer();
			this.waterLayer.setName("Water flooding tool");
			wwj.addLayer(this.waterLayer);
		}
		return this.waterLayer;
	}

}
