
package fr.pigeo.rimap.rimaprcp.core.ui.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.util.StatusBar;

public class WWJView {
	/**
	 * Identifier for view.
	 */
	public static final String ID = "WorldWindJava.view"; //$NON-NLS-1$

	/**
	 * AWT container for view.
	 */
	private Composite _embeddedContainer;

	@Inject
	public WWJView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent,MApplication app, WwjInstance wwj, @Preference(nodePath = RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE) IEclipsePreferences prefs) {
		// Setup AWT container.
		_embeddedContainer = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(_embeddedContainer);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		frame.add(panel);
		  
		//gov.nasa.worldwindx.examples.ApplicationTemplate.insertBeforePlacenames(wwd, new LatLonGraticuleLayer());
		wwj.initialize(prefs);
		panel.add(wwj.getWwd(), java.awt.BorderLayout.CENTER);
		StatusBar statusBar = new StatusBar();
		panel.add(statusBar, BorderLayout.PAGE_END);
        statusBar.setEventSource(wwj.getWwd());
	}

}