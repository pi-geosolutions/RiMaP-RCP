 
package fr.pigeo.rimap.rimaprcp.core.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.core.ui.swt.GeocatSearchForm;

public class geocatalogue {
	@Inject
	public geocatalogue() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		PShelf shelf = new PShelf(parent, SWT.NONE);
	    
	//  Optionally, change the renderer
	  shelf.setRenderer(new RedmondShelfRenderer());

	    PShelfItem item1 = new PShelfItem(shelf,SWT.NONE);
	    item1.setText("Search");
	    
	    item1.getBody().setLayout(new FillLayout());
	    
		GeocatSearchForm searchForm = new GeocatSearchForm(item1.getBody(), SWT.NONE);
		
	    PShelfItem item2 = new PShelfItem(shelf,SWT.NONE);
	    item2.setText("Results");
	    
	    item2.getBody().setLayout(new FillLayout());
	    
		new GeocatSearchForm(item2.getBody(), SWT.NONE);
	}
	
	
	
	
}