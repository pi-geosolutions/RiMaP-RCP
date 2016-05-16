package fr.pigeo.rimap.rimaprcp.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;

public class LayerDragListener implements DragSourceListener {
	private final TableViewer viewer;
	private WwjInstance wwj;

	public LayerDragListener(TableViewer viewer, WwjInstance wwjInst) {
		this.viewer = viewer;
		this.wwj=wwjInst;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		//we send the layer's position (index) in the layerlist
		IStructuredSelection selection = viewer.getStructuredSelection();
		Layer firstElement = (Layer) selection.getFirstElement();
		
		event.data = String.valueOf(this.wwj.getPositionInLayerlist(firstElement));
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}

}