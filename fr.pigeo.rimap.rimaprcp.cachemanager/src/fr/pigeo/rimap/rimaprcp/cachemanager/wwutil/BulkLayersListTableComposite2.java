package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

//import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.BulkLayersListTableComposite.BulkRetrievablePanel;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.SWTSectorSelector;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.ui.jface.TableCheckEditingSupport;
import fr.pigeo.rimap.rimaprcp.core.ui.jface.WidgetsFilter;
import fr.pigeo.rimap.rimaprcp.core.ui.views.OrganizeTabPart;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.event.BulkRetrievalEvent;
import gov.nasa.worldwind.event.BulkRetrievalListener;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.retrieve.BulkRetrievable;
import gov.nasa.worldwind.retrieve.BulkRetrievalThread;
//import gov.nasa.worldwind.retrieve.Progress;
//import gov.nasa.worldwindx.examples.BulkDownloadPanel;
//import gov.nasa.worldwindx.examples.BulkDownloadPanel.BulkRetrievablePanel;
//import gov.nasa.worldwindx.examples.BulkDownloadPanel.DownloadMonitorPanel;
//import gov.nasa.worldwindx.examples.util.SectorSelector;
//import gov.nasa.worldwindx.examples.util.SectorSelector;
//import gov.nasa.worldwindx.examples.BulkDownloadPanel.BulkRetrievablePanel;

public class BulkLayersListTableComposite2 extends Composite {
	@Inject
	WwjInstance wwj;

	@Inject
	IEventBroker eventBroker;

	protected List<Layer> layersToDownload = new ArrayList();
	protected List<Boolean> layersToDwnld =new ArrayList();

	private final Image CHECKED = getImage("checked.png");
	private final Image UNCHECKED = getImage("unchecked.png");
	private boolean doDownload = false;
	private static boolean clickSelect = false;
	private static boolean moveGraphActive = false;
	private static boolean visibleColCheckBox = false;
	private static Point moveStartPos = new Point(0, 0);
	private static Point startOrigin;
	private boolean enabled;
	private MouseAdapter clickListener;

	private TableViewer tableViewer;

	protected SWTSectorSelector selector;
	protected Sector currentSector;
	protected WorldWindowGLCanvas wwd;
	protected CLabel sectorLabel;
	protected ArrayList<BulkRetrievablePanel> retrievables;
	//protected ArrayList<BulkRetrievablePanel> retrievables=new ArrayList<BulkRetrievablePanel>();

	private boolean dirCache = false;
	private Shell shell = new Shell();

	private static boolean wait = false;

	private static java.awt.Cursor cursor = null;
	protected Button btnDrawArea;
	protected Button btnDownload;
	protected Composite composite_1;
	protected TableViewerColumn downloadProgressCol;
	protected BasicDataFileStore cache;
	protected long size;
	
	

