 
package fr.pigeo.rimap.rimaprcp.core.ui.handlers;

import java.util.Map.Entry;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;

public class PerspectiveSwitchHandler {

	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService, IEventBroker eventBroker,
			@Named("fr.pigeo.rimap.rimaprcp.commandparameter.perspectiveid") String perspectiveId) {
		
		MPerspective element = (MPerspective) modelService.find(perspectiveId, app);
		//System.out.println("switching to "+perspectiveId);
		// now switch perspective
		if (element!=null)
			eventBroker.send(RiMaPEventConstants.LEAVING_PERSPECTIVE, perspectiveId);
			partService.switchPerspective(element);
	}
	@CanExecute
	public boolean canExecute(final IEclipseContext ictx) {
	    /*final EclipseContext ctx = (EclipseContext) ictx.getParent();
	    System.out.println("### START ###");
	    for (final Entry<String, Object> entry : ctx.localData().entrySet()) {
	        System.out.println(String.format("Key: '%s', value: '%s'", entry.getKey(), entry.getValue()));
	    }
	    System.out.println("### END ###");*/
	    return true;
	}

}