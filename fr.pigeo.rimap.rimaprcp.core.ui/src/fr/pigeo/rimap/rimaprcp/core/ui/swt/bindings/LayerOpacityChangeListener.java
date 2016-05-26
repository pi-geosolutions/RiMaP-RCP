package fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings;

import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;

public class LayerOpacityChangeListener implements IValueChangeListener {
	private Layer layer;

	public LayerOpacityChangeListener(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void handleValueChange(ValueChangeEvent event) {
		//System.out.println("opacity changed");
		if (layer!=null) {
			layer.firePropertyChange(AVKey.OPACITY, null, event.getObservableValue().getValue());
		}

	}

}
