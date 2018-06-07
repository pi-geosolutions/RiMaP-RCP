
package fr.pigeo.rimap.rimaprcp.core.ui.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.core.ui.swt.WaterLevelDialog;
import fr.pigeo.rimap.rimaprcp.core.ui.tools.WaterLevel;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.util.SectorSelector;

public class WaterLevelHandler {
	WaterLevelDialog dialog;
	
	@Execute
	public void execute(IEclipseContext context, WwjInstance wwj, IEventBroker evtBroker, Shell shell, WaterLevel wlmanager) {
		if (dialog == null) {
			dialog = new WaterLevelDialog(shell, evtBroker);
		}
		ContextInjectionFactory.inject(dialog, context);
		dialog.open();

	}

}