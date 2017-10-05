/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.application;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Advisor (configurer) for the workbench window.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	/**
	 * Initializes a new instance of ApplicationWorkbenchWindowAdvisor.
	 *  
	 * @param configurer The action bar configurer.
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	/**
	 * Creates a new action bar advisor to configure the action bars of the window via the given action bar configurer.
	 * 
	 * @param configurer The action bar configurer for the window.
	 * @return           The action bar advisor for the window.
	 */
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ApplicationActionBarAdvisor(configurer);
	}

	/**
	 * Performs arbitrary actions before the window is opened.
	 */
	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("Sample World Wind RCP Application"); //$NON-NLS-1$
	}
}
