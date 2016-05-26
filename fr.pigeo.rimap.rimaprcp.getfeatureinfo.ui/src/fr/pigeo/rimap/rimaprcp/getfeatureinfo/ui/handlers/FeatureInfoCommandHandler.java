
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfo;

public class FeatureInfoCommandHandler {

	@Execute
	public void execute(final MToolItem item, IEventBroker broker, FeatureInfo fi) {
		broker.post("GETFEATUREINFO/UI/BUTTON_STATUS", item.isSelected()); 
		fi.setEnabled(item.isSelected());
	}

}