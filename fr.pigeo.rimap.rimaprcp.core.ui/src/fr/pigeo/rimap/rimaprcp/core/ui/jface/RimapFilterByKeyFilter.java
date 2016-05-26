package fr.pigeo.rimap.rimaprcp.core.ui.jface;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import gov.nasa.worldwind.layers.Layer;

public class RimapFilterByKeyFilter extends ViewerFilter {
	private String key;
	
	public RimapFilterByKeyFilter(String key) {
		this.key=key;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (this.key==null)
			return true;
		Layer l = (Layer) element;
		if (Boolean.parseBoolean(l.getStringValue(key)))
			return true;
		return false;
	}

}
