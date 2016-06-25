
package fr.pigeo.rimap.rimaprcp.cachemanager.ui.views;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheDataSet;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.XMLFileImport;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class CacheTable {
	private TableViewer tableViewer;

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		// String configpath = "/home/msi/var/cache/WorldWindData";
		FileStore store = new BasicDataFileStore();

		// System.out.println(store.getWriteLocation().getPath());

		File cacheRoot = store.getWriteLocation();
		String rpath = cacheRoot.getPath();
		// System.out.println("rpath " + rpath);

		// Liste des fichiers XML disponibles sous le répertoire de cache

		XMLFileImport xfi = new XMLFileImport();
		List<CacheDataSet> list = xfi.listXML();

		// define the TableViewer
		tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// create the columns
		// not yet implemented
		createColumns(tableViewer);

		// make lines and header visiblenext
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// this code is placed after the definition of
		// the viewer

		// set the content provider
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		tableViewer.setInput(list.toArray());
		// tableViewer.setInput(FileStoreDataSet.getDataSets(cacheRoot).toArray());

		
		// Delete Layer button
		Button buttonDelete = new Button(parent, SWT.PUSH);

		buttonDelete.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));

		buttonDelete.setText("Delete selection");

		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override

			public void widgetSelected(SelectionEvent e) {

				if (!tableViewer.getSelection().isEmpty()) {

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

					//Object firstElement = selection.getFirstElement();
					Iterator itr = selection.iterator();
				      while(itr.hasNext()) {
				         Object element = itr.next();
				         
				         	if (element instanceof CacheDataSet) {
				         		CacheDataSet cds = (CacheDataSet) element;
				         		System.out.println("TODO: delete "+cds.getPathname());
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

		/*
		 * 
		 * tableViewer.addSelectionChangedListener(new
		 * ISelectionChangedListener() {
		 * 
		 * @Override public void selectionChanged(SelectionChangedEvent event) {
		 * IStructuredSelection selection = (IStructuredSelection)
		 * tableViewer.getSelection(); Object firstElement =
		 * selection.getFirstElement(); System.out.println("element  " +
		 * firstElement); // do something with it } });
		 * 
		 * 
		 * 
		 * 
		 * Button button = new Button(parent, SWT.PUSH);
		 * button.addSelectionListener(new SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) { // update
		 * the table content, whenever the button is pressed //
		 * viewer.setInput(todoService.getTodos());
		 * System.out.println("Called!"); //File homepath = new
		 * File(dir.getParent()); //long size = FileUtils.forceDelete(arg0);;
		 * //tableViewer.setInput(list.toArray()); } }); button.setText(
		 * "Delete Layer");
		 */
	}

	private void createColumns(TableViewer viewer) {

		// create column for name property
		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String name = cd.getFilename();
				return name;
				// System.out.println("objet " + obj);
			}
		});
		colName.getColumn().setWidth(300);
		colName.getColumn().setText("Name");

		// create column for number of layer levels property
		TableViewerColumn colNumLevels = new TableViewerColumn(viewer, SWT.NONE);
		colNumLevels.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String num = cd.getNumLevels();
				// String nu = Integer.toString(num);
				return num;

			}
		});
		colNumLevels.getColumn().setWidth(100);
		colNumLevels.getColumn().setText("Levels");

		// create column for number of layer levels property
		TableViewerColumn collatsw = new TableViewerColumn(viewer, SWT.NONE);
		collatsw.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String num = cd.getLatSW();
				// String latlontext = num.toString();
				return num;

			}
		});
		collatsw.getColumn().setWidth(100);
		collatsw.getColumn().setText("SW Lat");

		// create column for number of layer levels property
		TableViewerColumn collonsw = new TableViewerColumn(viewer, SWT.NONE);
		collonsw.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String num = cd.getLonSW();
				// String latlontext = num.toString();
				return num;

			}
		});
		collonsw.getColumn().setWidth(100);
		collonsw.getColumn().setText("SW Lon");

		// create column for number of layer levels property
		TableViewerColumn collatne = new TableViewerColumn(viewer, SWT.NONE);
		collatne.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String num = cd.getLatNE();
				// String latlontext = num.toString();
				return num;

			}
		});
		collatne.getColumn().setWidth(100);
		collatne.getColumn().setText("NE Lat");

		// create column for number of layer levels property
		TableViewerColumn collonne = new TableViewerColumn(viewer, SWT.NONE);
		collonne.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				String num = cd.getLonNE();
				// String latlontext = num.toString();
				return num;

			}
		});
		collonne.getColumn().setWidth(100);
		collonne.getColumn().setText("NE Lon");

		// create column for size of layer
		TableViewerColumn colsize = new TableViewerColumn(viewer, SWT.NONE);
		colsize.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				long num = cd.directorySize();
				Formatter formatter = new Formatter();
				Float mu = (float) num;
				return formatter.format("%5.1f", ((float) mu) / (1e6)).toString();

			}
		});
		colsize.getColumn().setWidth(100);
		colsize.getColumn().setText("Size Mo");

		// create column for Last Modified file
		TableViewerColumn collast = new TableViewerColumn(viewer, SWT.NONE);
		collast.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CacheDataSet cd = (CacheDataSet) element; // cast
				long num = cd.getLastModif();
				// String nu = Long.toString(num);

				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(num);// fsds.getLastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm a");
				return sdf.format(cal.getTime());

				// return nu;

			}
		});
		collast.getColumn().setWidth(200);
		collast.getColumn().setText("Last Modified");

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

}