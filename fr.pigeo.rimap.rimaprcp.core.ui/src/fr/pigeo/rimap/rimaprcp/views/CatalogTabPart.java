
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class CatalogTabPart {
	@Inject
	private IEventBroker eventBroker;
	@Inject
	Logger logger;

	private TreeViewer viewer;

	@PostConstruct
	public void postConstruct(// @Preference IEclipsePreferences prefs,
			Composite parent, final IEclipseContext ctx, final WwjInstance wwj, IPreferencesService prefService,
			ICatalogService catalogService, ISessionService sessionService) {

		// System.out.println("Preference project url: "+project_url);

		// catalog = new PadreCatalogComposite(parent, SWT.BORDER_SOLID,
		// layertree_service_url, web_usage_level, wwj, eventBroker);
		// catalog.setLayout(new FillLayout());
		ICatalog mainCatalog = catalogService.getMainCatalog();
		
		//TODO: Build the Part using the Catalog list (only mainCatalog as first)
	}
}