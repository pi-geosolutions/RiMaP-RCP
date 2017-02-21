package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatSearchResultSet;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataToolBox;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
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

	private RenderableLayer searchResultsRenderableLayer;
	private java.awt.Color[] colorPalette = { Color.white, Color.magenta, Color.green, Color.blue, Color.gray,
			Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray, Color.pink, Color.white, Color.magenta,
			Color.green, Color.blue, Color.gray, Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray,
			Color.pink, Color.white, Color.magenta, Color.green, Color.blue, Color.gray, Color.orange, Color.cyan,
			Color.red, Color.yellow, Color.lightGray, Color.pink };

	private List<GeocatSearchResultImpl> currentResultsPanels = new ArrayList<GeocatSearchResultImpl>();

	SortByValue[] values;
	
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
		//enhanceControls();
	}

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
				if (element instanceof SortByValue) {
					SortByValue val = (SortByValue) element;
					return val.getText();
				}
				return super.getText(element);
			}
		});
		values = new SortByValue[] {
				new SortByValue("relevance", messages.sortby_relevance),
				new SortByValue("changeDate", messages.sortby_changeDate),
				new SortByValue("title", messages.sortby_title),
//				new SortByValue("rating", messages.sortby_rating),
				new SortByValue("popularity", messages.sortby_popularity),
				new SortByValue("denominatorDesc", messages.sortby_denominatorDesc),
				new SortByValue("denominatorAsc", messages.sortby_denominatorAsc)
		};
		comboViewerSortBy.setInput(values);
		comboViewerSortBy.setSelection(new StructuredSelection(values[0]));
		
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
	}

	protected void search(String text) {
		// clear the results list currently displayed
		Control[] controls = resultsListContainerComposite.getChildren();
		for (Control c : controls) {
			c.dispose();
		}

		//retrieve the selected value from sortBy combo
		String sortby = (values==null)? "relevance": values[comboSortBy.getSelectionIndex()].getCode();
		
		GeocatSearchResultSet resultSet = searchTools.search(text, sortby);
		if (resultSet != null) {
			if (resultSet.hadException()) {
				if (searchResultsRenderableLayer != null) {
					searchResultsRenderableLayer.removeAllRenderables();
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

			if (this.searchResultsRenderableLayer == null) {
				searchResultsRenderableLayer = new RenderableLayer();
				searchResultsRenderableLayer.setName("Search Results");
				wwjInst.addLayer(searchResultsRenderableLayer);
			} else {
				searchResultsRenderableLayer.removeAllRenderables();
			}
			this.currentResultsPanels.clear();
			
			this.updateResultsBar(resultSet);

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
					searchResultsRenderableLayer.addRenderable(poly);
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

	private void updateResultsBar(GeocatSearchResultSet resultSet) {
		lblResultsNb.setText(resultSet.get_from()+"-"+resultSet.get_to()+"/"+resultSet.getSummary().get_count());
		resultsTopToolbar.layout(true);
	}

	protected void setHighlighted(GeocatSearchResultImpl searchres) {
		this.currentResultsPanels.forEach(panel -> {
			panel.setHighlighted(panel == searchres);
		});

	}

	protected void setHighlightedPolygon(SurfacePolygon poly) {
		if (this.searchResultsRenderableLayer != null) {
			this.searchResultsRenderableLayer.getRenderables()
					.forEach(renderable -> {
						((SurfacePolygon) renderable).setHighlighted(renderable == poly);
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

	private class SortByValue {
		private String code, text;

		public SortByValue(String code, String text) {
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
