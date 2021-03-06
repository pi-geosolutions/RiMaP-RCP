package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.ui.core.Plugin;

public class GeocatSearchForm extends Composite {
	protected Text txtFreeSearch;
	protected Button btnSearch;
	protected Button btnReset;
	protected TabFolder tabFolder;
	protected Composite resultsListContainerComposite;
	protected TabItem tbtmResults;
	protected TabItem tbtmAdvSearch;
	protected CCombo comboSortBy;
	protected ComboViewer comboViewerSortBy;
	protected Label lblResultsNb;
	protected Composite resultsTopToolbar;
	protected Button btnNext;
	protected Button btnPrev;
	protected Button btnCheckDynamicMap;
	protected Button btnCheckDownloadable;
	protected Button btnDrawExtent;
	protected CCombo comboExtent;
	protected ComboViewer comboViewerExtent;
	protected Group grpFacets;
	protected Composite advSearchComposite;
	protected Label lblPleaseFirstPerform;
	protected Label lblSearch;

	public GeocatSearchForm(Composite parent, int style) {
		super(parent, SWT.COLOR_WIDGET_BACKGROUND);
		//setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		createControls();	
	}
	
	public void createControls() {
		setLayout(new GridLayout(3, false));
		
		lblSearch = new Label(this, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		//lblSearch.setText("Search:");
		lblSearch.setText(Plugin.translate("geocat.search.title"));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		txtFreeSearch = new Text(this, SWT.BORDER);
		txtFreeSearch.setToolTipText("");
		txtFreeSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSearch = new Button(this, SWT.NONE);
		btnSearch.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/magnifier.png"));
		
		btnReset = new Button(this, SWT.NONE);
		btnReset.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		btnReset.setToolTipText(Plugin.translate("geocat.search.btn.reset"));
		btnReset.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/cross.png"));
		

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		tbtmAdvSearch = new TabItem(tabFolder, SWT.NONE);
		tbtmAdvSearch.setText(Plugin.translate("geocat.search.advancedsearch"));
		
		tbtmResults = new TabItem(tabFolder, SWT.NONE);
		tbtmResults.setText(Plugin.translate("geocat.search.results"));
		
		Composite resultsComposite = new Composite(tabFolder, SWT.NONE);
		tbtmResults.setControl(resultsComposite);
		GridLayout gl_resultsComposite = new GridLayout(1, false);
		gl_resultsComposite.verticalSpacing = 0;
		gl_resultsComposite.marginHeight = 0;
		resultsComposite.setLayout(gl_resultsComposite);
		
		resultsTopToolbar = new Composite(resultsComposite, SWT.NONE);
		GridLayout gl_resultsTopToolbar = new GridLayout(7, false);
		resultsTopToolbar.setLayout(gl_resultsTopToolbar);
		GridData gd_resultsTopToolbar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_resultsTopToolbar.heightHint = 28;
		resultsTopToolbar.setLayoutData(gd_resultsTopToolbar);
		
		/*CCombo combo_1 = new CCombo(resultsTopToolbar, SWT.NONE);
		combo_1.setLayoutData(new RowData(67, 16));
		
		Label label_1 = new Label(resultsTopToolbar, SWT.NONE);
		label_1.setText("       ");*/
		
		Label lblSortBy = new Label(resultsTopToolbar, SWT.NONE);
		GridData gd_lblSortBy = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSortBy.heightHint = 16;
		lblSortBy.setLayoutData(gd_lblSortBy);
		lblSortBy.setText(Plugin.translate("geocat.search.sortby"));
		
		comboSortBy = new CCombo(resultsTopToolbar, SWT.NONE);
		GridData gd_comboSortBy = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboSortBy.widthHint = 150;
		gd_comboSortBy.heightHint = 16;
		comboSortBy.setLayoutData(gd_comboSortBy);
		comboSortBy.setEditable(false);
		comboSortBy.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		//comboSortBy.setItems(new String[] {"relevance", "changeDate", "title", "rating", "popularity", "denominatorDesc", "denominatorAsc"});
		comboSortBy.select(0);
		
		comboViewerSortBy = new ComboViewer(comboSortBy);
		comboViewerSortBy.setContentProvider(ArrayContentProvider.getInstance());
		/*comboSortBy = comboViewerSortBy.getCombo();
		comboSortBy.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		comboSortBy.setLayoutData(new RowData(SWT.DEFAULT, 16));*/
		
		Label lblFiller = new Label(resultsTopToolbar, SWT.NONE);
		GridData gd_lblFiller = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblFiller.minimumWidth = 5;
		lblFiller.setLayoutData(gd_lblFiller);
		
		Label lblResults = new Label(resultsTopToolbar, SWT.NONE);
		GridData gd_lblResults = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblResults.heightHint = 16;
		lblResults.setLayoutData(gd_lblResults);
		lblResults.setText(Plugin.translate("geocat.search.results")+" ");
		
		lblResultsNb = new Label(resultsTopToolbar, SWT.NONE);
		GridData gd_lblResultsNb = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblResultsNb.heightHint = 16;
		lblResultsNb.setLayoutData(gd_lblResultsNb);
		
		btnPrev = new Button(resultsTopToolbar, SWT.NONE);
		GridData gd_btnPrev = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrev.heightHint = 16;
		btnPrev.setLayoutData(gd_btnPrev);
		btnPrev.setEnabled(false);
		btnPrev.setText("<");
		
		btnNext = new Button(resultsTopToolbar, SWT.NONE);
		GridData gd_btnNext = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNext.heightHint = 16;
		btnNext.setLayoutData(gd_btnNext);
		btnNext.setEnabled(false);
		btnNext.setText(">");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite( resultsComposite, SWT.H_SCROLL | SWT.V_SCROLL );
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		resultsListContainerComposite = new Composite(scrolledComposite, SWT.NONE);
		resultsListContainerComposite.setLayout(new GridLayout(1, false));
		resultsListContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		resultsListContainerComposite.setBounds(0, 0, 64, 64);
		resultsListContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		
		lblPleaseFirstPerform = new Label(resultsListContainerComposite, SWT.NONE);
		lblPleaseFirstPerform.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblPleaseFirstPerform.setText(Plugin.translate("geocat.search.pleasePerformASearch"));
		scrolledComposite.setContent( resultsListContainerComposite );
		
				
		advSearchComposite = new Composite(tabFolder, SWT.BORDER);
		tbtmAdvSearch.setControl(advSearchComposite);
		advSearchComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		advSearchComposite.setLayout(new GridLayout(1, false));
		advSearchComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		
		Group grpGeographicFilter = new Group(advSearchComposite, SWT.NONE);
		grpGeographicFilter.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		RowLayout rl_grpGeographicFilter = new RowLayout(SWT.HORIZONTAL);
		rl_grpGeographicFilter.marginLeft = 10;
		rl_grpGeographicFilter.center = true;
		grpGeographicFilter.setLayout(rl_grpGeographicFilter);
		grpGeographicFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpGeographicFilter.setText(Plugin.translate("geocat.search.geogfilter"));
		
		btnDrawExtent = new Button(grpGeographicFilter, SWT.NONE);
		btnDrawExtent.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		btnDrawExtent.setText(Plugin.translate("geocat.search.drawextent"));
		btnDrawExtent.setToolTipText(Plugin.translate("geocat.search.drawextent"));
		btnDrawExtent.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/draw_rectangle_off.png"));
		
		Label label = new Label(grpGeographicFilter, SWT.NONE);
		label.setText("     ");
		
		Label lblRelation = new Label(grpGeographicFilter, SWT.NONE);
		lblRelation.setText(Plugin.translate("geocat.search.relation"));
		
		comboExtent = new CCombo(grpGeographicFilter, SWT.BORDER);
		//comboExtent.setItems(new String[] {"Intersection with", "Completely inside of"});
		comboExtent.select(0);
		
		comboViewerExtent = new ComboViewer(comboExtent);
		comboViewerExtent.setContentProvider(ArrayContentProvider.getInstance());
		
		Group grpAssociatedResources = new Group(advSearchComposite, SWT.NONE);
		grpAssociatedResources.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FillLayout fl_grpAssociatedResources = new FillLayout(SWT.VERTICAL);
		fl_grpAssociatedResources.spacing = 3;
		fl_grpAssociatedResources.marginHeight = 3;
		fl_grpAssociatedResources.marginWidth = 10;
		grpAssociatedResources.setLayout(fl_grpAssociatedResources);
		grpAssociatedResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpAssociatedResources.setText(Plugin.translate("geocat.search.resources"));
		
		btnCheckDownloadable = new Button(grpAssociatedResources, SWT.CHECK);
		btnCheckDownloadable.setText(Plugin.translate("geocat.search.downloadable"));
		
		btnCheckDynamicMap = new Button(grpAssociatedResources, SWT.CHECK);
		btnCheckDynamicMap.setText(Plugin.translate("geocat.search.visualisable"));
		
		grpFacets = new Group(advSearchComposite, SWT.SHADOW_IN);
		grpFacets.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpFacets.setText(Plugin.translate("geocat.search.refine"));
		grpFacets.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FillLayout fl_grpFacets = new FillLayout(SWT.VERTICAL);
		fl_grpFacets.spacing = 3;
		fl_grpFacets.marginWidth = 10;
		fl_grpFacets.marginHeight = 3;
		grpFacets.setLayout(fl_grpFacets);
		
		Label lblToDo = new Label(grpFacets, SWT.NONE);
		lblToDo.setText(Plugin.translate("geocat.search.refinemsg"));
	}
}
