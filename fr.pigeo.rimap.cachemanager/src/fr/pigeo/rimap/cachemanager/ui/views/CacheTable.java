
package fr.pigeo.rimap.cachemanager.ui.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwindx.examples.util.FileStoreDataSet;

public class CacheTable {
	private TableViewer tableViewer;

	@Inject
	public CacheTable() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
	    parent.setLayout(new GridLayout(1, false));
		    String[] titles = { " DataSet", "Last Used", "Size", "Day Old", "Week old",
		        "Month Old", "Year Old" };
		    
		    //String configpath = "/home/msi/var/cache/WorldWindData";
		    FileStore store = new BasicDataFileStore();
		    
			System.out.println(store.getWriteLocation().getPath());
			
		    File cacheRoot = store.getWriteLocation();
		    String rpath = cacheRoot.getPath();
		    System.out.println("rpath" + rpath);
		    
		 // define the TableViewer
		    tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
		          | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		    // create the columns 
		    // not yet implemented
		    createColumns(tableViewer);

		    // make lines and header visible
		    final Table table = tableViewer.getTable();
		    table.setHeaderVisible(true);
		    table.setLinesVisible(true); 
		    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    
		 // this code is placed after the definition of 
		 // the viewer

		 // set the content provider
		    tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		 // provide the input to the viewer
		 // setInput() calls getElements() on the 
		 // content provider instance
		    tableViewer.setInput( FileStoreDataSet.getDataSets(cacheRoot).toArray()); 
	}

	private void createColumns(TableViewer viewer) {
	    // create column for name property
	    TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
	    colName.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FileStoreDataSet fsds = (FileStoreDataSet) element;
		        return fsds.getName();
	      }
	    });
	    colName.getColumn().setWidth(300);
	    colName.getColumn().setText("Name");
	    

		// create column for the 'last used' property
	    TableViewerColumn colcolLastModified = new TableViewerColumn(viewer, SWT.NONE);
	    colcolLastModified.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FileStoreDataSet fsds = (FileStoreDataSet) element;
	    	  GregorianCalendar cal = new GregorianCalendar();
              cal.setTimeInMillis(fsds.getLastModified());
              SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm a");
              return sdf.format(cal.getTime());
	      }
	    });
	    colcolLastModified.getColumn().setWidth(200);
	    colcolLastModified.getColumn().setText("Last modified");


	    // create column for name property
	    TableViewerColumn colSize = new TableViewerColumn(viewer, SWT.NONE);
	    colSize.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FileStoreDataSet fsds = (FileStoreDataSet) element;
	    	  Formatter formatter = new Formatter();
              return formatter.format("%5.1f", ((float) fsds.getSize()) / 1e6).toString();
	      }
	    });
	    colSize.getColumn().setWidth(100);
	    colSize.getColumn().setText("Size (Mb)");

	}

}