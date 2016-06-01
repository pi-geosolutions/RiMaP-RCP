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
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
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

	@Inject
	ISessionService sessionService;

	ICatalog mainCatalog;

	@Override
	public ICatalog getMainCatalog() {

		if (sessionService == null) {
			logger.info("[CatalogService] Session service is null");
		} else {
			logger.info(
					"[CatalogService] Session service instanciated. Session username is " + sessionService.getSession()
							.getUsername());
		}

		if (mainCatalog != null) {
			return mainCatalog;
		}
		String baseurl = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT, null);
		String layertreeService = prefService.getString(CatalogConstants.PREFERENCES_NODE,
				CatalogConstants.MAINCATALOG_LAYERTREE_RELPATH_PREF_TAG,
				CatalogConstants.MAINCATALOG_LAYERTREE_RELPATH_PREF_DEFAULT, null);
		String layertree_service_url = baseurl + layertreeService;
		int web_usage_level = prefService.getInt(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.WEB_USAGE_LEVEL_PREF_TAG, RimapConstants.WEB_USAGE_LEVEL_PREF_DEFAULT, null);
		logger.info("Preference layertree service url: " + layertree_service_url + " (web usage level is "
				+ web_usage_level + ")");

		Preferences preferences = InstanceScope.INSTANCE.getNode(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE);
		Preferences config = preferences.node("config");
		String cachePath = config.get(RimapConstants.CACHE_PATH_PREF_TAG, RimapConstants.CACHE_PATH_PREF_DEFAULT);

		CatalogParams params = new CatalogParams(layertree_service_url, "layertree", "PadreCatalog", web_usage_level,
				cachePath);
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
