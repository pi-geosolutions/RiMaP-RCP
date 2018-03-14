package fr.pigeo.rimap.rimaprcp.core.ui.jface;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.Layer;

public class TableCheckEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public TableCheckEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new CheckboxCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		boolean enabled = false;
		if (element instanceof Layer) {
			enabled = ((Layer) element).isEnabled();
		}
		if (element instanceof ElevationModel) {
			enabled = ((ElevationModel) element).isEnabled();
		}
		return enabled;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if (element instanceof Layer) {
			((Layer) element).setEnabled((boolean) value);
		}
		if (element instanceof ElevationModel) {
			((ElevationModel) element).setEnabled((boolean) value);
		}
		viewer.update(element, null);
		// System.out.println("is rimap layer : "+((Layer)
		// element).getStringValue("isRimapLayer"));
	}

}
