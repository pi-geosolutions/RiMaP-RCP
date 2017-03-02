package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wb.swt.SWTResourceManager;

public class CButton extends CLabel {

	private Color bg = SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY);
	private Color shadow = SWTResourceManager.getColor(SWT.COLOR_GRAY);

	public CButton(Composite parent, int style) {
		super(parent, style);
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				setBackground(bg);
				notifyListeners(SWT.Selection, new Event());
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBackground(shadow);
			}
		});
		this.setAlignment(SWT.CENTER);
	}

	public void setBackgrounds(Color bgcolor, Color shadow) {
		super.setBackground(bgcolor);
		this.bg = bgcolor;
		this.shadow = shadow;
	}


	@Override
	public void dispose() {
		super.dispose();
	}

}
