
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import fr.pigeo.rimap.rimaprcp.core.ui.core.Central;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.PolygonQuery;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;

public class PolygonQueryResultsPart {
	private TableViewer viewer;
	private Browser browser;
	private Locale locale;

	@Inject
	@Translation
	Messages messages;
	
	@Inject UISynchronize sync;

	@Inject
	public PolygonQueryResultsPart() {

	}

	@PostConstruct
	public void postConstruct(Display display, Composite parent, Central central, MPart part, IEclipseContext context, final PolygonQuery pq) {
		if(locale==null) {
			locale = (Locale) context.get(TranslationService.LOCALE);
		}
		part.setIconURI("platform:/plugin/fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui/icons/polygon_query_16px.png");
		GridLayout gl_parent = new GridLayout(2, false);
		gl_parent.marginWidth = 0;
		parent.setLayout(gl_parent);

		/*Label lblPos = new Label(parent, SWT.NONE);
		lblPos.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblPos.setText("Polygon query ");*/

		// define the TableViewer
		viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		table.setHeaderVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// create column for the Name of the layers
		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IPolygonQueryableLayer el = (IPolygonQueryableLayer) element;
				return el.getName();
			}
		});
		colName.getColumn().setWidth(200);
		colName.getColumn().setText(messages.polygonquery_layers);

		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		viewer.setInput(pq.getLayers());
		
		//needed for Windows env. (elsewise, nothing is selected by default):
		updateBrowser(browser, pq.getLayers().get(0), pq);
		//browser.setText(pq.getStats(pq.getLayers().get(0)));
		

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					IPolygonQueryableLayer target = (IPolygonQueryableLayer) selection.getFirstElement();
					updateBrowser(browser, target, pq);
					//browser.setText(pq.getStats(target));
				}
			}
		});
	}
	
	private void updateBrowser(final Browser browser, final IPolygonQueryableLayer layer, final PolygonQuery pq) {
		browser.setText(messages.loading);
		Job job = new Job("My Job") {
				String html="";
			
			  @Override
			  protected IStatus run(IProgressMonitor monitor) {
				 html = pq.getStats(layer);
				  
			    sync.asyncExec(new Runnable() {
			      @Override
			      public void run() {
			        browser.setText(html);
			      }
			    });
			    return Status.OK_STATUS;
			  }
			};

			// Start the Job
			job.schedule(); 
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}