
package fr.pigeo.rimap.rimaprcp.cachemanager.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


/*
 * Unused class. Just kept here for reference..
 */

public class LayersCache {
	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService) {
		MPerspective element = (MPerspective) modelService.find("fr.pigeo.rimap.cachemanager.ui.perspective.main",
				app);
		// now switch perspective
		partService.switchPerspective(element);

		/*
		MTrimmedWindow win = (MTrimmedWindow) modelService.find("fr.pigeo.rimap.cachemanager.ui.window.main", app);
		MTrimmedWindow appwin = (MTrimmedWindow) modelService.find("fr.pigeo.rimap.rimaprcp.trimmedwindow.rimap3d", app);
		MTrimmedWindow mtw = (MTrimmedWindow) modelService.cloneElement(win, appwin);
		mtw.setVisible(true);
		mtw.setToBeRendered(true);*/
	}

}