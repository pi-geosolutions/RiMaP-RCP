
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.catalog.PadreCatalogParams;
import fr.pigeo.rimap.rimaprcp.jface.AbstractCatalogComposite;
import fr.pigeo.rimap.rimaprcp.jface.PadreCatalogComposite;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class CatalogTabPart {
	@Inject 
	private IEventBroker eventBroker;

	private AbstractCatalogComposite catalog;
	
	@PostConstruct
	public void postConstruct(@Preference IEclipsePreferences prefs, Composite parent, final IEclipseContext ctx, final WwjInstance wwj) {
		String project_url = prefs.get("project_url", "http://gm-risk.pigeo.fr/gm-risk-gn2_10/srv/fre/pigeo.layertree.get");
		if (project_url==null) {
			Button btn =  new Button(parent, SWT.NONE);
			btn.setText("Set project");
		} else {
			PadreCatalogParams params = new PadreCatalogParams(project_url);
			
			catalog = new PadreCatalogComposite(parent, SWT.BORDER_SOLID, project_url, wwj, eventBroker);
			catalog.setLayout(new FillLayout());
		}


	}
}