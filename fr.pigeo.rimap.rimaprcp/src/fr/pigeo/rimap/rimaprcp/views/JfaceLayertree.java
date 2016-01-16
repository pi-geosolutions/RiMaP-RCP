
package fr.pigeo.rimap.rimaprcp.views;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogViewContentProvider;
import fr.pigeo.rimap.rimaprcp.catalog.RiskCatalogViewLabelProvider;
import fr.pigeo.rimap.rimaprcp.catalog.RiskJfaceCatalogImpl;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.WmsLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

public class JfaceLayertree {
	@Inject 
	private IEventBroker eventBroker;
	
	private TreeViewer viewer;
	private Tree tree;

	@PostConstruct
	public void postConstruct(Composite parent, final IEclipseContext ctx, final WwjInstance wwj) {
		// new Button(parent, SWT.CHECK);

		RiskJfaceCatalogImpl catalog = new RiskJfaceCatalogImpl();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new RiskCatalogViewContentProvider());
		viewer.setLabelProvider(new RiskCatalogViewLabelProvider());
		viewer.setInput(catalog.getRoot());
		viewer.setExpandedElements(FolderLayer.getExpandedFolders());
		this.tree = viewer.getTree();
		this.tree.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// normally, we can select only 1 item at a time. So we get the
				// first one of the array
				if (tree.getSelection().length > 0) {
					TreeItem item = tree.getSelection()[0];
					if(item.getImage() != null) {
						if((e.x > item.getImageBounds(0).x) && (e.x < (item.getImageBounds(0).x + item.getImage().getBounds().width))) {
							if((e.y > item.getImageBounds(0).y) && (e.y < (item.getImageBounds(0).y + item.getImage().getBounds().height))) {
								if (item.getData() instanceof AbstractLayer) {
									AbstractLayer layer = (AbstractLayer) item.getData();
									layer.setChecked();
									item.setImage(layer.getImage());
									checkedLayer(layer, ctx, wwj);
								}
							}
						}
					}
					//Deals with folder expanded/folded image on expand/fold
					if (item.getData() instanceof FolderLayer){
						FolderLayer layer = (FolderLayer) item.getData();
						layer.setExpanded(item.getExpanded());
						item.setImage(layer.getImage());
					}
				}

			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
		this.tree.addListener(SWT.Expand, new Listener() {
		      public void handleEvent(Event e) {
		        System.out.println("Expand={" + e.item + "}");
		      //Deals with folder expanded/folded image on expand/fold
		        TreeItem item = (TreeItem) e.item;
				if (item.getData() instanceof FolderLayer){
					FolderLayer layer = (FolderLayer) item.getData();
					layer.setExpanded(true);
					item.setImage(layer.getImage());
				}
		      }
		});
		this.tree.addListener(SWT.Collapse, new Listener() {
		      public void handleEvent(Event e) {
		        System.out.println("Collapse={" + e.item + "}");
		      //Deals with folder expanded/folded image on expand/fold
		        TreeItem item = (TreeItem) e.item;
				if (item.getData() instanceof FolderLayer){
					FolderLayer layer = (FolderLayer) item.getData();
					layer.setExpanded(false);
					item.setImage(layer.getImage());
				}
		      }
		});


	}
	
	public void checkedLayer(AbstractLayer layer, IEclipseContext ctx, WwjInstance wwj) {
		WorldWindowGLCanvas wwd = wwj.getWwd();

		if (layer instanceof WmsLayer) {
			layer.addToGlobe(wwd);
		}
		eventBroker.post(RimaprcpConstants.LAYER_CHECKED, layer ); 
	}
}