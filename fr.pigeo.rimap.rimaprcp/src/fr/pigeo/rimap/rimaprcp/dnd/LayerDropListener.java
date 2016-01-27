package fr.pigeo.rimap.rimaprcp.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

public class LayerDropListener extends ViewerDropAdapter {

	private WwjInstance wwj;
	private final Viewer viewer;
	private Object dropTarget;
	private int location=0;

	public LayerDropListener(Viewer viewer, WwjInstance wwjInst) {
		super(viewer);
		this.viewer = viewer;
		this.wwj=wwjInst;
	}

	@Override
	public void drop(DropTargetEvent event) {
		this.location = this.determineLocation(event);
		this.dropTarget = determineTarget(event);
		String translatedLocation = "";
		switch (location) {
		case 1:
			translatedLocation = "Dropped before the target ";
			break;
		case 2:
			translatedLocation = "Dropped after the target ";
			break;
		case 3:
			translatedLocation = "Dropped on the target ";
			break;
		case 4:
			translatedLocation = "Dropped into nothing ";
			break;
		}
		System.out.println(translatedLocation);
		System.out.println("The drop was done on the element: " + ((Layer) dropTarget).getName());
		super.drop(event);
	}

	// This method performs the actual drop
	// We simply add the String we receive to the model and trigger a refresh of
	// the
	// viewer by calling its setInput method.
	@Override
	public boolean performDrop(Object data) {
		int dragPos = Integer.parseInt((String) data);
		if (this.wwj!=null && this.dropTarget!=null) {
			int destPos = this.computeDropPos(this.wwj.getPositionInLayerlist(this.dropTarget));

			System.out.println("dragPos : "+dragPos);
			System.out.println("destPos : "+destPos);
			this.wwj.moveLayer(dragPos, destPos);
			System.out.println("sent drop event");
			//eventBroker.post("dropped_layer", dragPos );
			this.getViewer().setInput(this.wwj.getLayersList());
			this.getViewer().refresh();
		}
		System.out.println("drop performed");
/*		Layer dndedLayer=null;
		if (wwj !=null && this.dropTarget!=null) {
			System.out.println("drop performed");
			LayerList list = wwj.getModel().getLayers();
			dndedLayer=list.get(dragPos);
			list.remove(dndedLayer);
			int destIndex = this.computeDropPos(list, this.dropTarget);
			if (destIndex <0) {
				//something wrong happened. We will restore the previous state and issue an error.
				list.add(dragPos,dndedLayer);
				System.out.println("Error while dropping the layer \n Restored previous state.");
				
			} else {
				list.add(destIndex, dndedLayer);
			}
		}*/
		/*
		 * ContentProviderTree.INSTANCE.getModel().add(data.toString());
		 * viewer.setInput(ContentProviderTree.INSTANCE.getModel());
		 */
		/*try {
			System.out.println("dropped " + ((Layer) data).getName());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;

	}
	/*
	 * Will compute the new index for the dropped layer, using the layerlist and the 
	 * Layer define as target for the drop. 
	 * If nothing suitable comes out, a negative index is output
	 */
	private int computeDropPos(int targetPos) {
		switch (location) {
		case 1:
			return targetPos-1;
		case 2:
			return targetPos+1;
		case 3:
			return targetPos;
		default:
			return -1;
		}
	}

}