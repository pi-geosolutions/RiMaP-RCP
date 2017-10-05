/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.application;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Sample World Wind RCP SDK application.
 */
public class SampleRcpApplication implements IApplication
{
	/**
	 * Start the application on the provided context.
	 * 
	 * @param context The application context to pass to the application.
	 * @return        The return value of the application.
	 */
	public Object start(IApplicationContext context)
	{
		Display display = PlatformUI.createDisplay();
		try
		{
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
			{
				return IApplication.EXIT_RESTART;
			}
			
			return IApplication.EXIT_OK;
		}
		finally
		{
			display.dispose();
		}
	}

	/**
	 * Forces this running application to exit.
	 */
	public void stop()
	{
		if (PlatformUI.isWorkbenchRunning() == false)
		{
			return;
		}
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (display.isDisposed() == false)
				{
					workbench.close();
				}
			}
		});
	}
}
