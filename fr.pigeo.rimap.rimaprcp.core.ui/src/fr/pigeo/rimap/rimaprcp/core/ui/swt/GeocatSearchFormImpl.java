package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataToolBox;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatSearchResultSet;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Category;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Dimension;
import fr.pigeo.rimap.rimaprcp.core.ui.jface.FacetsContentProvider;
import fr.pigeo.rimap.rimaprcp.core.ui.jface.FacetsLabelProvider;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.util.SectorSelector;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfacePolygon;

public class GeocatSearchFormImpl extends GeocatSearchForm {
	/**
	 * A String array of default proposals for autocompletion
	 */
	private String[] defaultProposals = new String[] { "Assistance 1", "Assistance 2", "Assistance 3", "Assistance 4",
			"Assistance 5" };

	private ContentProposalAdapter adapter = null;
	private SimpleContentProposalProvider scp = new SimpleContentProposalProvider(defaultProposals);

	private java.awt.Color[] colorPalette = { Color.white, Color.magenta, Color.green, Color.blue, Color.gray,
			Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray, Color.pink, Color.white, Color.magenta,
			Color.green, Color.blue, Color.gray, Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray,
			Color.pink, Color.white, Color.magenta, Color.green, Color.blue, Color.gray, Color.orange, Color.cyan,
			Color.red, Color.yellow, Color.lightGray, Color.pink };

	private int nbResultsPerPage = 20;
	private int page = 1;

	private List<GeocatSearchResultImpl> currentResultsPanels = new ArrayList<GeocatSearchResultImpl>();

	private RenderableLayer geocatRenderableLayer;
	private SectorSelector sectorSelector;
	private boolean drawingSector = false;
	

	private static String IMAGE_CHECKED = "icons/checked.png";
	private static String IMAGE_UNCHECKED = "icons/unchecked.png";

	public static Image checkedImage;
	public static Image uncheckedImage;

	@Inject
	GeocatMetadataToolBox searchTools;

	@Inject
	WwjInstance wwjInst;

	@Inject
	IEclipseContext context;

	@Inject
	@Translation
	private Messages messages;

	public GeocatSearchFormImpl(Composite parent, int style) {
		super(parent, style);
		// enhanceControls();
	}

