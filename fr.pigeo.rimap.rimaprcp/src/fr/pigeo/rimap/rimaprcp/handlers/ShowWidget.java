 
package fr.pigeo.rimap.rimaprcp.handlers;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class ShowWidget {
	@Execute
	public void execute(@Preference(nodePath = "fr.pigeo.rimap.rimaprcp") IEclipsePreferences prefs,
			WwjInstance wwj,
			@Named("fr.pigeo.rimap.rimaprcp.commandparameter.show_wwj_widget_ref") String widgetRef) {
		System.out.println("current widget : "+widgetRef);
		boolean show = prefs.getBoolean("show_"+widgetRef, true);
		
		wwj.showWidget(!show, widgetRef);
		prefs.putBoolean("show_"+widgetRef, !show);
		try {
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("setting "+widgetRef+" visibility to "+!show);
	}
		
}