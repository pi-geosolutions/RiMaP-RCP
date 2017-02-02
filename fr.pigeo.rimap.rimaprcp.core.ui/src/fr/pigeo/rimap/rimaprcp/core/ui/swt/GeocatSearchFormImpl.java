package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
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

import geocatalog.GeocatMetadataEntity;
import geocatalog.GeocatSearchResultSet;
import geocatalog.GeocatSearchTools;

public class GeocatSearchFormImpl extends GeocatSearchForm {
	/**
	 * A String array of default proposals for autocompletion
	 */
	private String[] defaultProposals = new String[] { "Assistance 1", "Assistance 2", "Assistance 3", "Assistance 4",
			"Assistance 5" };

	private ContentProposalAdapter adapter = null;
	private SimpleContentProposalProvider scp = new SimpleContentProposalProvider(defaultProposals);

	@Inject
	@Optional
	GeocatSearchTools searchTools;

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
			List<GeocatMetadataEntity> metadata = resultSet.getMetadata();
			Iterator<GeocatMetadataEntity> it = metadata.iterator();
			while (it.hasNext()) {
				GeocatMetadataEntity mtd = it.next();
				if (mtd.getIdxError() == null) {
					GeocatSearchResultImpl mtdPanel = new GeocatSearchResultImpl(mtd, searchTools,
							resultsListContainerComposite, SWT.NONE);
					mtdPanel.addListener(SWT.MouseHover, new Listener() {
						@Override
						public void handleEvent(Event event) {
							System.out.println("hover " + mtd.getDefaultTitle());
						}
					});
					mtdPanel.addListener(SWT.MouseEnter, new Listener() {
						@Override
						public void handleEvent(Event event) {
							mtdPanel.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
						}
					});
					mtdPanel.addListener(SWT.MouseExit, new Listener() {
						@Override
						public void handleEvent(Event event) {
							//do not change background color if we are entering a child widget
							for (Control child : mtdPanel.getChildren()) {
								if (child.getBounds()
										.contains(new Point(event.x, event.y)))
									return;
							}
							
							mtdPanel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
						}
					});
				}
			}
		}
		tabFolder.setSelection(this.tbtmResults);
		resultsListContainerComposite.setSize(resultsListContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		resultsListContainerComposite.layout();
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
