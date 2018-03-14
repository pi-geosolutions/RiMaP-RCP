package fr.pigeo.rimap.rimaprcp.core.ui.jface;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import gov.nasa.worldwind.layers.Layer;

public class WidgetsFilter extends ViewerFilter {
	private boolean keep = true;

	/**
	 * @param keep
	 *            : if true, the filter will select the widgets. If false,
	 *            it will purge the widgets and select the remaining entries
	 */
	public WidgetsFilter(boolean keep) {
		this.keep = keep;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Layer) {
			Layer l = (Layer) element;
			if (l.getName()
					.endsWith("Widget"))
				return this.keep;
			else
				return !this.keep;
		}
		//fallback
		return true;
	}

}
