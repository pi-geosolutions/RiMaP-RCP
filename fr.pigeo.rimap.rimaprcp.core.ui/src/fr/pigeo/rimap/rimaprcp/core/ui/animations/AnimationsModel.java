package fr.pigeo.rimap.rimaprcp.core.ui.animations;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import fr.pigeo.rimap.rimaprcp.core.services.catalog.worldwind.layers.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class AnimationsModel {
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public static final String FIELD_NAME = "name";
	public static final String FIELD_EXTENTTYPE = "extentType";
	public static final String FIELD_RESOLUTION = "resolution";
	public static final String FIELD_RESOLUTIONS = "resolutionsList";

	private Messages i18n;
	private String name, extentType, resolution;
	private IObservableList resolutionsList;
	private String[] timestamps;

	public AnimationsModel(Messages messages) {
		this.i18n = messages;
		initVars();
	}

	private void initVars() {
		name = i18n.animations_dialog_layername_isnull;
		extentType = i18n.ANIM_EXTENT_UNDEFINED;
		resolution = i18n.ANIM_RESOL_MEDIUM;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		changes.firePropertyChange(FIELD_NAME, this.name, this.name = name);
	}

	public String getExtentType() {
		return extentType;
	}

	public void setExtentType(String extent) {
		changes.firePropertyChange(FIELD_EXTENTTYPE, this.extentType, this.extentType = extent);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	public WritableList getResolutionsList() {
		WritableList resolutions = new WritableList();
		resolutions.add(i18n.ANIM_RESOL_HIGH);
		resolutions.add(i18n.ANIM_RESOL_MEDIUM);
		resolutions.add(i18n.ANIM_RESOL_LOW);
		return resolutions;
	}

	public String getResolution() {
		return resolution;
	}

	public double getResolutionAsMultiplicationFactor() {
		if (resolution.equals(i18n.ANIM_RESOL_HIGH)) {
			return 2;
		} else if (resolution.equals(i18n.ANIM_RESOL_LOW)) {
			return 0.5;
		}
		return 1;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public void setTimestamps(String[] timestamps) {
		this.timestamps = timestamps;
	}
	public String [] getTimestamps() {
		return this.timestamps;
	}

}
