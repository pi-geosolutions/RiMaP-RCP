 
package fr.pigeo.rimap.cachemanager.ui.views;

import javax.inject.Inject;

import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class SWTCacheTable {
	@Inject
	public SWTCacheTable() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		Table table = new Table(parent, SWT.MULTI | SWT.BORDER
		        | SWT.FULL_SELECTION);
		    table.setLinesVisible(true);
		    table.setHeaderVisible(true);
		    String[] titles = { " DataSet", "Last Used", "Size", "Day Old", "Week old",
		        "Month Old", "Year Old" };
		    
		    //String configpath = "/home/msi/var/cache/WorldWindData";
		    FileStore store = new BasicDataFileStore();
		    
			System.out.println(store.getWriteLocation().getPath());
			
		    File cacheRoot = store.getWriteLocation();
		    String rpath = cacheRoot.getPath();
		    System.out.println("rpath" + rpath);
		    
		    
		    for (int i = 0; i < titles.length; i++) {
		      TableColumn column = new TableColumn(table, SWT.NONE);
		      column.setText(titles[i]);
		    }
		    int count = 10;
		    for (int i = 0; i < count; i++) {
		      TableItem item = new TableItem(table, SWT.NONE);
		      item.setText(0, "aaaaaaaaaa");
		      item.setText(1, "bbbbbbbbbb");
		      item.setText(2, "cccccccccc");
		      item.setText(3, "ddddddddddd");
		      item.setText(4, "eeeeeeeeeeeee");
		      item.setText(5, "ffffffffffffff");
		      item.setText(6, "line " + i );
		    }
		    for (int i = 0; i < titles.length; i++) {
		      table.getColumn(i).pack();
		    }
		    table.setSize(table.computeSize(SWT.DEFAULT, 200));
	}
}