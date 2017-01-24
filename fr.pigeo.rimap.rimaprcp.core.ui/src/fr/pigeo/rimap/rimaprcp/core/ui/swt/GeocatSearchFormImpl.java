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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

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
		this.txtFreeSearch.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				// Method for autocompletion
				String txt = txtFreeSearch.getText();
				if (txt != null && txt.length() > 2) {
					setAutoCompletion(txtFreeSearch, txt);
				}
			}
		});

		// perform search on search button click
		this.btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(txtFreeSearch.getText());
			}
		});
	}

	protected void search(String text) {
		//clear the results list currently displayed
		Control[] controls = resultsListContainerComposite.getChildren();
		for (Control c : controls) {
			c.isVisible();
		}
		
		GeocatSearchResultSet resultSet = searchTools.search(text);
		if (resultSet != null) {
			List<GeocatMetadataEntity> metadata = resultSet.getMetadata();
			Iterator<GeocatMetadataEntity> it = metadata.iterator();
			while (it.hasNext()) {
				GeocatMetadataEntity mtd = it.next();
				GeocatSearchResultImpl mtdPanel = new GeocatSearchResultImpl(mtd, resultsListContainerComposite,
						SWT.NONE);
			}
		}
		resultsListContainerComposite.layout(true);
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
