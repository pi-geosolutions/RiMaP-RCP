package fr.pigeo.rimap.rimaprcp.core.ui.statusbar.jface;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class StatusBar {
	
	@PostConstruct
	public void createGui(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
	    comp.setLayout(new GridLayout());
	    Text text = new Text(comp, SWT.NONE);
	    text.setMessage("Search");
	}
}