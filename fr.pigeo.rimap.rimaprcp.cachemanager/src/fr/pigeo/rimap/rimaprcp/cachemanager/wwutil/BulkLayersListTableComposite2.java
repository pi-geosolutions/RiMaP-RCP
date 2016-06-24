package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.ui.jface.WidgetsFilter;
import fr.pigeo.rimap.rimaprcp.core.ui.views.OrganizeTabPart;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;

public class BulkLayersListTableComposite2 extends Composite {
	@Inject
	WwjInstance wwj;

	@Inject
	IEventBroker eventBroker;
	
	private List<Layer> layersToDownload = new ArrayList();;
	

	private final Image CHECKED = getImage("checked.png");
	private final Image UNCHECKED = getImage("unchecked.png");

	private TableViewer tableViewer;

	public BulkLayersListTableComposite2(Composite parent, int style, WwjInstance wwjInst) {
		super(parent, style);
		this.wwj = wwjInst;

		setLayout(new GridLayout(1, false));

		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(tableColumnLayout);

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// create the columns
		createColumns(tableViewer, tableColumnLayout);
		

		// set the content provider
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(wwj.getLayersList());
		tableViewer.addFilter(new WidgetsFilter(false));

		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnDrawArea = new Button(composite_1, SWT.NONE);
		btnDrawArea.setText("Draw Area");

		Button btnDestinationDir = new Button(composite_1, SWT.NONE);
		btnDestinationDir.setEnabled(false);
		btnDestinationDir.setText("...");

		Button btnDownload = new Button(composite_1, SWT.NONE);
		btnDownload.setEnabled(false);
		btnDownload.setText("Start Download");
	}
	
	protected void createColumns(TableViewer tv, TableColumnLayout tcl) {
		// create a column for the checkbox
		TableViewerColumn visibleCol = new TableViewerColumn(tv, SWT.NONE);
		visibleCol.getColumn().setAlignment(SWT.CENTER);
		visibleCol.getColumn().setText("x");
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
		
		// create a column for the download checkbox
		TableViewerColumn downloadCheckCol = new TableViewerColumn(tv, SWT.NONE);
		downloadCheckCol.getColumn().setAlignment(SWT.CENTER);
		downloadCheckCol.getColumn().setText("Download ?");
		downloadCheckCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				if (layersToDownload.contains((Layer) element)) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});
		
		// create a column for the name
		TableViewerColumn downloadProgressCol = new TableViewerColumn(tv, SWT.NONE);
		downloadProgressCol.getColumn().setAlignment(SWT.CENTER);
		downloadProgressCol.getColumn().setText("Progress");
		downloadProgressCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				return "0%";
			}
		});


		// sets the width for each column
		tcl.setColumnData(visibleCol.getColumn(), new ColumnPixelData(40));
		tcl.setColumnData(nameCol.getColumn(), new ColumnWeightData(20, 200, true));
		tcl.setColumnData(downloadCheckCol.getColumn(), new ColumnPixelData(80));
		tcl.setColumnData(downloadProgressCol.getColumn(), new ColumnPixelData(80));
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
	
	@Override
	public void dispose() {
		CHECKED.dispose();
		UNCHECKED.dispose();
		super.dispose();
	}
}
