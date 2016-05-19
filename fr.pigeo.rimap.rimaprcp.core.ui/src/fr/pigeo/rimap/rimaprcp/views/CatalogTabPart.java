
package fr.pigeo.rimap.rimaprcp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import fr.pigeo.rimap.rimaprcp.core.catalog.CatalogViewContentProvider;
import fr.pigeo.rimap.rimaprcp.core.catalog.CatalogViewNodenameProvider;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalogService;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckable;
import fr.pigeo.rimap.rimaprcp.core.catalog.IExpandable;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class CatalogTabPart {
	@Inject
	private IEventBroker eventBroker;
	@Inject
	Logger logger;

	@Inject
	Display disp;

	private TreeViewer viewer;
	private Tree tree;

	@PostConstruct
	public void postConstruct(// @Preference IEclipsePreferences prefs,
			Composite parent, final IEclipseContext ctx, final WwjInstance wwj, IPreferencesService prefService,
			ICatalogService catalogService, ISessionService sessionService, Display display) {
		this.disp = display;

		ICatalog mainCatalog = catalogService.getMainCatalog();

		// TODO: Build the Part using the Catalog list (only mainCatalog as
		// first)

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new CatalogViewContentProvider());
		// viewer.setLabelProvider(new CatalogViewLabelProvider());
		// To support multiple columns. See
		// http://www.vogella.com/tutorials/EclipseJFaceTree/article.html
		TreeViewerColumn mainColumn = new TreeViewerColumn(viewer, SWT.NONE);
		mainColumn.getColumn().setWidth(300);
		mainColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new CatalogViewNodenameProvider()));

		viewer.setInput(mainCatalog.getRootNode());
		this.tree = viewer.getTree();
		this.tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// normally, we can click only 1 item at a time. So we get the
				// first one of the array
				if (tree.getSelection().length > 0) {
					TreeItem item = tree.getSelection()[0];
					if (item.getImage() != null) {
						if ((e.x > item.getImageBounds(0).x)
								&& (e.x < (item.getImageBounds(0).x + item.getImage().getBounds().width))) {
							if ((e.y > item.getImageBounds(0).y)
									&& (e.y < (item.getImageBounds(0).y + item.getImage().getBounds().height))) {
								if (item.getData() instanceof ICheckable) {
									ICheckable node = (ICheckable) item.getData();
									node.toggleChecked();
									item.setImage(node.getImage());
								}
							}
						}
					}
				}

			}
		});
		this.tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event e) {
				// Deals with folder expanded/folded image on expand/fold
				TreeItem item = (TreeItem) e.item;
				if (item.getData() instanceof IExpandable) {
					IExpandable folder = (IExpandable) item.getData();
					folder.setExpanded(true);
					item.setImage(folder.getImage());
				}
			}
		});
		this.tree.addListener(SWT.Collapse, new Listener() {
			public void handleEvent(Event e) {
				// Deals with folder expanded/folded image on expand/fold
				TreeItem item = (TreeItem) e.item;
				if (item.getData() instanceof IExpandable) {
					IExpandable folder = (IExpandable) item.getData();
					folder.setExpanded(true);
					item.setImage(folder.getImage());
				}
			}
		});

		// To reserve max space to first columne. See
		// http://www.vogella.com/tutorials/EclipseJFaceTree/article.html,
		// chapter 2.3
		Listener packListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				TreeItem treeItem = (TreeItem) event.item;
				final TreeColumn[] treeColumns = treeItem.getParent().getColumns();
				disp.asyncExec(new Runnable() {

					@Override
					public void run() {
						for (TreeColumn treeColumn : treeColumns)
							treeColumn.pack();
					}
				});
			}
		};

		this.tree.addListener(SWT.Expand, packListener);
	}
}