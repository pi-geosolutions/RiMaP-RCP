
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import gov.nasa.worldwind.Configuration;
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
	public void postConstruct(Composite parent,MApplication app) {
		// Setup AWT container.
		_embeddedContainer = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(_embeddedContainer);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		frame.add(panel);
		
		

//		// Create World Wind canvas and add it to panel.
//		System.setProperty(
//	            "gov.nasa.worldwind.config.file",
//	            "fr/pigeo/rimap/rimaprcp/config/wwj/worldwind.xml");
		System.setProperty(
	            "gov.nasa.worldwind.app.config.document",
	            "customconfig/worldwind.xml");
//        Configuration.setValue(
//        		"gov.nasa.worldwind.config.file",
//	            "fr/pigeo/rimap/rimaprcp/config/wwj/worldwind.xml");
		
		WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		wwd.setModel(m);
		//System.out.println(Configuration.getStringValue("gov.nasa.worldwind.config.file"));
		//System.out.println(System.getProperty("gov.nasa.worldwind.app.config.document"));
		//wwd.setModel(new BasicModel());
		// create a new local_ context
		  IEclipseContext localCtx = EclipseContextFactory.create("rimapRcpContext");
		  localCtx.set(WorldWindowGLCanvas.class, wwd);
		  // connect new local context with context hierarchy
		  localCtx.setParent(app.getContext());
		  
		  
		//gov.nasa.worldwindx.examples.ApplicationTemplate.insertBeforePlacenames(wwd, new LatLonGraticuleLayer());
		panel.add(wwd, java.awt.BorderLayout.CENTER);

	}

}