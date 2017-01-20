package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.FillLayout;

public class GeocatSearchForm extends Composite {
	private Text text;

	public GeocatSearchForm(Composite parent, int style) {
		super(parent, style);
		
		createControls();	
	}
	
	public void createControls() {
		setLayout(new GridLayout(2, false));
		
		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSearch = new Button(this, SWT.NONE);
		btnSearch.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/magnifier.png"));
		
		Composite composite = new Composite(this, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.spacing = 10;
		rl_composite.center = true;
		composite.setLayout(rl_composite);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		Button btnAdvancedSearch = new Button(composite, SWT.TOGGLE);
		btnAdvancedSearch.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		btnAdvancedSearch.setText("Advanced Search");
		btnAdvancedSearch.setSelection(true);
		
		Button btnResults = new Button(composite, SWT.TOGGLE);
		btnResults.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		btnResults.setToolTipText("");
		btnResults.setText("Results");
		
		Button btnReset = new Button(composite, SWT.NONE);
		btnReset.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		btnReset.setText("Reset");
		btnReset.setToolTipText("");
		btnReset.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/cross.png"));
		new Label(this, SWT.NONE);
		
		Composite composite_1 = new Composite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		composite_1.setBackgroundMode(SWT.INHERIT_FORCE);
		
		Group grpGeographicFilter = new Group(composite_1, SWT.NONE);
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
		
		Group grpAssociatedResources = new Group(composite_1, SWT.NONE);
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
		
		Group grpFacets = new Group(composite_1, SWT.NONE);
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
