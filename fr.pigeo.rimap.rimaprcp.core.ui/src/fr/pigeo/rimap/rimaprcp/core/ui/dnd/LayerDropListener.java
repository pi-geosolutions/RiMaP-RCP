package fr.pigeo.rimap.rimaprcp.core.ui.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class LayerDropListener extends ViewerDropAdapter {

	private WwjInstance wwj;

	public LayerDropListener(Viewer viewer, WwjInstance wwjInst) {
		super(viewer);
		this.wwj = wwjInst;
	}

	@Override
	public void drop(DropTargetEvent event) {
		/*this.location = this.determineLocation(event);
		this.dropTarget = determineTarget(event);*/
		super.drop(event);
	}

	/*
	 * This method performs the actual drop We get the String we receive
	 * (dragPos) and compute the new position. Then it calls the wwj instance
	 * for the actual move And refreshes the viewer
	 */
	@Override
	public boolean performDrop(Object data) {
		int dragPos = Integer.parseInt((String) data);		
		Object dropTarget = this.getCurrentTarget();
		if (this.wwj != null && dropTarget != null) {
			int destPos = this.computeDropPos(dragPos, this.wwj.getPositionInLayerlist(dropTarget));
			if (destPos < 0)
				return false;

			this.wwj.moveLayer(dragPos, destPos);
			this.getViewer().setInput(this.wwj.getLayersList());
			this.getViewer().refresh();
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;

	}

	/*
	 * Will compute the new index for the dropped layer, using the layerlist and
	 * the Layer define as target for the drop. If nothing suitable comes out, a
	 * negative index is output
	 */
	private int computeDropPos(int dragPos, int targetPos) {
		int location = this.getCurrentLocation();
		if (targetPos > dragPos) {
			targetPos--;
		}
		switch (location) {
		case 1:
			return targetPos;
		case 2:
			return targetPos + 1;
		case 3:
			return targetPos;
		default:
			return -1;
		}
	}

}