 
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogViewContentProvider;
import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogViewLabelProvider;
import fr.pigeo.rimap.rimaprcp.catalog.RiskJfaceCatalogImpl;

public class JfaceLayertree {
	private TreeViewer viewer;
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		//new Button(parent, SWT.CHECK); 
		
		String url = "http://ne-risk.pigeo.fr/ne-risk-gn2_10";
		RiskJfaceCatalogImpl catalog = new RiskJfaceCatalogImpl(url);
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new RiskCatalogViewContentProvider());
		viewer.setLabelProvider(new RiskCatalogViewLabelProvider());
		viewer.setInput(catalog.getRoot());
	}
}