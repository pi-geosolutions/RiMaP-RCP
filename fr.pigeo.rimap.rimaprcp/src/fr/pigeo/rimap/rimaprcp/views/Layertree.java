
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogImpl;

public class Layertree {
	@Inject
	public Layertree() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		//new Button(parent, SWT.CHECK); 
		/*
		Tree check = new Tree(parent, SWT.CHECK | SWT.BORDER);
		GridData  data = new GridData(GridData.FILL_BOTH);
		  check.setLayoutData(data);
		  fillTree(check);
		*/
		String url = "http://ne-risk.pigeo.fr/ne-risk-gn2_10";
		//String url = "http://127.0.0.1/dev/layertree/layertree.js";
		RiskCatalogImpl catalog = new RiskCatalogImpl(url);
		System.out.println("loading from "+url);

		//parent.setLayout(new GridLayout(1, true));
		//Composite top = new Composite(parent, SWT.NONE);
		//GridData data = new GridData(GridData.FILL_BOTH);
		//top.setLayoutData(data);
		Tree tree = catalog.getTree(parent);
		  fillTree(tree);
		tree.setRedraw(true);
	}
	private void fillTree(Tree tree) {
		  // Turn off drawing to avoid flicker
		  tree.setRedraw(false);

		  // Create five root items
		  for (int i = 0; i < 5; i++) {
		    TreeItem item = new TreeItem(tree, SWT.NONE);
		    item.setText("Root Item " + i);

		    // Create three children below the root
		    for (int j = 0; j < 3; j++) {
		      TreeItem child = new TreeItem(item, SWT.NONE);
		      child.setText("Child Item " + i + " - " + j);

		      // Create three grandchildren under the child
		      for (int k = 0; k < 3; k++) {
		        TreeItem grandChild = new TreeItem(child, SWT.NONE);
		        grandChild.setText("Grandchild Item " + i + " - " + j + " - " + k);
		      }
		    }
		  }
		  // Turn drawing back on!
		  tree.setRedraw(true);
		}

}