	/*
	 * Need Dependency Injection to have been run first (can't be called from
	 * inside the constructor)
	 * !!!! Beware: run it only once !
	 */
	public void enhanceControls() {
		// autocomplete in anysearch Text component
		// TODO : finish this
		/*
		 * this.txtFreeSearch.addKeyListener(new KeyAdapter() {
		 * public void keyReleased(KeyEvent ke) {
		 * // Method for autocompletion
		 * String txt = txtFreeSearch.getText();
		 * if (txt != null && txt.length() > 2) {
		 * setAutoCompletion(txtFreeSearch, txt);
		 * }
		 * }
		 * });
		 */

		// sortBy combo entries
		comboViewerSortBy.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ComboKeyValue) {
					ComboKeyValue val = (ComboKeyValue) element;
					return val.getText();
				}
				return super.getText(element);
			}
		});
		ComboKeyValue[] sortBy = new ComboKeyValue[] { new ComboKeyValue("relevance", messages.sortby_relevance),
				new ComboKeyValue("changeDate", messages.sortby_changeDate),
				new ComboKeyValue("title", messages.sortby_title),
				// new SortByValue("rating", messages.sortby_rating),
				new ComboKeyValue("popularity", messages.sortby_popularity),
				new ComboKeyValue("denominatorDesc", messages.sortby_denominatorDesc),
				new ComboKeyValue("denominatorAsc", messages.sortby_denominatorAsc) };
		comboViewerSortBy.setInput(sortBy);
		comboViewerSortBy.setSelection(new StructuredSelection(sortBy[0]));
		comboSortBy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// trigger a new search to take into account the change
				// using pagination started from the beginning (page 1)
				page = 1;
				search(txtFreeSearch.getText());
			}
		});

		// perform search on search button click
		this.btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(txtFreeSearch.getText());
			}
		});
		this.txtFreeSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					search(txtFreeSearch.getText());
				}
			}
		});

		this.btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtFreeSearch.setText("");
				btnCheckDownloadable.setSelection(false);
				btnCheckDynamicMap.setSelection(false);
			}
		});

		resultsListContainerComposite.addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!resultsListContainerComposite.getBounds()
						.contains(new Point(event.x, event.y))) {
					setHighlighted(null);
					setHighlightedPolygon(null);
					wwjInst.getWwd()
							.redraw();
				}
			}
		});

		this.btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// increment page number (results pagination)
				page++;
				search(txtFreeSearch.getText());
			}
		});
		this.btnPrev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// increment page number (results pagination)
				if (page == 1) {
					return;
				}
				page--;
				search(txtFreeSearch.getText());
			}
		});

		// Advanced Search

		// Init sector selector
		RenderableLayer rl = new RenderableLayer();
		rl.setName("Search extent");
		this.sectorSelector = new SectorSelector(wwjInst.getWwd(), rl);
		this.sectorSelector.setInteriorColor(new Color(1f, 1f, 1f, 0.1f));
		this.sectorSelector.setBorderColor(new Color(1f, 0f, 0f, 0.5f));
		this.sectorSelector.setBorderWidth(3);
		/*
		 * this.sectorSelector.addPropertyChangeListener(SectorSelector.
		 * SECTOR_PROPERTY, new PropertyChangeListener() {
		 * public void propertyChange(PropertyChangeEvent evt) {
		 * 
		 * }
		 * });
		 */

		btnDrawExtent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				drawingSector = !drawingSector;
				if (drawingSector) {
					btnDrawExtent.setText("Clear extent");
					sectorSelector.enable();
				} else {
					sectorSelector.disable();
					btnDrawExtent.setText("Draw extent");
				}
			}
		});
		// sortBy combo entries
		comboViewerSortBy.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ComboKeyValue) {
					ComboKeyValue val = (ComboKeyValue) element;
					return val.getText();
				}
				return super.getText(element);
			}
		});
		ComboKeyValue[] relation = new ComboKeyValue[] {
				new ComboKeyValue("intersection", messages.extent_intersection),
				new ComboKeyValue("within", messages.extent_within) };
		comboViewerExtent.setInput(relation);
		comboViewerExtent.setSelection(new StructuredSelection(relation[0]));

	}

	protected RenderableLayer getRenderableLayer() {
		return this.getRenderableLayer(false);
	}

	protected RenderableLayer getRenderableLayer(boolean clear) {
		if (this.geocatRenderableLayer == null) {
			geocatRenderableLayer = new RenderableLayer();
			geocatRenderableLayer.setName("Search Results");
			wwjInst.addLayer(geocatRenderableLayer);
		} else {
			if (clear) {
				geocatRenderableLayer.removeAllRenderables();
			}
		}
		return this.geocatRenderableLayer;
	}

	protected void search(String text) {
		// clear the results list currently displayed
		Control[] controls = resultsListContainerComposite.getChildren();
		for (Control c : controls) {
			c.dispose();
		}

		// retrieve the selected value from sortBy combo
		// String sortby = (values == null) ? "relevance" :
		// values[comboSortBy.getSelectionIndex()].getCode();
		String sortby = ((ComboKeyValue) comboViewerSortBy.getStructuredSelection()
				.getFirstElement()).getCode();

		int startIndex = 1 + (page - 1) * nbResultsPerPage;
		int endIndex = page * nbResultsPerPage;
		boolean advSearchDownloadable = btnCheckDownloadable.getSelection();
		boolean advSearchDynamic = btnCheckDynamicMap.getSelection();
		Sector sector = (drawingSector ? sectorSelector.getSector() : null);
		// retrieve the selected value from extent combo
		String extentRelation = ((ComboKeyValue) comboViewerExtent.getStructuredSelection()
				.getFirstElement()).getCode();

		GeocatSearchResultSet resultSet = searchTools.search(text, sortby, startIndex, endIndex, advSearchDownloadable,
				advSearchDynamic, sector, extentRelation);
		if (resultSet != null) {
			if (resultSet.hadException()) {
				// clear RenderableLayer
				if (geocatRenderableLayer != null) {
					geocatRenderableLayer.removeAllRenderables();
				}
				Text txtError = new Text(resultsListContainerComposite, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
				txtError.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
				txtError.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				txtError.setFont(SWTResourceManager.getFont("Sans", 12, SWT.ITALIC | SWT.BOLD));
				txtError.setText(resultSet.getException()
						.getClass()
						.getName() + " : \n"
						+ resultSet.getException()
								.getLocalizedMessage());
				Text txtErrorFull = new Text(resultsListContainerComposite, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
				txtErrorFull.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
				txtErrorFull.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				txtErrorFull.setFont(SWTResourceManager.getFont("Sans", 7, SWT.ITALIC));
				txtErrorFull.setText(GeocatMetadataToolBox.stackTraceToString(resultSet.getException()));
				resultsListContainerComposite.pack();

				tabFolder.setSelection(this.tbtmResults);
				return;
			}

			// clear renderable layer
			RenderableLayer rlayer = getRenderableLayer(true);
			this.currentResultsPanels.clear();

			this.updateResultsBar(resultSet);
			
			this.updateFacets(resultSet);

			List<GeocatMetadataEntity> metadata = resultSet.getMetadata();
			Iterator<GeocatMetadataEntity> it = metadata.iterator();
			int idx = 0;
			while (it.hasNext()) {
				GeocatMetadataEntity mtd = it.next();
				if (mtd.getIdxError() == null) {
					GeocatSearchResultImpl mtdPanel = new GeocatSearchResultImpl(mtd, searchTools, wwjInst, context,
							resultsListContainerComposite, SWT.NONE);
					currentResultsPanels.add(mtdPanel);
					SurfacePolygon poly = mtdPanel.getPolygon(this.colorPalette[idx]);
					if (poly != null) {
						rlayer.addRenderable(poly);
					}
					mtdPanel.addListener(SWT.MouseEnter, new Listener() {
						@Override
						public void handleEvent(Event event) {
							setHighlighted(mtdPanel);
							setHighlightedPolygon(poly);
							// mtdPanel.setHighlighted(true);
							// poly.setHighlighted(true);
							wwjInst.getWwd()
									.redraw();
						}
					});
					/*
					 * mtdPanel.addListener(SWT.MouseExit, new Listener() {
					 * 
					 * @Override
					 * public void handleEvent(Event event) {
					 * for (Control child : mtdPanel.getChildren()) {
					 * if (child.getBounds()
					 * .contains(new Point(event.x, event.y))) {
					 * System.out.println(child.getClass());
					 * System.out.println(
					 * "Bounds : x=" + child.getBounds().x + "  y=" +
					 * child.getBounds().y + "  w="
					 * + child.getBounds().width + "  h=" +
					 * child.getBounds().height);
					 * System.out.println("Mouse location : x=" + event.x +
					 * "  y=" + event.y);
					 * return;
					 * }
					 * }
					 * setHighlighted(null);
					 * setHighlightedPolygon(null);
					 * // mtdPanel.setHighlighted(false);
					 * // poly.setHighlighted(false);
					 * wwjInst.getWwd()
					 * .redraw();
					 * }
					 * });
					 *//*
						 * mtdPanel.addListener(SWT.MouseEnter, new Listener() {
						 * 
						 * @Override
						 * public void handleEvent(Event event) {
						 * System.out.println("entering "
						 * +mtdPanel.txtTitle.getText());
						 * mtdPanel.setData(
						 * "org.eclipse.e4.ui.css.CssClassName", "hover");
						 * }
						 * });
						 * mtdPanel.addListener(SWT.MouseExit, new Listener() {
						 * 
						 * @Override
						 * public void handleEvent(Event event) {
						 * //do not change background color if we are entering a
						 * child widget
						 * for (Control child : mtdPanel.getChildren()) {
						 * if (child.getBounds()
						 * .contains(new Point(event.x, event.y)))
						 * return;
						 * }
						 * 
						 * mtdPanel.setData(
						 * "org.eclipse.e4.ui.css.CssClassName", "");
						 * System.out.println("exiting "
						 * +mtdPanel.txtTitle.getText());
						 * }
						 * });
						 */
				}

				idx++;
			}
		}
		tabFolder.setSelection(this.tbtmResults);
		wwjInst.getWwd()
				.redrawNow();
		resultsListContainerComposite.setSize(resultsListContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		resultsListContainerComposite.layout();
	}
	
	public void updateFacets(GeocatSearchResultSet resultSet) {
		for (Control c : grpFacets.getChildren()) {
			c.dispose();
		}
		List<Dimension> facets = resultSet.getSummary().getDimension();
		TreeViewer viewer = new TreeViewer(grpFacets, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	    viewer.setContentProvider(new FacetsContentProvider());
	    viewer.setLabelProvider(new FacetsLabelProvider());
	    viewer.setInput(resultSet.getSummary());
	    viewer.expandAll();
	    Tree tree = viewer.getTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// normally, we can click only 1 item at a time. So we get the
				// first one of the array
				if (tree.getSelection().length > 0) {
					TreeItem item = tree.getSelection()[0];
					if (item.getData() instanceof Category) {
						Category node = (Category) item.getData();
							if (item.getImage() != null) {
								if ((e.x > item.getImageBounds(0).x) && (e.x < (item.getImageBounds(0).x + item.getImage()
										.getBounds().width))) {
									if ((e.y > item.getImageBounds(0).y) && (e.y < (item.getImageBounds(0).y + item.getImage()
											.getBounds().height))) {
											node.toggleChecked();
											item.setImage(getCheckboxImage(node.isChecked()));
									}
								}
						}
					}
					
				}

			}
		});

	    grpFacets.layout(true);
	    grpFacets.pack();
	    advSearchComposite.pack();
	}

	public static Image getCheckboxImage(boolean checkedStatus) {
		if (checkedStatus) {
			if (checkedImage==null) {
				checkedImage = createImage(IMAGE_CHECKED);
			}
			return checkedImage;
		} else {
			if (uncheckedImage==null) {
				uncheckedImage = createImage(IMAGE_UNCHECKED);
			}
			return uncheckedImage;
		}
	}
	
	protected static Image createImage(String path) {
		Bundle bundle = FrameworkUtil.getBundle(GeocatSearchFormImpl.class);
		URL url = FileLocator.find(bundle, new Path(path), null);

		ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
		return imageDescr.createImage();
	}

	private void updateResultsBar(GeocatSearchResultSet resultSet) {
		int from = Integer.parseInt(resultSet.get_from());
		int to = Integer.parseInt(resultSet.get_to());
		int count = Integer.parseInt(resultSet.getSummary()
				.get_count());

		lblResultsNb.setText(from + "-" + to + "/" + count);

		btnNext.setEnabled(to < count);
		btnPrev.setEnabled(from > 1);

		resultsTopToolbar.layout(true);

	}

	protected void setHighlighted(GeocatSearchResultImpl searchres) {
		this.currentResultsPanels.forEach(panel -> {
			panel.setHighlighted(panel == searchres);
		});

	}

	protected void setHighlightedPolygon(SurfacePolygon poly) {
		if (this.geocatRenderableLayer != null) {
			this.geocatRenderableLayer.getRenderables()
					.forEach(renderable -> {
						if (renderable instanceof SurfacePolygon) {
							((SurfacePolygon) renderable).setHighlighted(renderable == poly);
						}
					});
		}
	}

	private void setAutoCompletion(Text text, String value) {
		try {
			scp.setProposals(getAllProposals(value));
			if (adapter == null) {
				adapter = new ContentProposalAdapter(text, new TextContentAdapter(), scp, null, null);
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String[] getAllProposals(String text) {
		return GeocatMetadataToolBox.getAnysearchAutocompleteProposals(text);
	}

	private class ComboKeyValue {
		private String code, text;

		public ComboKeyValue(String code, String text) {
			this.code = code;
			this.text = text;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String toString() {
			return this.text;
		}
	}

}
