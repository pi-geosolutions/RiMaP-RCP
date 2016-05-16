package fr.pigeo.rimap.rimaprcp.core.services.security.internal;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;

public class SecureResourceServiceContextFunction extends ContextFunction {
	@Override
	  public Object compute(IEclipseContext context, String contextKey) {
	    
	    // create an instance of ITodoService
		ISecureResourceService secureResourceService = 
	        ContextInjectionFactory.make(SecureFileServiceImpl.class, context);
	    
	    // add this instance to the application context
	    // next invocation uses the instance from the application context
	    MApplication app = context.get(MApplication.class);
	    IEclipseContext appCtx = app.getContext();
	    appCtx.set(ISecureResourceService.class, secureResourceService);

	    // return instance for the current invocation 
	    return secureResourceService;
	  }
}
