/**
 * Implements ICatalog Interface.
 * Provides a reader on the ??-risk-gn2.10 layertree implementation
 */
package fr.pigeo.rimap.rimaprcp.catalog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jean
 *
 */
public class RiskCatalogImpl implements ICatalog {
	//config variables
	static String RIMAP_LAYERTREE_CHILDREN_TAG = "children";
	static String RIMAP_LAYERTREE_SERVICE_PATH = "/srv/fre/pigeo.layertree.get";
	
	
	private URL baseURL;
	private String filepath = "/home/jean/tmp/ne-risk-layertree.json";
	private JsonNode layertree;
	private Tree tree = null;

	/*
	 * Gets layertree data from base URL (e.g.
	 * http://ne-risk.pigeo.fr/ne-risk-gn2_10) TODO : implement recovery from
	 * URL + disk storage + offline capability
	 */
	public RiskCatalogImpl(String url) {
		//autocomplete URL if necessary
		if (!url.endsWith(".get")) {
			url=url+RIMAP_LAYERTREE_SERVICE_PATH;
		}
		
		try {
			this.baseURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			// TODO maybe try if it is not a file path instead of URL 
			e.printStackTrace();
		}
	}

	private void fillTree() {
		try {
			// We create the JsonParser using Jackson
			ObjectMapper objectMapper = new ObjectMapper();
			this.layertree = objectMapper.readValue(this.baseURL, JsonNode.class);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//if null, then it failed. We exit the function.
		if (this.layertree==null) {
			System.out.println("ERROR parsing layertree ("+this.getClass().getName()+")");
			return;
		}
		TreeItem item = new TreeItem(tree, SWT.NONE);
		this.loadChildren(item, this.layertree);
	}

	private void loadChildren(TreeItem parentItem, JsonNode parentNode) {
		if (parentNode.has(RIMAP_LAYERTREE_CHILDREN_TAG) && parentNode.get(RIMAP_LAYERTREE_CHILDREN_TAG).isArray()) {
			Iterator<JsonNode> itr = parentNode.get(RIMAP_LAYERTREE_CHILDREN_TAG).iterator();
			 while(itr.hasNext()) {
		         JsonNode child = itr.next();
		         TreeItem item = new TreeItem(parentItem, SWT.NONE);
		         item.setText(child.get("text").asText("node"));
		         System.out.println(item.getText());
		         this.loadChildren(item, child);
		      }
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.pigeo.rimap.rimaprcp.catalog.ICatalog#getTree()
	 */
	@Override
	public Tree getTree(Composite composite) {
		// if tree already instanciated
		if (this.tree != null)
			return this.tree;

		// else we initialize it
		this.tree = new Tree(composite, SWT.CHECK | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		this.tree.setLayoutData(data);
		this.fillTree();

		return this.tree;
	}

}
