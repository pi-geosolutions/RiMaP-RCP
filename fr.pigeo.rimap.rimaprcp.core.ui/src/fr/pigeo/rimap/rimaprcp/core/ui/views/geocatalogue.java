 
package fr.pigeo.rimap.rimaprcp.core.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.core.ui.swt.GeocatSearchFormImpl;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataToolBox;

public class geocatalogue {
	@Inject
	public geocatalogue() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext context, GeocatMetadataToolBox searchTools) {	    
		GeocatSearchFormImpl gsf = new GeocatSearchFormImpl(parent, SWT.NONE);
		context.set(GeocatMetadataToolBox.class, searchTools);
		ContextInjectionFactory.inject(gsf, context);
		gsf.enhanceControls();
	}
	
	
	
	
}