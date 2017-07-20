package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import gov.nasa.worldwind.layers.BasicTiledImageLayer;

public class ResolutionEditingSupport extends EditingSupport {

	private final TableViewer viewer;

	public ResolutionEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		double[] resolutions = ((Downloadable) element).getResolutions();
		if (resolutions==null) {
			System.out.println("###################### oups, resolutions is null##############");
			return null;
		}
		String[] levels = new String[resolutions.length];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = String.format("%.2f m", resolutions[i]);
		}
		return new ComboBoxCellEditor(viewer.getTable(), levels, SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		double res =  ((Downloadable) element).getMaxResolution();
		double[] resolutions = ((Downloadable) element).getResolutions();
		int index = resolutions.length-1;
		while (index >= 0) {
			if (res <= resolutions[index]) {
				return index;
			}
			index--;
		}
		//else
		return resolutions.length;
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		((Downloadable) element).setMaxLevel((int) userInputValue);
		((Downloadable) element).updateSize();
		viewer.update(element, null);
	}
}