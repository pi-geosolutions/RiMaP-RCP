package fr.pigeo.rimap.rimaprcp.core.services.wms.internal;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import fr.pigeo.rimap.rimaprcp.core.wms.IWmsService;

public class WmsServiceContextFunction extends ContextFunction {
	 @Override
	  public Object compute(IEclipseContext context, String contextKey) {
	    
	    // create an instance of ITodoService
		 IWmsService wmsService = 
	        ContextInjectionFactory.make(WmsServiceImpl.class, context);
	    
	    // add this instance to the application context
	    // next invocation uses the instance from the application context
	    MApplication app = context.get(MApplication.class);
	    IEclipseContext appCtx = app.getContext();
	    appCtx.set(IWmsService.class, wmsService);

	    // return instance for the current invocation 
	    return wmsService;
	  }
}
