package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload.BulkDownloadManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheUtil;
import gov.nasa.worldwind.geom.Sector;

public class BulkDownloadPanel extends Composite {
	private BulkDownloadManager bulkManager;
	
	private Group grpSelectLayersTo;
	private Group grpDefineArea;
	private Text txt_N;
	private Text txt_E;
	private Text txt_S;
	private Text txt_W;
	private Button btn_DrawExtent, btn_startDownload;
	private Label lbl_totalValue,lbl_message;

	public BulkDownloadPanel(Composite parent, int style, BulkDownloadManager bulkManager) {
		super(parent, style);
		this.bulkManager = bulkManager;
		ContextInjectionFactory.inject(this, bulkManager.getEclipseContext());
		
		createControls();
		createControlsListeners();	
	}

	private void createControls() {
		setLayout(new GridLayout(2, false));
		grpSelectLayersTo = new Group(this, SWT.NONE);
		GridData gd_grpSelectLayersTo = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_grpSelectLayersTo.heightHint = 62;
		grpSelectLayersTo.setLayoutData(gd_grpSelectLayersTo);
		grpSelectLayersTo.setText("Select Layers to Download");
		grpSelectLayersTo.setLayout(new FillLayout(SWT.HORIZONTAL));

		BulkDownloadLayersTable bdlt = new BulkDownloadLayersTable(grpSelectLayersTo, SWT.BORDER_SOLID, bulkManager);

		grpDefineArea = new Group(this, SWT.NONE);
		grpDefineArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpDefineArea.setText("Define Download Area");
		grpDefineArea.setLayout(new GridLayout(2, false));

		Composite composite = new Composite(grpDefineArea, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gd_composite.minimumHeight = 300;
		composite.setLayoutData(gd_composite);

		txt_N = new Text(composite, SWT.BORDER);
		txt_N.setBounds(162, 15, 75, 25);

		txt_E = new Text(composite, SWT.BORDER);
		txt_E.setBounds(243, 31, 75, 25);

		txt_S = new Text(composite, SWT.BORDER);
		txt_S.setBounds(162, 46, 75, 25);

		txt_W = new Text(composite, SWT.BORDER);
		txt_W.setBounds(81, 31, 75, 25);

		Label lbl_W = new Label(composite, SWT.RIGHT);
		lbl_W.setText("W");
		lbl_W.setBounds(55, 38, 21, 15);

		Label lbl_E = new Label(composite, SWT.NONE);
		lbl_E.setText("E");
		lbl_E.setBounds(324, 38, 21, 15);

		Label lbl_N = new Label(composite, SWT.CENTER);
		lbl_N.setText("N");
		lbl_N.setBounds(190, 0, 16, 15);

		Label lbl_S = new Label(composite, SWT.CENTER);
		lbl_S.setText("S");
		lbl_S.setBounds(190, 72, 16, 15);

		btn_DrawExtent = new Button(grpDefineArea, SWT.NONE);
		btn_DrawExtent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btn_DrawExtent.setText("Draw extent");
		new Label(grpDefineArea, SWT.NONE);
		new Label(grpDefineArea, SWT.NONE);

		Label lbl_total = new Label(this, SWT.NONE);
		lbl_total.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lbl_total.setText("Total size");

		lbl_totalValue = new Label(this, SWT.BORDER | SWT.CENTER);
		lbl_totalValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lbl_totalValue.setText("-");
		
		Label lbl_available = new Label(this, SWT.NONE);
		lbl_available.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lbl_available.setText("Available space");

		Label lbl_availableValue = new Label(this, SWT.BORDER | SWT.CENTER);
		lbl_availableValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lbl_availableValue.setText(CacheUtil.makeSizeDescription(bulkManager.getFreeSpace()));

		lbl_message = new Label(this, SWT.NONE);
		lbl_message.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lbl_message.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD | SWT.ITALIC));
		lbl_message.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lbl_message.setText("");

		btn_startDownload = new Button(this, SWT.RIGHT);
		btn_startDownload.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btn_startDownload.setText("Start Download");
		btn_startDownload.setEnabled(false);
	}

	private void createControlsListeners() {
		btn_DrawExtent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				bulkManager.enableSectorSelector();
			}
		});
		btn_startDownload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				bulkManager.startBulkDownload();
				btn_startDownload.setEnabled(false);
			}
		});
	}
	
	@Inject
	@Optional
	void updateSector(@UIEventTopic(CacheManagerEventConstants.SECTORSELECTOR_DRAWING) Sector s) {
        if (s != null)
        {
            // Update sector description
        	txt_N.setText(String.format("%7.4f\u00B0",s.getMaxLatitude().degrees));
        	txt_S.setText(String.format("%7.4f\u00B0",s.getMinLatitude().degrees));
        	txt_E.setText(String.format("%7.4f\u00B0",s.getMaxLongitude().degrees));
        	txt_W.setText(String.format("%7.4f\u00B0",s.getMinLongitude().degrees));
        }
        else
        {
            // null sector
        	txt_N.setText("");
        	txt_S.setText("");
        	txt_E.setText("");
        	txt_W.setText("");
        }
	}
	
	@Inject
	@Optional
	void selectedSector(@UIEventTopic(CacheManagerEventConstants.DOWNLOADABLE_SIZE_UPDATED) long s) {
		long total = bulkManager.getTotalDownloadSize();
		String totals = CacheUtil.makeSizeDescription(total);
		lbl_totalValue.setText( totals);
		
		boolean ok = total < bulkManager.getFreeSpace();
		String msg = ok ? "" : "Not enough available space";
		//display a warning if not enough space
		lbl_message.setText(msg);
		//update layout
		this.layout(true);
		//enable (or not) download button
		btn_startDownload.setEnabled(ok);
	}

}
