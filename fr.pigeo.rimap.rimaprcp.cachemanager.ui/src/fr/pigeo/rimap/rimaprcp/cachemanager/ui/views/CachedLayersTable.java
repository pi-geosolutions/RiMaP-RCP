
package fr.pigeo.rimap.rimaprcp.cachemanager.ui.views;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import fr.pigeo.rimap.rimaprcp.cachemanager.ui.utils.CachedDataSetViewerComparator;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheUtil;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CachedDataSet;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.RenderableManager;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;

public class CachedLayersTable {
	private TableViewer tableViewer;

	private CachedDataSetViewerComparator comparator;
	protected String searchString = "";

	private List<CachedDataSet> cdsList = new ArrayList<CachedDataSet>();

	@Inject
	@Optional
	IEventBroker evtBroker;

	@Inject
	@Optional
	UISynchronize synch;

	@PostConstruct
	public void postConstruct(Composite parent, RenderableManager renderableManager,
			IEventBroker eventBroker, final UISynchronize synch) {
		this.evtBroker = eventBroker;
		this.synch = synch;
		parent.setLayout(new GridLayout(1, false));

		Text search = new Text(parent, SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);

		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		search.setMessage("Filter");
		// filter at every keystroke
		search.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text source = (Text) e.getSource();
				searchString = source.getText();
				// trigger update in the viewer
				tableViewer.refresh();
			}
		});

		// SWT.SEARCH | SWT.CANCEL is not supported under Windows7 and
		// so the following SelectionListener will not work under Windows7
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					Text text = (Text) e.getSource();
					text.setText("");
				}
			}
		});

		// define the TableViewer
		tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// create the columns
		// not yet implemented
		createColumns(tableViewer);

		// make lines and header visible
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// set the content provider
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(cdsList.toArray());
		this.listCachedLayers();

		// Delete Layer button
		Button buttonDelete = new Button(parent, SWT.PUSH);
		buttonDelete.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		buttonDelete.setText("Delete selection");
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override

			public void widgetSelected(SelectionEvent e) {

				if (!tableViewer.getSelection()
						.isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

					// Object firstElement = selection.getFirstElement();
					Iterator itr = selection.iterator();
					while (itr.hasNext()) {
						Object element = itr.next();

						if (element instanceof CachedDataSet) {
							CachedDataSet cds = (CachedDataSet) element;
							try {
								FileUtils.forceDelete(cds.getPathname());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						tableViewer.remove(element);
					}
				}

			}

		});

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof CachedDataSet) {
					CachedDataSet cds = (CachedDataSet) firstElement;
					// define position
					ArrayList<LatLon> surfaceLinePositions = new ArrayList<LatLon>();
					surfaceLinePositions.add(LatLon.fromDegrees(cds.getMaxLat(), cds.getMaxLon()));
					surfaceLinePositions.add(LatLon.fromDegrees(cds.getMaxLat(), cds.getMinLon()));
					surfaceLinePositions.add(LatLon.fromDegrees(cds.getMinLat(), cds.getMinLon()));
					surfaceLinePositions.add(LatLon.fromDegrees(cds.getMinLat(), cds.getMaxLon()));
					surfaceLinePositions.add(LatLon.fromDegrees(cds.getMaxLat(), cds.getMaxLon()));

					// define apparence
					ShapeAttributes attr = new BasicShapeAttributes();
					attr.setOutlineWidth(2.0);
					attr.setOutlineMaterial(Material.YELLOW);
					attr.setInteriorOpacity(0.3);
					attr.setInteriorMaterial(Material.YELLOW);
					attr.setEnableAntialiasing(true);

					// create poly & add to WWJ
					SurfacePolygon poly = new SurfacePolygon(attr, surfaceLinePositions);
					renderableManager.setRenderable(poly);
				}
			}

		});

		// add a filter which will search in the filename field
		tableViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				CachedDataSet cds = (CachedDataSet) element;
				return cds.getFilename()
						.contains(searchString);
			}
		});

		// Set the sorter for the table
		comparator = new CachedDataSetViewerComparator();
		tableViewer.setComparator(comparator);
	}

	private void listCachedLayers() {
		FileStore store = new BasicDataFileStore();
		scanFile(store.getWriteLocation());
	}

	private void scanFile(File dir) {
		if (!dir.isDirectory()) {
			return;
		}

		if (CacheUtil.isSingleDataSet(dir.listFiles())) {
			Job job = new Job("Read Cached Dataset params "+dir.getAbsolutePath()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					synch.asyncExec(new Runnable() {
						@Override
						public void run() {
							CachedDataSet cds = CacheUtil.findDatasetDefinition(dir, evtBroker);
							if (cds != null) {
								cdsList.add(cds);
								tableViewer.setInput(cdsList);
							}
						}
					});
					return Status.OK_STATUS;
				}
			};
			// Start the Job
			job.schedule();
		}

		else {
			for (File sd : dir.listFiles()) {
				scanFile(sd);
			}
		}
	}

	private void createColumns(TableViewer viewer) {

		// create column for name property
		TableViewerColumn col = createTableViewerColumn("Name", 300, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String name = cd.getFilename();
				return name;
			}
		});

		// create column for number of layer levels property
		col = createTableViewerColumn("Levels", 100, 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String num = cd.getNumLevels()
						.toString();
				return num;

			}
		});

		// create column for number of layer levels property
		TableViewerColumn collatsw = new TableViewerColumn(viewer, SWT.NONE);
		collatsw.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String num = cd.getMinLat()
						.toString();
				return num;

			}
		});
		collatsw.getColumn()
				.setWidth(100);
		collatsw.getColumn()
				.setText("MinLat");

		// create column for number of layer levels property
		TableViewerColumn collonsw = new TableViewerColumn(viewer, SWT.NONE);
		collonsw.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String num = cd.getMinLon()
						.toString();
				return num;

			}
		});
		collonsw.getColumn()
				.setWidth(100);
		collonsw.getColumn()
				.setText("MinLon");

		// create column for number of layer levels property
		TableViewerColumn collatne = new TableViewerColumn(viewer, SWT.NONE);
		collatne.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String num = cd.getMaxLat()
						.toString();
				return num;

			}
		});
		collatne.getColumn()
				.setWidth(100);
		collatne.getColumn()
				.setText("MaxLat");

		// create column for number of layer levels property
		TableViewerColumn collonne = new TableViewerColumn(viewer, SWT.NONE);
		collonne.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				String num = cd.getMaxLon()
						.toString();
				return num;

			}
		});
		collonne.getColumn()
				.setWidth(100);
		collonne.getColumn()
				.setText("MaxLon");

		// create column for size of layer
		col = createTableViewerColumn("Size", 100, 2);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				Long num = cd.getDirectorySize();
				if (num == null) {
					return "...";
				}
				Double mb = ((double) num) / (1024 * 1024);
				return String.format("%5.1f %s", mb, "Mb");
			}
		});

		// create column for Last Modified file
		col = createTableViewerColumn("Last Modified", 200, 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedDataSet cd = (CachedDataSet) element; // cast
				long num = cd.getLastModif();
				// String nu = Long.toString(num);

				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(num);// fsds.getLastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm a");
				return sdf.format(cal.getTime());
			}
		});

		/*
		 * // create column for the 'last used' property TableViewerColumn colco
		 * at org.eclipse.jface.viewers.ColumnLabelProvider.update(
		 * ColumnLabelProvider.java :34)lLastModified = new
		 * TableViewerColumn(viewer, SWT.NONE);
		 * colcolLastModified.setLabelProvider(new ColumnLabelProvider() {
		 * 
		 * @Override public String getText(Object element) { FileStoreDataSet
		 * fsds = (FileStoreDataSet) element; GregorianCalendar cal = new
		 * GregorianCalendar(); cal.setTimeInMillis(fsds.getLastModified());
		 * SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm a");
		 * return sdf.format(cal.getTime()); } });
		 * colcolLastModified.getColumn().setWidth(200);
		 * colcolLastModified.getColumn().setText("Last modified");
		 * 
		 * // create column for name property TableViewerColumn colSize = new
		 * TableViewerColumn(viewer, SWT.NONE); colSize.setLabelProvider(new
		 * ColumnLabelProvider() {
		 * 
		 * @Override public String getText(Object element) { FileStoreDataSet
		 * fsds = (FileStoreDataSet) element; Formatter formatter = new
		 * Formatter(); return formatter.format("%5.1f", ((float)
		 * fsds.getSize()) / 1e6).toString(); } });
		 * colSize.getColumn().setWidth(100); colSize.getColumn().setText(
		 * "Size (Mb)");
		 * 
		 * // create column for name property TableViewerColumn colArea = new
		 * TableViewerColumn(viewer, SWT.NONE);
		 * 
		 * colArea.setLabelProvider(new ColumnLabelProvider() {
		 * 
		 * @Override public String getText(Object element) { FileStoreDataSet
		 * fsds = (FileStoreDataSet) element;
		 * 
		 * // public static XPath makeXPath();
		 * 
		 * // Element el = path == null ? context : getElement(context, // path,
		 * xpath); at org.eclipse.jface.viewers.ColumnLabelProvider.update(
		 * ColumnLabelProvider.java :34) // String xpath = //
		 * ".nasa.worldwindx.examples.util.FileStoreDataSet@1deb0979"; // LatLon
		 * sw = getLatLon(element, "SouthWest/LatLon", xpath);
		 * 
		 * // LatLon ne = getLatLon(el, "NorthEast/LatLon", rpath);
		 * 
		 * Formatter formatter = new Formatter(); return
		 * formatter.format("%5.1f", ((float) fsds.getSize()) / 1e6).toString();
		 * } }); colArea.getColumn().setWidth(100); colArea.getColumn().setText(
		 * "Size (Mb)");
		 * 
		 * // create column for area extent included in a 2D polygon defined by
		 * the // points (x,y) // computePolygonAreaFromVertices(Iterable<?
		 * extends Vec4> points)
		 * 
		 */

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				tableViewer.getTable()
						.setSortDirection(dir);
				tableViewer.getTable()
						.setSortColumn(column);
				tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Inject
	@Optional
	void exitingPerspective(@UIEventTopic("updatedCDS") CachedDataSet cds) {
		tableViewer.refresh(cds);
	}
}