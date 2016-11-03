
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfo;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;

public class FeatureInfoCommandHandler {
	private MToolItem toolItem=null;
	private FeatureInfo featureinfo=null;

	@Execute
	public void execute(final MToolItem item, IEventBroker broker, FeatureInfo fi, IEventBroker eventBroker) {
		//disable all other toolItems by telling them this item is selected
		eventBroker.send(QueryEventConstants.TOOLITEM_SELECTED, item);
		
		fi.setEnabled(item.isSelected());
		this.featureinfo = fi;
		this.toolItem=item;
	}
	
	@Inject
	@Optional
	void onToolItemChange(@UIEventTopic(QueryEventConstants.TOOLITEM_SELECTED) MToolItem item) {
		if (item==this.toolItem) {
			//do nothing, we probably issued the event
			return;
		}
		if (this.toolItem!=null) {
			this.toolItem.setSelected(false);
		}
		if (this.featureinfo!=null) {
			this.featureinfo.setEnabled(false);
		}
	}

}