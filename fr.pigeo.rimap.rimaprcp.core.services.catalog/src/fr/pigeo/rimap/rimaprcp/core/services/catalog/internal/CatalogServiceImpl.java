package fr.pigeo.rimap.rimaprcp.core.services.catalog.internal;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.osgi.service.prefs.Preferences;

import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.CatalogParams;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.PadreCatalog;

public class CatalogServiceImpl implements ICatalogService {

	@Inject
	IPreferencesService prefService;

	@Inject
	Logger logger;

	@Inject
	IEclipseContext context;
	
	@Inject ISessionService sessionService;
	
	ICatalog mainCatalog;

	@Override
	public ICatalog getMainCatalog() {

		if (sessionService == null) {
			logger.info("[CatalogService] Session service is null");
		} else {
			logger.info(
					"[CatalogService] Session service instanciated. Session username is " + sessionService.getSession().getUsername());
		}
		
		if (mainCatalog!=null) {
			return mainCatalog;
		}
		String baseurl = prefService.getString("fr.pigeo.rimap.rimaprcp", "project.baseurl", null, null);
		String layertreeService = prefService.getString("fr.pigeo.rimap.rimaprcp", "project.services.layertree",
				"not set", null);
		String layertree_service_url = baseurl + layertreeService;
		int web_usage_level = prefService.getInt("fr.pigeo.rimap.rimaprcp", "web.usage.level", 0, null);
		logger.info("Preference layertree service url: " + layertree_service_url + " (web usage level is "
				+ web_usage_level + ")");
		
		Preferences preferences = InstanceScope.INSTANCE.getNode("fr.pigeo.rimap.rimaprcp");
		Preferences config = preferences.node("config");
		String cachePath = config.get("cachePath", "RiMaP");

		CatalogParams params = new CatalogParams(layertree_service_url, "Layertree", "PadreCatalog", web_usage_level, cachePath);
		 // create new context
	    IEclipseContext catCtx = context.createChild();
	    
	    // add vars in local context
	    catCtx.set(CatalogParams.class, params);

	    // create WizardPages via CIF
		mainCatalog = ContextInjectionFactory.make(PadreCatalog.class, catCtx);
		
		return mainCatalog;
	}

	@Override
	public ICatalog newCatalog(String className, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICatalog getCatalog(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICatalog> getCatalogs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCatalog(String id) {
		// TODO Auto-generated method stub

	}
}
