package fr.pigeo.rimap.rimaprcp.core.services.resource.internal;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;

public class ResourceServiceContextFunction extends ContextFunction {
	@Override
	  public Object compute(IEclipseContext context, String contextKey) {
	    
	    // create an instance of ITodoService
	    IResourceService resourceService = 
	        ContextInjectionFactory.make(ResourceServiceImpl.class, context);
	    
	    // add this instance to the application context
	    // next invocation uses the instance from the application context
	    MApplication app = context.get(MApplication.class);
	    IEclipseContext appCtx = app.getContext();
	    appCtx.set(IResourceService.class, resourceService);

	    // return instance for the current invocation 
	    return resourceService;
	  }
}
