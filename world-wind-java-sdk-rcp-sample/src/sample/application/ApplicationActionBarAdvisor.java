/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.application;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Advisor (configurer) for the action bar of the workbench window.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	/**
	 * Initializes a new instance of ApplicationActionBarAdvisor.
	 *  
	 * @param configurer The action bar configurer.
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
	{
		super(configurer);
	}
}
