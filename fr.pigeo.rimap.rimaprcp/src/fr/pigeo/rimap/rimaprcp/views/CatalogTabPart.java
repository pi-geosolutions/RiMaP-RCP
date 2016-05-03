
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.jface.AbstractCatalogComposite;
import fr.pigeo.rimap.rimaprcp.jface.PadreCatalogComposite;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class CatalogTabPart {
	@Inject 
	private IEventBroker eventBroker;
	@Inject 
	Logger logger; 

	private AbstractCatalogComposite catalog;
	
	@PostConstruct
	public void postConstruct(//@Preference IEclipsePreferences prefs, 
			Composite parent, final IEclipseContext ctx, final WwjInstance wwj,
			IPreferencesService prefService) {
		
		String project_url = prefService.getString("fr.pigeo.rimap.rimaprcp", "project.url", "not set", null);
		int web_usage_level = prefService.getInt("fr.pigeo.rimap.rimaprcp", "web.usage.level", 0, null);
		logger.info("Preference project url: "+project_url+" (web usage level is "+web_usage_level+")");
		//System.out.println("Preference project url: "+project_url);
		
		catalog = new PadreCatalogComposite(parent, SWT.BORDER_SOLID, project_url, web_usage_level, wwj, eventBroker);
		catalog.setLayout(new FillLayout());


	}
}