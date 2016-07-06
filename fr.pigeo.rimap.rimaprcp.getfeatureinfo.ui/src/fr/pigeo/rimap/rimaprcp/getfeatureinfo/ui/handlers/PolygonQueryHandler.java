
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.handlers;

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.viewers.IStructuredSelection;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.PolygonQuery;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.PolygonQueryUIManager;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.render.SurfacePolygon;

public class PolygonQueryHandler {
	private IStructuredSelection selectedLayers = null;
	private MToolItem toolItem=null;

	@Execute
	public void execute(final MToolItem item, IEventBroker broker, PolygonQueryUIManager pquim) {
		pquim.enable(item.isSelected());
	}

	@CanExecute
	public boolean canExecute(final MToolItem item) {
		this.toolItem = item;
		if (selectedLayers != null) {
			Iterator it = selectedLayers.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof WWObjectImpl) {
					WWObjectImpl wwobj = (WWObjectImpl) obj;
					if (AVListImpl.getBooleanValue(wwobj, RimapAVKey.LAYER_ISPOLYGONQUERYABLE, false)) {
						return true;
					}
				}
			}

		}
		reset();
		return false;
	}

	@Inject
	@Optional
	void onLayerSelectionChange(
			@UIEventTopic(RiMaPEventConstants.LAYER_SELECTED_LAYERS) IStructuredSelection selectedLayers,
			IEventBroker eventBroker) {
		this.selectedLayers = selectedLayers;
		eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
	}

	@Inject
	@Optional
	void onPolygonClose(@UIEventTopic(QueryEventConstants.POLYGONQUERY_POLYGON_CLOSED) SurfacePolygon p) {
		reset();
	}

	private void reset() {
		this.toolItem.setSelected(false);
	}

}