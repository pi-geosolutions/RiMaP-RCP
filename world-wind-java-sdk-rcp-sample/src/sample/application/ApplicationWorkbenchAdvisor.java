/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.application;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Advisor (configurer) for the workbench.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	/**
	 * Identifier for the perspective.
	 */
	private static final String PERSPECTIVE_ID = "SampleApplication.perspective"; //$NON-NLS-1$

	/**
	 * Creates a new workbench window advisor for configuring a new workbench window via the given workbench window configurer.
	 * 
	 * @param configurer The workbench window configurer.
	 * @return           A new workbench window advisor.
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/**
	 * Returns the identifier of the perspective to use for the initial workbench window.
	 * 
	 * @return The identifier.
	 */
	public String getInitialWindowPerspectiveId()
	{
		return PERSPECTIVE_ID;
	}
}
