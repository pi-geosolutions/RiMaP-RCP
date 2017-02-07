package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.ScrolledComposite;

public class GeocatSearchForm extends Composite {
	protected Text txtFreeSearch;
	protected Button btnSearch;
	protected Button btnReset;
	protected TabFolder tabFolder;
	protected  Composite resultsListContainerComposite;
	protected TabItem tbtmResults;
	protected TabItem tbtmAdvSearch;

	public GeocatSearchForm(Composite parent, int style) {
		super(parent, SWT.NO_BACKGROUND);
		
		createControls();	
	}
	
	public void createControls() {
		setLayout(new GridLayout(3, false));
		
		Label lblSearch = new Label(this, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		lblSearch.setText("Search:");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		txtFreeSearch = new Text(this, SWT.BORDER);
		txtFreeSearch.setText("population");
		txtFreeSearch.setToolTipText("");
		txtFreeSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSearch = new Button(this, SWT.NONE);
		btnSearch.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/magnifier.png"));
		
		btnReset = new Button(this, SWT.NONE);
		btnReset.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		btnReset.setToolTipText("Reset");
		btnReset.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/cross.png"));
		

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		tbtmAdvSearch = new TabItem(tabFolder, SWT.NONE);
		tbtmAdvSearch.setText("Advanced Search");
		
		tbtmResults = new TabItem(tabFolder, SWT.NONE);
		tbtmResults.setText("Results");
		
		Composite resultsComposite = new Composite(tabFolder, SWT.NONE);
		tbtmResults.setControl(resultsComposite);
		resultsComposite.setLayout(new GridLayout(1, false));
		
		Composite resultsTopToolbar = new Composite(resultsComposite, SWT.NONE);
		GridData gd_resultsTopToolbar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_resultsTopToolbar.heightHint = 30;
		resultsTopToolbar.setLayoutData(gd_resultsTopToolbar);
		resultsTopToolbar.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		CCombo combo_1 = new CCombo(resultsTopToolbar, SWT.BORDER);
		combo_1.setLayoutData(new RowData(67, 17));
		
		Label label_1 = new Label(resultsTopToolbar, SWT.NONE);
		label_1.setText("       ");
		
		Label lblSortBy = new Label(resultsTopToolbar, SWT.NONE);
		lblSortBy.setLayoutData(new RowData(SWT.DEFAULT, 20));
		lblSortBy.setText("Sort by: ");
		
		CCombo combo_2 = new CCombo(resultsTopToolbar, SWT.BORDER);
		
		Button button = new Button(resultsTopToolbar, SWT.NONE);
		button.setText("<");
		
		Button button_1 = new Button(resultsTopToolbar, SWT.NONE);
		button_1.setText(">");
		
		Label lblResults = new Label(resultsTopToolbar, SWT.NONE);
		lblResults.setText("Results ");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite( resultsComposite, SWT.H_SCROLL | SWT.V_SCROLL );
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		resultsListContainerComposite = new Composite(scrolledComposite, SWT.NONE);
		resultsListContainerComposite.setLayout(new GridLayout(1, false));
		resultsListContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		resultsListContainerComposite.setBounds(0, 0, 64, 64);
		scrolledComposite.setContent( resultsListContainerComposite );
		
				
		Composite advSearchComposite = new Composite(tabFolder, SWT.BORDER);
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
		grpGeographicFilter.setText("Geographic filter");
		
		Button btnDrawExtent = new Button(grpGeographicFilter, SWT.NONE);
		btnDrawExtent.setText("Draw Extent");
		btnDrawExtent.setToolTipText("Draw Extent");
		btnDrawExtent.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/draw_rectangle_off.png"));
		
		Label label = new Label(grpGeographicFilter, SWT.NONE);
		label.setText("         ");
		
		Label lblRelation = new Label(grpGeographicFilter, SWT.NONE);
		lblRelation.setText("relation: ");
		
		CCombo combo = new CCombo(grpGeographicFilter, SWT.BORDER);
		combo.setItems(new String[] {"Intersection with", "Completely inside of"});
		
		Group grpAssociatedResources = new Group(advSearchComposite, SWT.NONE);
		grpAssociatedResources.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FillLayout fl_grpAssociatedResources = new FillLayout(SWT.VERTICAL);
		fl_grpAssociatedResources.spacing = 3;
		fl_grpAssociatedResources.marginHeight = 3;
		fl_grpAssociatedResources.marginWidth = 10;
		grpAssociatedResources.setLayout(fl_grpAssociatedResources);
		grpAssociatedResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpAssociatedResources.setText("Associated resources");
		
		Button btnCheckButton = new Button(grpAssociatedResources, SWT.CHECK);
		btnCheckButton.setText("Downloadable data");
		
		Button btnCheckButton_1 = new Button(grpAssociatedResources, SWT.CHECK);
		btnCheckButton_1.setText("Visualisable data");
		
		Group grpFacets = new Group(advSearchComposite, SWT.BORDER | SWT.SHADOW_IN);
		grpFacets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpFacets.setText("Refine Search");
		grpFacets.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FillLayout fl_grpFacets = new FillLayout(SWT.VERTICAL);
		fl_grpFacets.spacing = 3;
		fl_grpFacets.marginWidth = 10;
		fl_grpFacets.marginHeight = 3;
		grpFacets.setLayout(fl_grpFacets);
		
		Label lblToDo = new Label(grpFacets, SWT.NONE);
		lblToDo.setText("To do");
	}
}
