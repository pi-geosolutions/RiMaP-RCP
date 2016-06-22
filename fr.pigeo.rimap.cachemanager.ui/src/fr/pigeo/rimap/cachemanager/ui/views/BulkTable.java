package fr.pigeo.rimap.cachemanager.ui.views;



import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.BulkLayersListTableComposite;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;


public class BulkTable {
	//@SuppressWarnings("restriction")
	@Inject
	IEventBroker broker;

	private BulkLayersListTableComposite layersListComposite;

	@Inject
	public BulkTable() {
	}

	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst){ //,  WorldWindow wwd) {
		this.layersListComposite = new BulkLayersListTableComposite(parent, SWT.BORDER_SOLID, wwjInst); //, wwd);
		this.layersListComposite.addDragnDropSupport();
		this.layersListComposite.addWidgetFilter(false);
		this.layersListComposite.setEventBroker(broker);
		// this.layersListComposite.drawTableLines();
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


/*
package fr.pigeo.rimap.cachemanager.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.BulkLayersListTableComposite;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.WorldWindow;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;


public class BulkTable {

	@Inject
	IEventBroker broker;

	private BulkLayersListTableComposite layersListComposite;

	@Inject
	public BulkTable() {
	}

	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst){ //,  WorldWindow wwd) {
		this.layersListComposite = new BulkLayersListTableComposite(parent, SWT.BORDER_SOLID, wwjInst); //, wwd);
		this.layersListComposite.addDragnDropSupport();
		this.layersListComposite.addWidgetFilter(false);
		this.layersListComposite.setEventBroker(broker);
		// this.layersListComposite.drawTableLines();
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
*/