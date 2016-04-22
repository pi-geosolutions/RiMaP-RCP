 
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import fr.pigeo.rimap.rimaprcp.core.Central;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfoTarget;

public class FeatureInfoResultsPart {
	private TableViewer viewer;
	
	@Inject
	public FeatureInfoResultsPart() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent, Central central) {
		System.out.println("OK");
		GridLayout gl_parent = new GridLayout(2, false);
	    gl_parent.marginRight = 10;
	    gl_parent.marginLeft = 10;
	    gl_parent.horizontalSpacing = 10;
	    gl_parent.marginWidth = 0;
	    parent.setLayout(gl_parent);
	    
	    Label lblTitle = new Label(parent, SWT.NONE);
	    lblTitle.setText("FeatureInfo results");
	    
	    ArrayList<FeatureInfoTarget> targets = (ArrayList<FeatureInfoTarget>) central.get("fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfoTargets");
	    if (targets==null || targets.size()==0) {
	    	return;
	    }

	    Label lblPos = new Label(parent, SWT.NONE);
	    lblPos.setText("Position: "+targets.get(0).getPosition().toString());
	    
	    /*Iterator<FeatureInfoTarget> it = targets.iterator();
	    while (it.hasNext()) {
	    	FeatureInfoTarget fit = it.next();
	    	Label lbl = new Label(parent, SWT.NONE);
		    lbl.setText(fit.getLayer().getName());
	    }*/
	    
	    //define the TableViewer
	    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
	          | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

	    // create the columns 
	    // not yet implemented
	    //createColumns(viewer);

	    // make lines and header visible
	    final Table table = viewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true); 
	    viewer.setContentProvider(ArrayContentProvider.getInstance());
	 // create column for the Name of the layers
	    TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
	    colName.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FeatureInfoTarget fit = (FeatureInfoTarget) element;
	        return fit.getLayer().getName();
	      }
	    });
	    colName.getColumn().setWidth(100);
	    colName.getColumn().setText("Layer");
	    viewer.setInput(targets.toArray());
	}
	
	
	
	
}