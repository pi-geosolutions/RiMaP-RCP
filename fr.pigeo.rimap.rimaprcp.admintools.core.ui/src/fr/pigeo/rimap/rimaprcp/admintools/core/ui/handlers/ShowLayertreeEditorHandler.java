 
package fr.pigeo.rimap.rimaprcp.admintools.core.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ShowLayertreeEditorHandler {
	@Execute
	public void execute(EPartService partService) {
		MPart layertreeEditorPart = partService.findPart("fr.pigeo.rimap.rimaprcp.admintools.core.ui.part.layertreeeditor");
			//show the part
			layertreeEditorPart.setVisible(true); // required if initial not visible
			partService.showPart(layertreeEditorPart, PartState.VISIBLE);
	}
		
}