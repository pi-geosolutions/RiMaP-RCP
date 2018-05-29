 
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ContActsViewPart {
	@Inject
	MPart part;
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText((String)part.getTransientData().get("message"));
	}
	
	
	
	
}