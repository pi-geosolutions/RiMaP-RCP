package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import gov.nasa.worldwind.layers.Layer;

public class LayerDownloadCheckEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public LayerDownloadCheckEditingSupport(TableViewer viewer) {
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
		return ((Downloadable) element).doDownload();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((Downloadable) element).setDownload((boolean) value);
		viewer.update(element, null);
	}

}
