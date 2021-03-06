
package fr.pigeo.rimap.rimaprcp.core.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.ui.core.Central;
import fr.pigeo.rimap.rimaprcp.core.ui.jface.LayersListTableComposite;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class OrganizeTabPart {
	@Inject
	IEventBroker broker;

	private LayersListTableComposite layersListComposite;

	@Inject
	public OrganizeTabPart() {
	}

	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst, Central central, IEclipseContext context) {
		this.layersListComposite = new LayersListTableComposite(parent, SWT.BORDER_SOLID, wwjInst);
		ContextInjectionFactory.inject(this.layersListComposite, context);
		
		this.layersListComposite.addDragnDropSupport();
		this.layersListComposite.addWidgetFilter(false);
		this.layersListComposite.registerEvents();
		// this.layersListComposite.drawTableLines();
		central.append("fr.pigeo.rimap.rimaprcp.jface.LayersListTableComposite", layersListComposite);
	}

	@Inject
	@Optional
	private void subscribeEventLayerChecked(@UIEventTopic(RiMaPEventConstants.CHECKABLENODE_CHECKCHANGE) ICheckableNode node) {
		this.layersListComposite.refresh();

	}

	@PreDestroy
	private void dispose() {
		this.layersListComposite.dispose();
	}

	public IStructuredSelection getSelectedLayers() {
		return layersListComposite.getSelectedLayers();
	}

}