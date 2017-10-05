/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.view;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Sample view which uses a <tt>WorldWindowGLCanvas</tt>.
 */
public class View extends ViewPart
{
	/**
	 * Identifier for view.
	 */
	public static final String ID = "SampleApplication.view"; //$NON-NLS-1$

	/**
	 * AWT container for view.
	 */
	private Composite _embeddedContainer;

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @param parent The parent control.
	 */
	public void createPartControl(Composite parent)
	{
		// Setup AWT container.
		_embeddedContainer = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(_embeddedContainer);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		frame.add(panel);
		
		// Create World Wind canvas and add it to panel.
		WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
		wwd.setModel(new BasicModel());
		panel.add(wwd, java.awt.BorderLayout.CENTER);
	}

	/**
	 * Invoked when this part takes focus in the workbench.
	 */
	public void setFocus()
	{
		_embeddedContainer.setFocus();
	}
}
