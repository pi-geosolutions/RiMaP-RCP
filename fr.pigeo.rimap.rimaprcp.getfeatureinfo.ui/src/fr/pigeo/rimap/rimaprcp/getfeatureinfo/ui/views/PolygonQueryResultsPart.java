
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.ui.core.Central;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.PolygonQuery;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContActs;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContActs.CounterEntry;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IPolygonQueryableLayer;

public class PolygonQueryResultsPart {
	private TableViewer viewer;
	private Browser browser;
	private Locale locale;
	private PolygonQuery polygonquery;
	protected Composite composite;

	@Inject
	@Translation
	Messages messages;

	@Inject
	UISynchronize sync;

	@Inject
	IEventBroker eventBroker;

	@Inject
	public PolygonQueryResultsPart() {

	}

	protected final Image VIEW = getImage("eye.png");
	protected final Image SEND = getImage("transmit.png");
	protected final Image SENDMAIL = getImage("email_go.png");

	@PostConstruct
	public void postConstruct(Display display, Composite parent, Central central, MPart part, IEclipseContext context,
			final PolygonQuery pq) {
		this.polygonquery = pq;
		if (locale == null) {
			locale = (Locale) context.get(TranslationService.LOCALE);
		}
		part.setIconURI("platform:/plugin/fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui/icons/polygon_query_16px.png");
		GridLayout gl_parent = new GridLayout(3, false);
		gl_parent.marginRight = 5;
		gl_parent.marginLeft = 5;
		gl_parent.verticalSpacing = 0;
		gl_parent.marginHeight = 0;
		gl_parent.marginWidth = 0;
		parent.setLayout(gl_parent);

		/*
		 * Label lblPos = new Label(parent, SWT.NONE);
		 * lblPos.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
		 * 2, 1));
		 * lblPos.setText("Polygon query ");
		 */

		// define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
		colName.getColumn()
				.setWidth(200);
		colName.getColumn()
				.setText(messages.polygonquery_layers);
		new Label(parent, SWT.NONE);

		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// browser = new Browser(parent, SWT.NONE);
		// browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		// 1));
		viewer.setInput(pq.getLayers());

		// needed for Windows env. (elsewise, nothing is selected by default):
		// updateBrowser(browser, pq.getLayers().get(0), pq);
		// browser.setText(pq.getStats(pq.getLayers().get(0)));

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					IPolygonQueryableLayer target = (IPolygonQueryableLayer) selection.getFirstElement();
					updateDisplay(target, pq);
					// browser.setText(pq.getStats(target));
				}
			}
		});
	}

	private void updateDisplay(final IPolygonQueryableLayer layer, final PolygonQuery pq) {
		System.out.println("Displaying results for layer " + layer.getName());

		this.clearComposite(composite);
		switch (layer.getParams()
				.getType()) {
		case RasterSTATS:
			System.out.println("Requesting Raster Stats");
			browser = new Browser(composite, SWT.NONE);
			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			composite.layout(true);
			this.updateBrowser(browser, layer, pq);
			break;
		case MobileService:
			System.out.println("Opting to Mobile Service");
			Job job = new Job("Load contActs") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					ContActs c = pq.getContActs(layer);

					sync.asyncExec(new Runnable() {
						@Override
						public void run() {
							displayContactsInLayerComposite(c);
						}
					});
					return Status.OK_STATUS;
				}
			};

			// Start the Job
			job.schedule();
			break;
		}

	}

	private void clearComposite(Composite compo) {
		for (Control control : compo.getChildren()) {
			control.dispose();
		}
	}

	private void updateBrowser(final Browser browser, final IPolygonQueryableLayer layer, final PolygonQuery pq) {
		browser.setText(messages.loading);
		Job job = new Job("My Job") {
			String html = "";

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

	private TableViewerColumn createTableViewerColumn(TableViewer parent, String header, int width) {
		TableViewerColumn column = new TableViewerColumn(parent, SWT.NONE);
		column.getColumn()
				.setText(header);
		column.getColumn()
				.setWidth(width);
		column.getColumn()
				.setResizable(true);
		column.getColumn()
				.setMoveable(true);

		return column;
	}

	private void displayContactsInLayerComposite(ContActs c) {
		ContActs contacts = c;
		TableViewer tv = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);

		// make lines and header visible
		final Table tbl = tv.getTable();
		tbl.setLinesVisible(true);
		tbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tbl.setHeaderVisible(true);
		tv.setContentProvider(ArrayContentProvider.getInstance());

		// Contact mode
		TableViewerColumn colMode = createTableViewerColumn(tv, messages.polygonquery_ms_mode, 120);
		colMode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String mode = ((CounterEntry) element).getCategory();
				return mode;
			}
		});

		// count
		TableViewerColumn colCount = createTableViewerColumn(tv, messages.polygonquery_ms_count, 120);
		colCount.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int count = ((CounterEntry) element).getCount();
				return String.valueOf(count);
			}
		});

		// peoplecount
		TableViewerColumn colPCount = createTableViewerColumn(tv, messages.polygonquery_ms_habcount, 120);
		colPCount.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int count = ((CounterEntry) element).getInhabitants();
				return String.valueOf(count);
			}
		});

		// percentage
		TableViewerColumn colPerc = createTableViewerColumn(tv, messages.polygonquery_ms_perc, 120);
		colPerc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int count = ((CounterEntry) element).getCount();
				float perc = 100.0f * ((float) count) / ((float) c.getTotalCount());
				return String.valueOf(Math.round(perc)) + " % ";
			}
		});

		// percentage of People
		TableViewerColumn colPPerc = createTableViewerColumn(tv, messages.polygonquery_ms_habperc, 120);
		colPPerc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int count = ((CounterEntry) element).getInhabitants();
				float perc = 100.0f * ((float) count) / ((float) c.getTotalInhabitantsCount());
				return String.valueOf(Math.round(perc)) + " % ";
			}
		});

		// view action
		TableViewerColumn colView = createTableViewerColumn(tv, "", 50);
		colView.setLabelProvider(new ContactButtonColumnLabelProvider(messages.polygonquery_ms_view_label, c, eventBroker, QueryEventConstants.POLYGONQUERY_MS_SHOW_CONTACTS_LIST));
		
		// send action
		TableViewerColumn colSend = createTableViewerColumn(tv, "", 80);
		colSend.setLabelProvider(new ContactButtonColumnLabelProvider(messages.polygonquery_ms_send_label, c, eventBroker, QueryEventConstants.POLYGONQUERY_MS_SEND));
	
		tv.setInput(c.getCounter());
		composite.layout(true);
	}

	@Focus
	public void setFocus() {
		viewer.getControl()
				.setFocus();
	}

	@PreDestroy
	public void onClose() {
		this.polygonquery.hidePolygon();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Closed tabs at " + sdf.format(cal.getTime()));
	}

	// helper method to load the images
	// ensure to dispose the images in your @PreDestroy method
	protected static Image getImage(String file) {
		return getImage(file, null);
	}

	// helper method to load the images
	// ensure to dispose the images in your @PreDestroy method
	protected static Image getImage(String file, Class refclass) {
		if (refclass == null) {
			// we assume we are using this bundle
			refclass = PolygonQueryResultsPart.class;
		}
		// assume that the current class is called View.java
		Bundle bundle = FrameworkUtil.getBundle(refclass);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	private class ContactButtonColumnLabelProvider extends ColumnLabelProvider {
		// TODO: make sure you dispose these buttons when viewer input
		// changes
		Map<Object, Button> buttons = new HashMap<Object, Button>();
		ContActs c;
		IEventBroker eventBroker;
		String eventID;
		String buttonLabel;

		public ContactButtonColumnLabelProvider(String buttonLabel, ContActs contacts, IEventBroker evt, String eventID) {
			this.c = contacts;
			this.eventBroker = evt;
			this.buttonLabel = buttonLabel;
			this.eventID = eventID;
		}

		@Override
		public void update(ViewerCell cell) {
			CounterEntry elt = ((CounterEntry) cell.getElement());
			if (elt.canSendMessage() && elt.getCount() > 0) {
				TableItem item = (TableItem) cell.getItem();
				Button button;
				if (buttons.containsKey(cell.getElement())) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow()
							.getControl(), SWT.NONE);
					button.setText(buttonLabel);
					buttons.put(cell.getElement(), button);
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (eventBroker != null) {
								Map<String, Object> h = new HashMap<String, Object>();
								h.put("mode", elt.getCategory());
								h.put("contacts", c);
								Map<String, Object> eventMap = new HashMap<String, Object>();
								// Data map goes under the `IEventBroker.DATA` key, cf. https://stackoverflow.com/questions/34039914/eclipse-e4-eventbroker-mapstring-string-nullpointer
								eventMap.put(IEventBroker.DATA, h);
								eventBroker.send(eventID, eventMap);
							}
						}

					});
					TableEditor editor = new TableEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(button, item, cell.getColumnIndex());
					editor.layout();
				}
			}
		}
	}

}