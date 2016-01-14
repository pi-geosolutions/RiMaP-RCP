
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

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
	public void postConstruct(Composite parent,MApplication app, WwjInstance wwj) {
		// Setup AWT container.
		_embeddedContainer = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(_embeddedContainer);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		frame.add(panel);
		  
		//gov.nasa.worldwindx.examples.ApplicationTemplate.insertBeforePlacenames(wwd, new LatLonGraticuleLayer());
		panel.add(wwj.getWwd(), java.awt.BorderLayout.CENTER);

	}

}