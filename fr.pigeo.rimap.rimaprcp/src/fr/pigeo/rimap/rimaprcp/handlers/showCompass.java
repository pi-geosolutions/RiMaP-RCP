 
package fr.pigeo.rimap.rimaprcp.handlers;

import java.util.prefs.BackingStoreException;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class showCompass {
	@Execute
	public void execute(@Preference(nodePath = "fr.pigeo.rimap.rimaprcp") IEclipsePreferences prefs, WwjInstance wwj) {
		boolean show = prefs.getBoolean("showcompass", true);
		
		wwj.showCompass(!show);
		prefs.putBoolean("showcompass", !show);
		try {
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("setting compass visibility to "+!show);
	}
		
}