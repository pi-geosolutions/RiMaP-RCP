 
package fr.pigeo.rimap.rimaprcp.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class PerspectiveSwitchHandler {

	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService,
			@Named("fr.pigeo.rimap.rimaprcp.commandparameter.perspectiveid") String perspectiveId) {
		MPerspective element = (MPerspective) modelService.find(perspectiveId, app);
		System.out.println("switching to "+perspectiveId);
		// now switch perspective
		if (element!=null)
			partService.switchPerspective(element);
	}

}