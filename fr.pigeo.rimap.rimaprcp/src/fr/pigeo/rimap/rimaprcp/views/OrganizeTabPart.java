 
package fr.pigeo.rimap.rimaprcp.views;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.dnd.LayerDragListener;
import fr.pigeo.rimap.rimaprcp.dnd.LayerDropListener;
import fr.pigeo.rimap.rimaprcp.jface.TableCheckEditingSupport;
import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;

public class OrganizeTabPart {
	private TableViewer viewer;
	private WwjInstance wwj;
	// fields for your class
	// assumes that you have these two icons
	// in the "icons" folder
	private final Image CHECKED = getImage("checked.png");
	private final Image UNCHECKED = getImage("unchecked.png");
	private final Image FEATUREINFO = getImage("icon_featureinfo_16px.png");
	private final Image METADATA = getImage("icon_metadata_16px.png");
	private final Image PQUERY = getImage("polygon_query_16px.png");
	
	@Inject
	public OrganizeTabPart() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst) {
		this.wwj = wwjInst;
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		// define the TableViewer
		viewer = new TableViewer(tableComposite, SWT.MULTI | SWT.H_SCROLL
		      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// create the columns 
		createColumns(viewer, tableColumnLayout);

		// make lines and header visible
		/*final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		*/
	    int operations = DND.DROP_MOVE;
	    Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
	    viewer.addDragSupport(operations, transferTypes , new LayerDragListener(viewer, wwjInst));
	    viewer.addDropSupport(operations, transferTypes, new LayerDropListener(viewer, wwjInst));
		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// provide the input to the viewer
		// setInput() calls getElements() on the 
		// content provider instance
		viewer.setInput(wwj.getLayersList()); 
		//viewer.addFilter(new RimapFilterByKeyFilter("isRimapLayer"));
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
		  return null;  // no string representation, we only want to display the image
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
		//qCol.getColumn().setAlignment(SWT.CENTER);
		qCol.getColumn().setText("i");
		qCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			  return null;  // no string representation, we only want to display the image
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
		//pqCol.getColumn().setAlignment(SWT.CENTER);
		pqCol.getColumn().setText("pq");
		pqCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			  return null;  // no string representation, we only want to display the image
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof RimapWMSTiledImageLayer) {
					RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) element;
				    if (l.getParent().getPq_layer()!=null)
				    	return PQUERY;
			  }
			  return null;
			  }
			}); 
		
		// create a column telling if metadata is linked
		TableViewerColumn mtdCol = new TableViewerColumn(tv, SWT.NONE);
		//mtdCol.getColumn().setAlignment(SWT.CENTER);
		mtdCol.getColumn().setText("M");
		mtdCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			  return null;  // no string representation, we only want to display the image
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof RimapWMSTiledImageLayer) {
					RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) element;
				    if (l.getParent().getMetadata_uuid()!="")
				    	return METADATA;
			  }
			  return null;
			  }
			}); 

		//sets the width for each column
		tcl.setColumnData(visibleCol.getColumn(),  new ColumnPixelData(20));
		tcl.setColumnData(nameCol.getColumn(),  new ColumnWeightData(20, 200, true));
		tcl.setColumnData(qCol.getColumn(),  new ColumnPixelData(20));
		tcl.setColumnData(pqCol.getColumn(),  new ColumnPixelData(20));
		tcl.setColumnData(mtdCol.getColumn(),  new ColumnPixelData(20));
		

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
	
	@Inject
	@Optional
	private void subscribeEventLayerChecked(
			@UIEventTopic(RimaprcpConstants.LAYER_CHECKED) AbstractLayer layer) {
		viewer.setInput(wwj.getModel().getLayers().toArray(new Layer[0])); 
		this.viewer.refresh();
		
	}
	
	@Inject
	@Optional
	private void subscribeEventLayerDropped(
			@UIEventTopic("dropped_layer") int index) {
		System.out.println("dropped at index "+index);;
		
	}

	
	@PreDestroy
	private void dispose() {
		CHECKED.dispose();
		UNCHECKED.dispose();
		this.FEATUREINFO.dispose();
		this.METADATA.dispose();
		this.PQUERY.dispose();
	}
	
	
	
}