package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload.BulkDownloadManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheUtil;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadables;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICheckableNode;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.ui.views.OrganizeTabPart;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class BulkDownloadLayersTable extends Composite {
	public TableViewer viewer;

	@Inject
	Downloadables downloadables;

	@Inject
	WwjInstance wwj;

	@Inject
	Logger logger;

	@Inject
	IEclipseContext context;

	@Inject
	MApplication application;

	@Inject
	IEventBroker eventBroker;
	
	@Inject
	@Optional
	UISynchronize synch;

	protected final Image CHECKED = getImage("checked.png");
	protected final Image UNCHECKED = getImage("unchecked.png");
	protected final Image FEATUREINFO = getImage("icon_featureinfo_16px.png");
	protected final Image METADATA = getImage("icon_metadata_16px.png");
	protected final Image PQUERY = getImage("polygon_query_16px.png");
	protected final Image WMSICON = getImage("wms.png");

	public BulkDownloadLayersTable(Composite parent, int style, BulkDownloadManager bulkManager) {
		super(parent, style);

		ContextInjectionFactory.inject(this, bulkManager.getEclipseContext());
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		this.setLayout(tableColumnLayout);

		// define the TableViewer
		viewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.NONE);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// create the columns
		createColumns(viewer, tableColumnLayout);

		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// downloadables = new Downloadables(wwjInst);
		viewer.setInput(downloadables.getList()
				.toArray());

	}

	protected void createColumns(TableViewer tv, TableColumnLayout tcl) {
		// create a column for the checkbox
		TableViewerColumn visibleCol = new TableViewerColumn(tv, SWT.NONE);
		visibleCol.getColumn()
				.setAlignment(SWT.CENTER);
		visibleCol.getColumn()
				.setText("x");
		visibleCol.setEditingSupport(new LayerViewCheckEditingSupport(tv));
		visibleCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (((Downloadable) element).getLayer()
						.isEnabled()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		// create a column for the name
		TableViewerColumn nameCol = new TableViewerColumn(tv, SWT.NONE);
		nameCol.getColumn()
				.setText("Layer name");
		nameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Layer l = ((Downloadable) element).getLayer();
				return l.getName();
			}

			@Override
			public Image getImage(Object element) {
				Layer l = ((Downloadable) element).getLayer();
				if (l instanceof WMSTiledImageLayer) {
					WMSTiledImageLayer tl = (WMSTiledImageLayer) l;
					if (tl.hasKey(RimapAVKey.LAYER_PARENTNODE)) {
						return WMSICON;
					}
				}
				return null;
			}
		});

		// create a column for the download checkbox
		TableViewerColumn col = new TableViewerColumn(tv, SWT.NONE);
		col.getColumn()
				.setAlignment(SWT.CENTER);
		col.getColumn()
				.setText("Download");

		col.setEditingSupport(new LayerDownloadCheckEditingSupport(tv));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (((Downloadable) element).doDownload()) {
					return "download";
				}
				return " ";
			}

			@Override
			public Image getImage(Object element) {
				if (((Downloadable) element).doDownload()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});
		tcl.setColumnData(col.getColumn(), new ColumnPixelData(100));
		/*
		 * // minLevel column
		 * col = new TableViewerColumn(tv, SWT.NONE);
		 * col.getColumn()
		 * .setAlignment(SWT.CENTER);
		 * col.getColumn()
		 * .setText("Min Level");
		 * 
		 * col.setEditingSupport(new LevelEditingSupport(tv, "min"));
		 * col.setLabelProvider(new ColumnLabelProvider() {
		 * 
		 * @Override
		 * public String getText(Object element) {
		 * return ((Downloadable) element).getLevelAsString("min");
		 * }
		 * 
		 * @Override
		 * public Image getImage(Object element) {
		 * return null;
		 * }
		 * });
		 * tcl.setColumnData(col.getColumn(), new ColumnPixelData(90));
		 */
		/*
		 * // maxLevel column
		 * col = new TableViewerColumn(tv, SWT.NONE);
		 * col.getColumn()
		 * .setAlignment(SWT.CENTER);
		 * col.getColumn()
		 * .setText("Max Level");
		 * col.setEditingSupport(new LevelEditingSupport(tv, "max"));
		 * col.setLabelProvider(new ColumnLabelProvider() {
		 * 
		 * @Override
		 * public String getText(Object element) {
		 * return ((Downloadable) element).getLevelAsString("max");
		 * }
		 * 
		 * @Override
		 * public Image getImage(Object element) {
		 * return null;
		 * }
		 * });
		 * tcl.setColumnData(col.getColumn(), new ColumnPixelData(90));
		 */
		// Max resolution column
		col = new TableViewerColumn(tv, SWT.NONE);
		col.getColumn()
				.setAlignment(SWT.CENTER);
		col.getColumn()
				.setText("Max resolution");
		col.setEditingSupport(new ResolutionEditingSupport(tv));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Downloadable d = ((Downloadable) element);
				if (d.doDownload()) {
					double res = d.getMaxResolution();
					return String.format("%.2f m",res);
				} else {
					return "-";
				}
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});
		tcl.setColumnData(col.getColumn(), new ColumnPixelData(150));
		
		// Estimated size column
		col = new TableViewerColumn(tv, SWT.NONE);
		col.getColumn()
				.setAlignment(SWT.CENTER);
		col.getColumn()
				.setText("Est. Size");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Downloadable d = ((Downloadable) element);
				if (d.doDownload()) {
					long size = d.getEstimatedSize();
					if (size == 0) {
						//means it has not yet been computed
						return "-";
					} else {
						return CacheUtil.makeSizeDescription(size);
					}
				} else {
					return "";
				}
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});
		tcl.setColumnData(col.getColumn(), new ColumnPixelData(100));
		
		// Estimated size column
		col = new TableViewerColumn(tv, SWT.NONE);
		col.getColumn()
				.setAlignment(SWT.CENTER);
		col.getColumn()
				.setText("Progress");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Downloadable d = ((Downloadable) element);
				if (d.doDownload()) {
					/*
					rd.getDownloadProgress();
					if (p < 0) {
						//means it has not yet been started
						return "-";
					} else {
						return String.format("%,.1f", p)+" %";
					}*/

					return d.getDownloadProgress();
				} else {
					return "";
				}
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});
		tcl.setColumnData(col.getColumn(), new ColumnPixelData(100));


		// sets the width for each column
		tcl.setColumnData(visibleCol.getColumn(), new ColumnPixelData(20));
		tcl.setColumnData(nameCol.getColumn(), new ColumnWeightData(20, 200, true));

	}

	/**
	 * make lines and header visible
	 */
	public void drawTableLines() {
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	// helper method to load the images
	// ensure to dispose the images in your @PreDestroy method
	protected static Image getImage(String file) {
		// assume that the current class is called View.java
		Bundle bundle = FrameworkUtil.getBundle(OrganizeTabPart.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	public void refresh() {
		if (downloadables != null) {
			this.viewer.setInput(downloadables.getList()
					.toArray());
			this.viewer.refresh();
		}

	}

	@Override
	public void dispose() {
		CHECKED.dispose();
		UNCHECKED.dispose();
		super.dispose();
	}

	public void registerEvents() {
	}

	public IStructuredSelection getDownloadables() {
		return this.viewer.getStructuredSelection();
	}

	@Inject
	@Optional
	void checkHandler(@UIEventTopic(RiMaPEventConstants.CHECKABLENODE_CHECKCHANGE) ICheckableNode node) {
		this.refresh();
	}

	@Inject
	@Optional
	void drawingSector(@UIEventTopic(CacheManagerEventConstants.SECTORSELECTOR_DRAWING) Sector s) {
		downloadables.setSector(null);
		viewer.refresh();
	}
	
	@Inject
	@Optional
	void updateSector(@UIEventTopic(CacheManagerEventConstants.SECTORSELECTOR_FINISHED) Sector s) {
		Job job = new Job("Compute download sizes") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				synch.asyncExec(new Runnable() {
					@Override
					public void run() {
						downloadables.setSector(s);
						viewer.refresh();
					}
				});
				return Status.OK_STATUS;
			}
		};
		// Start the Job
		job.schedule();
	}
	
	@Inject
	@Optional
	void updateProgress(@UIEventTopic(CacheManagerEventConstants.DOWNLOAD_PROGRESS_UPDATE) String s) {
		viewer.refresh();
	}
	
	@Inject
	@Optional
	void reloadTable(@UIEventTopic(CacheManagerEventConstants.BULKDOWNLOAD_TABLE_RELOAD) String s) {
		viewer.setInput(downloadables.getList(true)
				.toArray());
		viewer.refresh();
	}
	
}
