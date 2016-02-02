
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.jface.LayersListTableComposite;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class OrganizeTabPart {
	@Inject 
	IEventBroker broker;
	
	private LayersListTableComposite layersListComposite;

	@Inject
	public OrganizeTabPart() {
	}

	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst) {
		this.layersListComposite = new LayersListTableComposite(parent, SWT.BORDER_SOLID, wwjInst);
		this.layersListComposite.addDragnDropSupport();
		this.layersListComposite.addWidgetFilter(false);
		this.layersListComposite.setEventBroker(broker);
		//this.layersListComposite.drawTableLines();
	}

	@Inject
	@Optional
	private void subscribeEventLayerChecked(@UIEventTopic(RimaprcpConstants.LAYER_CHECKED) AbstractLayer layer) {
		this.layersListComposite.refresh();

	}

	@PreDestroy
	private void dispose() {
		this.layersListComposite.dispose();
	}

}