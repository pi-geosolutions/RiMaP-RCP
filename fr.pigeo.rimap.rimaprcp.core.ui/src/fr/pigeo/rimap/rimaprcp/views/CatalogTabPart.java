
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.core.catalog.CatalogViewContentProvider;
import fr.pigeo.rimap.rimaprcp.core.catalog.CatalogViewLabelProvider;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
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

		ICatalog mainCatalog = catalogService.getMainCatalog();

		// TODO: Build the Part using the Catalog list (only mainCatalog as
		// first)

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new CatalogViewContentProvider());
		viewer.setLabelProvider(new CatalogViewLabelProvider());
		viewer.setInput(mainCatalog.getRootNode());
	}
}