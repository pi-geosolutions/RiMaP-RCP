 
package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.util.Collections;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;
import fr.pigeo.rimap.rimaprcp.core.ui.views.CatalogTabPart;

public class ReloadTree {
	@Execute
	public void execute(ICatalogService catalogService, EModelService modelService, EPartService partService,MApplication app, Logger logger) {
		if (catalogService==null) {
			return;
		}
		List<MPart> parts = modelService.findElements(app, "fr.pigeo.rimap.rimaprcp.part.jfacelayertree", MPart.class, Collections.emptyList());
		if (parts.isEmpty()) {
			logger.error("Error searching 'Select' tab. Found "+parts.size()+" parts (1 expected)");
			return;
		}
		MPart mpart = parts.get(0);
		CatalogTabPart part = (CatalogTabPart) mpart.getObject();
		System.out.println("found "+parts.size()+" parts");
		ICatalog mainCatalog = catalogService.getMainCatalog();
		logger.info("Reloading Layertree !");
		part.load(mainCatalog, true);
		//partService.hidePart(parts.get(0));
	}
		
}