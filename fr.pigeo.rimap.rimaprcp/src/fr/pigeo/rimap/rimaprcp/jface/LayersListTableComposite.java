package fr.pigeo.rimap.rimaprcp.jface;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.dnd.LayerDragListener;
import fr.pigeo.rimap.rimaprcp.dnd.LayerDropListener;
import fr.pigeo.rimap.rimaprcp.riskcatalog.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.views.OrganizeTabPart;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class LayersListTableComposite extends Composite {
	private TableViewer viewer;
	private WwjInstance wwj;

	private final Image CHECKED = getImage("checked.png");
	private final Image UNCHECKED = getImage("unchecked.png");
	private final Image FEATUREINFO = getImage("icon_featureinfo_16px.png");
	private final Image METADATA = getImage("icon_metadata_16px.png");
	private final Image PQUERY = getImage("polygon_query_16px.png");

	public LayersListTableComposite(Composite parent, int style, WwjInstance wwjInst) {
		super(parent, style);
		this.wwj = wwjInst;
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		this.setLayout(tableColumnLayout);

		// define the TableViewer
		viewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// create the columns
		createColumns(viewer, tableColumnLayout);

		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(wwj.getLayersList());
		
		// viewer.addFilter(new RimapFilterByKeyFilter("isRimapLayer"));
	}

	private void createColumns(TableViewer tv, TableColumnLayout tcl) {
		// create a column for the checkbox
		TableViewerColumn visibleCol = new TableViewerColumn(tv, SWT.NONE);
		visibleCol.getColumn().setAlignment(SWT.CENTER);
		visibleCol.getColumn().setText("x");
		visibleCol.setEditingSupport(new TableCheckEditingSupport(tv));
		visibleCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (((Layer) element).isEnabled()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		// create a column for the name
		TableViewerColumn nameCol = new TableViewerColumn(tv, SWT.NONE);
		nameCol.getColumn().setText("Layer name");
		nameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Layer l = (Layer) element;
				return l.getName();
			}
		});

		// create a column for the queryable boolean value
		TableViewerColumn qCol = new TableViewerColumn(tv, SWT.NONE);
		// qCol.getColumn().setAlignment(SWT.CENTER);
		qCol.getColumn().setText("i");
		qCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof RimapWMSTiledImageLayer) {
					RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) element;
					if (l.getParent().isQueryable())
						return FEATUREINFO;
				}
				return null;
			}
		});

		// create a column for the polygon_queryable boolean value
		TableViewerColumn pqCol = new TableViewerColumn(tv, SWT.NONE);
		// pqCol.getColumn().setAlignment(SWT.CENTER);
		pqCol.getColumn().setText("pq");
		pqCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof RimapWMSTiledImageLayer) {
					RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) element;
					if (l.getParent().getPq_layer() != null)
						return PQUERY;
				}
				return null;
			}
		});

		// create a column telling if metadata is linked
		TableViewerColumn mtdCol = new TableViewerColumn(tv, SWT.NONE);
		// mtdCol.getColumn().setAlignment(SWT.CENTER);
		mtdCol.getColumn().setText("M");
		mtdCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof RimapWMSTiledImageLayer) {
					RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) element;
					if (l.getParent().getMetadata_uuid() != "")
						return METADATA;
				}
				return null;
			}
		});

		// sets the width for each column
		tcl.setColumnData(visibleCol.getColumn(), new ColumnPixelData(20));
		tcl.setColumnData(nameCol.getColumn(), new ColumnWeightData(20, 200, true));
		tcl.setColumnData(qCol.getColumn(), new ColumnPixelData(20));
		tcl.setColumnData(pqCol.getColumn(), new ColumnPixelData(20));
		tcl.setColumnData(mtdCol.getColumn(), new ColumnPixelData(20));

	}

	/**
	 *  make lines and header visible
	 */
	public void drawTableLines() {
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	/**
	 * @param filter
	 * Filters the TableViewer using the provided filter value
	 */
	public void addWidgetFilter(boolean keep) {
		viewer.addFilter(new WidgetsFilter(keep));
	}

	/**
	 * Enables layers reordering by drag n drop inside the table view
	 */
	public void addDragnDropSupport() {
		int operations = DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(operations, transferTypes, new LayerDragListener(viewer, this.wwj));
		viewer.addDropSupport(operations, transferTypes, new LayerDropListener(viewer, this.wwj));
	}

	// helper method to load the images
	// ensure to dispose the images in your @PreDestroy method
	private static Image getImage(String file) {
		// assume that the current class is called View.java
		Bundle bundle = FrameworkUtil.getBundle(OrganizeTabPart.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	public void refresh() {
		this.viewer.setInput(this.wwj.getLayersList());
		this.viewer.refresh();

	}

	@Override
	public void dispose() {
		CHECKED.dispose();
		UNCHECKED.dispose();
		this.FEATUREINFO.dispose();
		this.METADATA.dispose();
		this.PQUERY.dispose();
		super.dispose();
	}
}
