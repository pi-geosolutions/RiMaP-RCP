 
package fr.pigeo.rimap.rimaprcp.cachemanager.ui.views;

import javax.inject.Inject;
import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

public class bulk {
	private Table table;
	@Inject
	public bulk() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Composite composite = new Composite(parent, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rl_composite);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		Button btnDestDir = new Button(composite, SWT.NONE);
		btnDestDir.setToolTipText("Select destination dir");
		btnDestDir.setText("...");
		
		Button btnDownload = new Button(composite, SWT.NONE);
		btnDownload.setText("Start Download");
		
	}
	
	
	
	
}