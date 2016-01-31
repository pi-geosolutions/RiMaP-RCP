package fr.pigeo.rimap.rimaprcp.worldwind;

import gov.nasa.worldwind.layers.Layer;

public class Widget {
	private final static String WIDGET_SUFFIX = "Widget";
	
	
	private Layer layer;

	public Widget(Layer layer) {
		super();
		this.layer = layer;
	}

	public Layer getLayer() {
		return layer;
	}

	public String getWidgetClassName() {
		return layer.getClass().getName();
	}

	public String getLabel() {
		String label = layer.getName().substring(0, layer.getName().length()-WIDGET_SUFFIX.length());
		return label;
	}

	public boolean isVisible() {
		return this.layer.isEnabled();
	}

	public void initialize(boolean show) {
		this.layer.setEnabled(show);
	}

	public static boolean isWidget(Layer l)  {
		if (l.getName().endsWith(WIDGET_SUFFIX))
			return true;
		else
			return false;
	}
}
