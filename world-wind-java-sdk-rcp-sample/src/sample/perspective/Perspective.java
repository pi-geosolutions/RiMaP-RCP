/*!
 * @file
 * Copyright (c) jdknight. All rights reserved.
 *
 * The MIT License (MIT).
 */

package sample.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective for sample World Wind RCP SDK.
 */
public class Perspective implements IPerspectiveFactory
{
	/**
	 * Creates the initial layout for a page. 
	 * 
	 * @param layout The page layout.
	 */
	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
	}
}
