 
package fr.pigeo.rimap.rimaprcp.admintools.core.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.admintools.core.constants.AdminToolsEventConstants;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;

public class FixDataHandler {
	@Execute
	public void execute(IEventBroker eventBroker, ICatalogService catalogService) {
		ICatalog catalog = catalogService.getMainCatalog();
		if (catalog==null) {
			return;
		}
		eventBroker.send(AdminToolsEventConstants.ADMINTOOLS_CATALOG_FIX, catalog);
	}
		
}