	public BulkLayersListTableComposite2(Composite parent, int style, WwjInstance wwjInst) {
		super(parent, style);
		this.wwj = wwjInst;
		//final WorldWindowGLCanvas wwd = wwj.getWwd();
		wwd = wwj.getWwd();
		selector = new SWTSectorSelector(wwd){}; 
		
		// Init retievable list
      
		this.retrievables = new ArrayList<BulkRetrievablePanel>();
		Layer[] wwjlayers = this.wwj.getLayersList();
		for (Layer layerwwj : wwjlayers) {
        
            if (layerwwj instanceof BulkRetrievable){
               this.retrievables.add(new BulkRetrievablePanel((BulkRetrievable) layerwwj));
            }
		}
		

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

		final Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));

		///////////////////////
		Button btnDrawArea = new Button(composite_1, SWT.NONE);
		btnDrawArea.setText("Draw Area");
		btnDrawArea.setToolTipText("Press Draw Area then press and drag left button of your mouse on globe");
		btnDrawArea.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				

				if (wwj != null) {

					System.out.println("value of click  " + clickSelect);
					clickSelect = true;

					System.out.println("value of click  " + clickSelect);

					if (clickSelect) {
						
						
						
						selectButtonActionPerformed(e);
						
						clickSelect = false;

					}
					return;
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		//////////////////////

		Button btnDestinationDir = new Button(composite_1, SWT.PUSH);
		btnDestinationDir.setEnabled(true);
		btnDestinationDir.setText("...");
		btnDestinationDir.setToolTipText("Press this button to select the file where to store the data cache");
		btnDestinationDir.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				DirectoryDialog dlg = new DirectoryDialog(shell);
				dlg.setText("Select a directory to deposit data cache download");
				dlg.setMessage("Select a directory to deposit data cache download");
				dlg.setFilterPath("C:/");

				String dir_cache = dlg.open();
				System.out.println(dir_cache);
				dirCache = true;

				// DirFileSelector diro = new DirFileSelector();
				// System.out.println(diro);
			}

		});

		////////////////////////
		Button btnDownload = new Button(composite_1, SWT.NONE);
		btnDownload.setEnabled(true);
		btnDownload.setText("Start Download");
		btnDownload.setToolTipText("Select in the list above the data in checking 'Download' then press Start Download");
		btnDownload.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				startButtonActionPerformed(event);
			}
		});

	}
	

	protected void createColumns(TableViewer tv, TableColumnLayout tcl) {
		// create a column for the checkbox
		// TableViewerColumn visibleCol = new TableViewerColumn(tv, SWT.NONE);
		final TableViewerColumn visibleCol = new TableViewerColumn(tv, SWT.CHECK);
		visibleCol.getColumn().setAlignment(SWT.CENTER);
		visibleCol.getColumn().setText("x");
		visibleCol.setEditingSupport(new TableCheckEditingSupport(tv));
		visibleCol.setLabelProvider(new ColumnLabelProvider() {
			int incr=0;
			@Override
			public String getText(Object element) {
				return null; // no string representation, we only want to
								// display the image
			}

			@Override
			public Image getImage(Object element) {
				
				if (((Layer) element).isEnabled()) {
					
					//layersToDownload.add(((Layer) element));
					layersToDwnld.add(incr, true);
					incr=incr+1;
					//System.out.println("test sur passage CheckBox");
					visibleColCheckBox = true;
					return CHECKED;
					
				}
				else {
				layersToDwnld.add(incr, false);
				incr=incr+1;
				//System.out.println("test sur passage UnCheckBox");
				visibleColCheckBox = false;
				return UNCHECKED;
				}
			}
		});
		
		
		//System.out.println(layersToDwnld.size());
		//System.out.println(layersToDwnld.indexOf(true));
		// System.out.println(layersToDwnld.toString());

		// create a column for the name
		// TableViewerColumn nameCol = new TableViewerColumn(tv, SWT.NONE);
		TableViewerColumn nameCol = new TableViewerColumn(tv, SWT.FULL_SELECTION);
		nameCol.getColumn().setText("Layer name");
		nameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Layer l = (Layer) element;
				return l.getName();
			}
			/*
			 * @Override public Image getImage(Object element) { if (element
			 * instanceof WMSTiledImageLayer) { WMSTiledImageLayer l =
			 * (WMSTiledImageLayer) element; if
			 * (l.hasKey(RimapAVKey.LAYER_PARENTNODE)) { return WMSICON; } }
			 * return null; }
			 */
		});

		// create a column for the download checkbox
		// TableViewerColumn downloadCheckCol = new TableViewerColumn(tv,
		// SWT.NONE);
		TableViewerColumn downloadCheckCol = new TableViewerColumn(tv, SWT.CHECK);
		downloadCheckCol.getColumn().setAlignment(SWT.CENTER);
		downloadCheckCol.getColumn().setText("Download ?");
		downloadCheckCol.setEditingSupport(new TableCheckEditingSupport(tv));
		downloadCheckCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {

				return null; // no string representation, we only want to
								// display the image

			}

			@Override
			public Image getImage(Object element) {
				if (((Layer) element).isEnabled()) {
				// if (layersToDownload.contains((Layer) element)) {
				//if (visibleCol.equals(element)) {	
					//System.out.println("test sur passage DoDownload");
					doDownload = true;
					return CHECKED;
				}
				//System.out.println("test sur passage UnDoDownload");
				doDownload = false;
				return UNCHECKED;
			}
		});

		// create a column for the download progress
		// TableViewerColumn downloadProgressCol = new TableViewerColumn(tv,
		// SWT.NONE);
		TableViewerColumn downloadProgressCol = new TableViewerColumn(tv, SWT.FULL_SELECTION);
		downloadProgressCol.getColumn().setAlignment(SWT.CENTER);
		downloadProgressCol.getColumn().setText("Progress");
		downloadProgressCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (doDownload){
				return "10%";
				}
				else
					return "0%";
				
			}
		});
		
		
		// create a column for the size of the map
				// TableViewerColumn downloadProgressCol = new TableViewerColumn(tv,
				// SWT.NONE);
				TableViewerColumn sizeDownload = new TableViewerColumn(tv, SWT.FULL_SELECTION);
				sizeDownload.getColumn().setAlignment(SWT.CENTER);
				sizeDownload.getColumn().setText("Size");
				sizeDownload.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(Object element) {
						return BulkLayersListTableComposite2.makeSizeDescription(size);
						//return "0 Mb";
					}
				});

		// sets the width for each column
		tcl.setColumnData(visibleCol.getColumn(), new ColumnPixelData(40));
		tcl.setColumnData(nameCol.getColumn(), new ColumnWeightData(20, 200, true));
		tcl.setColumnData(downloadCheckCol.getColumn(), new ColumnPixelData(80));
		tcl.setColumnData(sizeDownload.getColumn(), new ColumnPixelData(80));
		tcl.setColumnData(downloadProgressCol.getColumn(), new ColumnPixelData(80));
	

	
	
	
	
	
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	
	
	
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		// logger.info("FeatureInfo button status is " + enabled);

		if (this.enabled) {
			if (clickListener == null) { // lazy init
				clickListener = buildMouseListener();
			}
			this.wwj.getWwd().getInputHandler().addMouseListener(clickListener);
			// logger.debug("added listener");
		} else {
			if (clickListener != null) {
				this.wwj.getWwd().getInputHandler().removeMouseListener(clickListener);
				// logger.debug("removed listener");
			}
		}
	}

	private MouseAdapter buildMouseListener() {
		// final java.awt.Point pt ;//= new Point();
		MouseAdapter ma = new MouseAdapter() {
					
			
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// PositionListener(wwd);
					//System.out.println("test position2");
					
					updateSector();
					// System.out.println(wwd.getCurrentPosition().toString());

					 //java.awt.Point pt = e.getLocationOnScreen();
					 //System.out.println("xpt,ypt " + pt.toString() );

					moveStartPos.x = e.getXOnScreen();
					moveStartPos.y = e.getYOnScreen();
					System.out.println("x,y " + moveStartPos.x + "   " + moveStartPos.y);
					// moveStartPos.y = e.MOUSE_CLICKED;
					// System.out.println("y " + moveStartPos.y);
					// getFeatureInfo();

				}
			}
			

		};
		return ma;
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

	/**
	 * @param filter
	 *            Filters the TableViewer using the provided filter value
	 */
	public void addWidgetFilter(boolean keep) {
		tableViewer.addFilter(new WidgetsFilter(keep));
	}

	@Override
	public void dispose() {
		CHECKED.dispose();
		UNCHECKED.dispose();
		super.dispose();
	}

	public void registerEvents() {
		if (eventBroker == null) {
			System.out.println("ERROR: EventBroker is Null");
			return;
		}
		this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = tableViewer.getStructuredSelection();
				Object item = selection.getFirstElement();
				if (item instanceof Layer) {
					eventBroker.post(RiMaPEventConstants.LAYER_SELECTED, (Layer) item);
				}

			}
		});
	}

	public IStructuredSelection getSelectedLayers() {
		return this.tableViewer.getStructuredSelection();
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////SWTSEctorSelection/////////////////////////////////////////////////////////
	
	
	
	
	
	
	 protected void updateSector()
	    {
		
		 this.currentSector = this.selector.getSector();
	        if (this.currentSector != null)
	        {
	            // Update sector description
	        	System.out.println("test curent sector");
	        	System.out.println("current sector" + makeSectorDescription(this.currentSector));
	            //this.sectorLabel.setText(makeSectorDescription(this.currentSector));
	            //this.btnDrawArea.setText("Clear Area");
	            //this.selectButton.setText("Clear sector");
	            //this.btnDownload.setEnabled(true);
	            
	        }
	        else
	        {
	            // null sector
	            //this.sectorLabel.setText("-");
	            //this.selectButton.setText("Select sector");
	        	//this.btnDrawArea.setText("Draw Area");
	        	//this.btnDownload.setEnabled(false);
	        
	        }
	        System.out.println("test updateRetrievablePanels");
	        updateRetrievablePanels(this.currentSector);
	    }

	    protected void updateRetrievablePanels(Sector sector)
	    {
	        for (BulkRetrievablePanel panel : this.retrievables)
	        {
	            panel.updateDescription(sector);
	        }
	    }

	   // @SuppressWarnings( {"UnusedDeclaration"})
	    protected void selectButtonActionPerformed(SelectionEvent e)
	    {
	        if (this.selector.getSector() != null)
	        {
	            this.selector.disable();
	        }
	        else
	        {
	            this.selector.enable();
	        }
	        System.out.println("test updateSector");
	        updateSector();
	    }

	    /** Clear the current selection sector and remove it from the globe. */
	    public void clearSector()
	    {
	        if (this.selector.getSector() != null)
	        {
	            this.selector.disable();
	        }
	        updateSector();
	    }

	    //@SuppressWarnings( {"UnusedDeclaration"})
	    protected void startButtonActionPerformed(Event event)
	    {
	    	int incr=0;
	        for (BulkRetrievablePanel panel : this.retrievables)
	        	
	        {
	        	
	            if (doDownload) //(layersToDwnld(incr, Class<Boolean>)==true) //(doDownload) //downloadCheckBox)
	            {
	                BulkRetrievable retrievable = panel.retrievable;
	                BulkRetrievalThread thread = retrievable.makeLocal(this.currentSector, 0, this.cache, new BulkRetrievalListener()
	                    {
	                        public void eventOccurred(BulkRetrievalEvent event)
	                        {
	                            // This is how you'd include a retrieval listener. Uncomment below to monitor downloads.
	                            // Be aware that the method is not invoked on the event dispatch thread, so any interaction
	                            // with AWT or Swing must be within a SwingUtilities.invokeLater() runnable.

	                            //System.out.printf("%s: item %s\n",
	                            //    event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_SUCCEEDED) ? "Succeeded"
	                            //: event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_FAILED) ? "Failed"
	                            //    : "Unknown event type", event.getItem());
	                        }
	                    });

	                if (thread != null)
	                    //this.monitorPanel.add(new DownloadMonitorPanel(thread));
	                	//this.composite_1.
	                	
	                	downloadProgressCol.setLabelProvider(new ColumnLabelProvider() {
	            			@Override
	            			public String getText(Object element) {

	            				
	    	                    		return BulkLayersListTableComposite2.makeSizeDescription(size); //return only size but not the percent of download
	    	                    
	            			}
	            		});
	            }
	        }
	       // this.getTopLevelAncestor().validate();
	    }
	    
	    
	    
	    
/*
	    
	    
	     //Determines whether there are any active downloads running.
	     
	     // @return <code>true</code> if at leat one download thread is active.
	     
	    public boolean hasActiveDownloads()
	    {
	        for (Component c : this.monitorPanel.getComponents())
	        {
	            if (c instanceof DownloadMonitorPanel)
	                if (((DownloadMonitorPanel) c).thread.isAlive())
	                    return true;
	        }
	        return false;
	    }

	    // Cancel all active downloads. 
	    public void cancelActiveDownloads()
	    {
	        for (Component c : this.monitorPanel.getComponents())
	        {
	            if (c instanceof DownloadMonitorPanel)
	            {
	                if (((DownloadMonitorPanel) c).thread.isAlive())
	                {
	                    DownloadMonitorPanel panel = (DownloadMonitorPanel) c;
	                    panel.cancelButtonActionPerformed(null);
	                    try
	                    {
	                        // Wait for thread to die before moving on
	                        long t0 = System.currentTimeMillis();
	                        while (panel.thread.isAlive() && System.currentTimeMillis() - t0 < 500)
	                        {
	                            Thread.sleep(10);
	                        }
	                    }
	                    catch (Exception ignore)
	                    {
	                    }
	                }
	            }
	        }
	    }

	    // Remove inactive downloads from the monitor panel. 
	    public void clearInactiveDownloads()
	    {
	        for (int i = this.monitorPanel.getComponentCount() - 1; i >= 0; i--)
	        {
	            Component c = this.monitorPanel.getComponents()[i];
	            if (c instanceof DownloadMonitorPanel)
	            {
	                DownloadMonitorPanel panel = (DownloadMonitorPanel) c;
	                if (!panel.thread.isAlive() || panel.thread.isInterrupted())
	                {
	                    this.monitorPanel.remove(i);
	                }
	            }
	        }
	        this.monitorPanel.validate();
	    }

	    protected void initComponents()
	    {
	        int border = 6;
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	        this.setBorder(
	            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Download")));
	        this.setToolTipText("Layer imagery bulk download.");

	        final JPanel locationPanel = new JPanel(new BorderLayout(5, 5));
	        JLabel locationLabel = new JLabel(" Cache:");
	        final JLabel locationName = new JLabel("");
	        JButton locationButton = new JButton("...");
	        locationPanel.add(locationLabel, BorderLayout.WEST);
	        locationPanel.add(locationName, BorderLayout.CENTER);
	        locationPanel.add(locationButton, BorderLayout.EAST);
	        this.add(locationPanel);

	        locationButton.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	                JFileChooser fc = new JFileChooser();
	                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                fc.setMultiSelectionEnabled(false);
	                int status = fc.showOpenDialog(locationPanel);
	                if (status == JFileChooser.APPROVE_OPTION)
	                {
	                    File file = fc.getSelectedFile();
	                    if (file != null)
	                    {
	                        locationName.setText(file.getPath());
	                        cache = new BasicDataFileStore(file);
	                        updateRetrievablePanels(selector.getSector());
	                    }
	                }
	            }
	        });

	        // Select sector button
	        JPanel sectorPanel = new JPanel(new GridLayout(0, 1, 0, 0));
	        sectorPanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
	        selectButton = new JButton("Select sector");
	        selectButton.setToolTipText("Press Select then press and drag button 1 on globe");
	        selectButton.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent event)
	            {
	                selectButtonActionPerformed(event);
	            }
	        });
	        sectorPanel.add(selectButton);
	        sectorLabel = new JLabel("-");
	        sectorLabel.setPreferredSize(new Dimension(350, 16));
	        sectorLabel.setHorizontalAlignment(JLabel.CENTER);
	        sectorPanel.add(sectorLabel);
	        this.add(sectorPanel);

	        // Retrievable list combo and start button
	        JPanel retrievablesPanel = new JPanel();
	        retrievablesPanel.setLayout(new BoxLayout(retrievablesPanel, BoxLayout.Y_AXIS));
	        retrievablesPanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));

	        // RetrievablePanel list
	        for (JPanel panel : this.retrievables)
	        {
	            retrievablesPanel.add(panel);
	        }
	        this.add(retrievablesPanel);

	        // Start button
	        JPanel startPanel = new JPanel(new GridLayout(0, 1, 0, 0));
	        startPanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
	        startButton = new JButton("Start download");
	        startButton.setEnabled(false);
	        startButton.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent event)
	            {
	                startButtonActionPerformed(event);
	            }
	        });
	        startPanel.add(startButton);
	        this.add(startPanel);

	        // Download monitor panel
	        monitorPanel = new JPanel();
	        monitorPanel.setLayout(new BoxLayout(monitorPanel, BoxLayout.Y_AXIS));
	        monitorPanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
	        //this.add(monitorPanel);

	        // Put the monitor panel in a scroll pane.
	        JPanel dummyPanel = new JPanel(new BorderLayout());
	        dummyPanel.add(monitorPanel, BorderLayout.NORTH);

	        JScrollPane scrollPane = new JScrollPane(dummyPanel);
	        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        scrollPane.setPreferredSize(new Dimension(350, 200));
	        this.add(scrollPane);
	    }

	    
	    */
	    
	    
	    public static String makeSectorDescription(Sector sector)
	    {
	        return String.format("S %7.4f\u00B0 W %7.4f\u00B0 N %7.4f\u00B0 E %7.4f\u00B0",
	            sector.getMinLatitude().degrees,
	            sector.getMinLongitude().degrees,
	            sector.getMaxLatitude().degrees,
	            sector.getMaxLongitude().degrees);
	    }

	    public static String makeSizeDescription(long size)
	    {
	        double sizeInMegaBytes = size / 1024 / 1024;
	        if (sizeInMegaBytes < 1024)
	            return String.format("%,.1f MB", sizeInMegaBytes);
	        else if (sizeInMegaBytes < 1024 * 1024)
	            return String.format("%,.1f GB", sizeInMegaBytes / 1024);
	        return String.format("%,.1f TB", sizeInMegaBytes / 1024 / 1024);
	    }

	    public class BulkRetrievablePanel extends Canvas
	    {
	        protected BulkRetrievable retrievable;
	        //protected JCheckBox selectCheckBox;
	       
	        protected CLabel descriptionLabel;
	        protected Thread updateThread;
	        protected Sector sector;

	        BulkRetrievablePanel(BulkRetrievable retrievable)
	        {
	            this.retrievable = retrievable;

	            this.initComponents();
	        }

	        protected void initComponents()
	        {
	            //this.setLayout(new BorderLayout());
	            //this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

	            // Check + name
	            //this.selectCheckBox = new JCheckBox(this.retrievable.getName());
	            //this.selectCheckBox.addActionListener(new ActionListener()
	            //{
	        	
	        	updateRetrievablePanels(selector.getSector());
	        	
	              //  public void actionPerformed(ActionEvent e)
	             //   {
	                    //if (((JCheckBox) e.getSource()).isSelected() && sector != null)
	                	//if (downloadCheckBox && sector != null)
	                	if (sector != null){
	                		System.out.println("-----------------updateDescription----------------");
	                        updateDescription(sector);
	                	}
	             //   }
	            //});
	            //this.add(this.selectCheckBox, BorderLayout.WEST);
	            // Description (size...)
	           //CLabel descriptionlabel = new CLabel(composite_1, SWT.BORDER);
	                        
	           //this.descriptionLabel = new CLabel(composite_1, SWT.BORDER);
	            //this.add(this.descriptionLabel, BorderLayout.EAST);
	        }

	        public void updateDescription(final Sector sector)
	        {
	            if (this.updateThread != null && this.updateThread.isAlive())
	                return;
	            //System.out.println("test");
	            this.sector = sector;
	            //if (!this.selectCheckBox.isSelected())
	            if (!visibleColCheckBox) //downloadCheckBox)
	            {
	            	//System.out.println("test");
	                doUpdateDescription(null);
	                return;
	            }

	            this.updateThread = new Thread(new Runnable()
	            {
	                public void run()
	                {
	                	System.out.println("test updateDescription");
	                    doUpdateDescription(sector);
	                }
	            });
	            this.updateThread.setDaemon(true);
	            this.updateThread.start();
	            //System.out.println("test");
	        }

	        protected void doUpdateDescription(final Sector sector)
	        {
	            if (sector != null)
	            {
	            	System.out.println("sector not null");
	                try
	                {
	                    long size = retrievable.getEstimatedMissingDataSize(sector, 0, cache);
	                    final String formattedSize = BulkLayersListTableComposite2.makeSizeDescription(size);
	                    System.out.println("formattedSize  " + formattedSize);
	                    //System.out.println("test");
	                    SwingUtilities.invokeLater(new Runnable()
	                    
	                    {
	                        public void run()
	                        {
	                        	System.out.println("formattedSize  " + formattedSize);
	                        	
	                          //  descriptionLabel.setText(formattedSize);
	                        	
	                        }
	                    });
	                }
	                catch (Exception e)
	                {
	                    SwingUtilities.invokeLater(new Runnable()
	                    {
	                        public void run()
	                        {
	                        	System.out.println("---");
	                            //descriptionLabel.setText("-");
	                        }
	                    });
	                }
	            }
	            else
	                SwingUtilities.invokeLater(new Runnable()
	                {
	                    public void run()
	                    {
	                    	System.out.println("---");
	                       // descriptionLabel.setText("-");
	                    }
	                });
	        }

	        public String toString()
	        {
	            return this.retrievable.getName();
	        }
	    }

	
	    
/*	    
   
  public class DownloadMonitorPanel extends JPanel {
		protected BulkRetrievalThread thread;
		protected Progress progress;
		protected Timer updateTimer;

		protected JLabel descriptionLabel;
		protected JProgressBar progressBar;
		protected JButton cancelButton;

		public DownloadMonitorPanel(BulkRetrievalThread thread) {
			this.thread = thread;
			this.progress = thread.getProgress();

			this.initComponents();

			this.updateTimer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					updateStatus();
				}
			});
			this.updateTimer.start();
		}

		protected void updateStatus() {
			// Update description
			String text = thread.getRetrievable().getName();
			text = text.length() > 30 ? text.substring(0, 27) + "..." : text;
			text += " (" + BulkDownloadPanel.makeSizeDescription(this.progress.getCurrentSize()) + " / "
					+ BulkDownloadPanel.makeSizeDescription(this.progress.getTotalSize()) + ")";
			this.descriptionLabel.setText(text);
			// Update progress bar
			int percent = 0;
			if (this.progress.getTotalCount() > 0)
				percent = (int) ((float) this.progress.getCurrentCount() / this.progress.getTotalCount() * 100f);
			this.progressBar.setValue(Math.min(percent, 100));
			// Update tooltip
			String tooltip = BulkDownloadPanel.makeSectorDescription(this.thread.getSector());
			this.descriptionLabel.setToolTipText(tooltip);
			this.progressBar.setToolTipText(makeProgressDescription());

			// Check for end of thread
			if (!this.thread.isAlive()) {
				// Thread is done
				this.cancelButton.setText("Remove");
				this.cancelButton.setBackground(Color.GREEN);
				this.updateTimer.stop();
			}
		}

		@SuppressWarnings({ "UnusedDeclaration" })
		protected void cancelButtonActionPerformed(ActionEvent event) {
			if (this.thread.isAlive()) {
				// Cancel thread
				this.thread.interrupt();
				this.cancelButton.setBackground(Color.ORANGE);
				this.cancelButton.setText("Remove");
				this.updateTimer.stop();
			} else {
				// Remove from monitor panel
				Container top = this.getTopLevelAncestor();
				this.getParent().remove(this);
				top.validate();
			}
		}

		/*
		 * protected void initComponents() { int border = 2; this.setLayout(new
		 * BoxLayout(this, BoxLayout.Y_AXIS));
		 * this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		 * 
		 * // Description label JPanel descriptionPanel = new JPanel(new
		 * GridLayout(0, 1, 0, 0));
		 * descriptionPanel.setBorder(BorderFactory.createEmptyBorder(border,
		 * border, border, border)); String text =
		 * thread.getRetrievable().getName(); text = text.length() > 40 ?
		 * text.substring(0, 37) + "..." : text; descriptionLabel = new
		 * JLabel(text); descriptionPanel.add(descriptionLabel);
		 * this.add(descriptionPanel);
		 * 
		 * // Progrees and cancel button JPanel progressPanel = new JPanel();
		 * progressPanel.setLayout(new BoxLayout(progressPanel,
		 * BoxLayout.X_AXIS));
		 * progressPanel.setBorder(BorderFactory.createEmptyBorder(border,
		 * border, border, border)); progressBar = new JProgressBar(0, 100);
		 * progressBar.setPreferredSize(new Dimension(100, 16));
		 * progressPanel.add(progressBar);
		 * progressPanel.add(Box.createHorizontalStrut(8)); cancelButton = new
		 * JButton("Cancel"); cancelButton.setBackground(Color.RED);
		 * cancelButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent event) {
		 * cancelButtonActionPerformed(event); } });
		 * progressPanel.add(cancelButton); this.add(progressPanel); }
		 
		protected String makeProgressDescription() {
			String text = "";
			if (this.progress.getTotalCount() > 0) {
				int percent = (int) ((double) this.progress.getCurrentCount() / this.progress.getTotalCount() * 100d);
				text = percent + "% of ";
				text += makeSizeDescription(this.progress.getTotalSize());
			}
			return text;
		}
		*/
	    
	    
	}

