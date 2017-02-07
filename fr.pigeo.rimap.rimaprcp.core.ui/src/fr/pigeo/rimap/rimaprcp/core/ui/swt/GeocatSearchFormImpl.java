package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatSearchResultSet;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatSearchTools;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
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
	private java.awt.Color[] colorPalette = {
			Color.white, Color.magenta, Color.green, Color.blue, Color.gray, 
			Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray, Color.pink,
			Color.white, Color.magenta, Color.green, Color.blue, Color.gray, 
			Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray, Color.pink,
			Color.white, Color.magenta, Color.green, Color.blue, Color.gray, 
			Color.orange, Color.cyan, Color.red, Color.yellow, Color.lightGray, Color.pink
	};

	@Inject
	GeocatSearchTools searchTools;

	@Inject
	WwjInstance wwjInst;

	public GeocatSearchFormImpl(Composite parent, int style) {
		super(parent, style);
		enhanceControls();
	}

	private void enhanceControls() {
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
	}

	protected void search(String text) {
		// clear the results list currently displayed
		Control[] controls = resultsListContainerComposite.getChildren();
		for (Control c : controls) {
			c.dispose();
		}

		GeocatSearchResultSet resultSet = searchTools.search(text);
		if (resultSet != null) {
			if (this.searchResultsRenderableLayer == null) {
				searchResultsRenderableLayer = new RenderableLayer();
				searchResultsRenderableLayer.setName("Search Results");
				wwjInst.addLayer(searchResultsRenderableLayer);
			} else {
				searchResultsRenderableLayer.removeAllRenderables();
			}

			List<GeocatMetadataEntity> metadata = resultSet.getMetadata();
			Iterator<GeocatMetadataEntity> it = metadata.iterator();
			int idx=0;
			while (it.hasNext()) {
				GeocatMetadataEntity mtd = it.next();
				if (mtd.getIdxError() == null) {
					GeocatSearchResultImpl mtdPanel = new GeocatSearchResultImpl(mtd, searchTools,
							resultsListContainerComposite, SWT.NONE);
					SurfacePolygon poly = mtdPanel.getPolygon(this.colorPalette[idx]);
					searchResultsRenderableLayer.addRenderable(poly);
					mtdPanel.addListener(SWT.MouseEnter, new Listener() {
						@Override
						public void handleEvent(Event event) {
							setHighlightedPolygon(poly);
							wwjInst.getWwd()
									.redraw();
						}
					});/*
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

	protected void setHighlightedPolygon(SurfacePolygon poly) {
		this.searchResultsRenderableLayer.getRenderables()
				.forEach(renderable -> {
					((SurfacePolygon) renderable).setHighlighted(renderable == poly);
				});
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
		return GeocatSearchTools.getAnysearchAutocompleteProposals(text);
	}

}
