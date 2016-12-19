package fr.pigeo.rimap.rimaprcp.cachemanager.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CacheUtil;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.ImportableCachePack;
import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.ImportableCachePacks;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class ImportPackageWizardPage1 extends WizardPage {
	private Table table;
	private Text fileName;
	private CheckboxTableViewer checkboxTableViewer;

	@Inject
	private ImportableCachePacks cachePacks;
	private Text txtTotalSize;
	private Text txtAvailableSpace;
	private Label lblWarningMessage;
	private long totalSize = -2, availableSpace = -1;

	private WwjInstance wwj;

	@Inject
	public ImportPackageWizardPage1(WwjInstance wwj) {
		super("Import Cache Packs, page 1");
		this.wwj = wwj;
		this.setPageComplete(false);
		this.setTitle("Import Packages");
		setDescription("Select and import pregenerated ZIP Cache Packs");
	}

	@Override
	public void createControl(Composite parent) {
		// parent.setLayout(new GridLayout());
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblSelectRoot = new Label(container, SWT.NONE);
		lblSelectRoot.setText("Select source directory:");
		new Label(container, SWT.NONE);

		fileName = new Text(container, SWT.BORDER);
		fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
			}
		});

		Button btnFile = new Button(container, SWT.NONE);
		GridData gd_btnFile = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnFile.widthHint = 50;
		btnFile.setLayoutData(gd_btnFile);
		btnFile.setText("...");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblCachePacks = new Label(container, SWT.NONE);
		lblCachePacks.setText("Cache Packs:");
		new Label(container, SWT.NONE);

		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkboxTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		table = checkboxTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));

		Button btnSelectAll = new Button(container, SWT.NONE);
		btnSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSelectAll.setText("Select All");

		Button btnDeselect = new Button(container, SWT.NONE);
		btnDeselect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnDeselect.setText("Deselect All");

		Button btnRefresh = new Button(container, SWT.NONE);
		btnRefresh.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRefresh.setText("Refresh");
		new Label(container, SWT.NONE);

		Group grpOptions = new Group(container, SWT.NONE);
		GridLayout gl_grpOptions = new GridLayout(1, false);
		grpOptions.setLayout(gl_grpOptions);
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpOptions.setText("Options");

		Button btnScanSubfolders = new Button(grpOptions, SWT.CHECK);
		btnScanSubfolders.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnScanSubfolders.setText("Scan subfolders");

		btnFile.addSelectionListener(
				new CachePathRootSelectionAdapter(fileName, checkboxTableViewer, cachePacks, btnScanSubfolders));
		btnRefresh.addSelectionListener(
				new RefreshListSelectionAdapter(checkboxTableViewer, cachePacks, btnScanSubfolders));
		btnSelectAll.addSelectionListener(new SelectAllSelectionAdapter(checkboxTableViewer, true));
		btnDeselect.addSelectionListener(new SelectAllSelectionAdapter(checkboxTableViewer, false));
		btnScanSubfolders.addSelectionListener(
				new RefreshListSelectionAdapter(checkboxTableViewer, cachePacks, btnScanSubfolders));
		checkboxTableViewer.addCheckStateListener(new SelectionNotEmptyCheckStateListener());
		createColumns(checkboxTableViewer);

		lblWarningMessage = new Label(container, SWT.WRAP | SWT.RIGHT);
		lblWarningMessage.setFont(SWTResourceManager.getFont("Sans", 8, SWT.BOLD | SWT.ITALIC));
		lblWarningMessage.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblWarningMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		lblWarningMessage.setText(" ");

		Label lblTotalSize = new Label(container, SWT.NONE);
		lblTotalSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTotalSize.setText("Total size");

		txtTotalSize = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtTotalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblAvailableSpace = new Label(container, SWT.NONE);
		lblAvailableSpace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAvailableSpace.setText("Available Space");

		txtAvailableSpace = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		txtAvailableSpace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		updateAvailableSpaceField();
	}

	private void createColumns(CheckboxTableViewer viewer) {
		// create column for path property
		TableViewerColumn col = createTableViewerColumn(viewer, "Path", 400, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ImportableCachePack pack = (ImportableCachePack) element;
				String p = pack.getPath()
						.toString();
				return p;
			}
		});
		// create column for name property
		col = createTableViewerColumn(viewer, "Name", 300, 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ImportableCachePack pack = (ImportableCachePack) element;
				String n = pack.getName();
				return n;
			}
		});
		// create column for name property
		col = createTableViewerColumn(viewer, "Size (zip)", 150, 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ImportableCachePack pack = (ImportableCachePack) element;
				long size = pack.getZipFileSize();
				return (size > 0 ? CacheUtil.makeSizeDescription(size) : "");
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(CheckboxTableViewer viewer, String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		// column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	@Override
	public boolean canFlipToNextPage() {
		if (this.totalSize >= this.availableSpace) {
			this.lblWarningMessage.setText("Not enough space available on the disk hosting the application's cache");
			this.lblWarningMessage.getParent()
					.layout();
		} else {
			this.lblWarningMessage.setText(" ");
		}
		return (this.totalSize < this.availableSpace && this.totalSize > 0);
		// return (checkboxTableViewer!=null &&
		// checkboxTableViewer.getCheckedElements().length>0);
	}

	@Override
	public IWizardPage getNextPage() {
		Object[] selected = checkboxTableViewer.getCheckedElements();
		List<ImportableCachePack> checkedCachePacksList = new ArrayList();
		for (Object sel : selected) {
			ImportableCachePack pack = (ImportableCachePack) sel;
			checkedCachePacksList.add(pack);
		}
		this.cachePacks.setCheckedCachePacksList(checkedCachePacksList);
		return super.getNextPage();
	}

	private void computeTotalSize() {
		Object[] selected = checkboxTableViewer.getCheckedElements();
		long total = 0;
		for (Object sel : selected) {
			ImportableCachePack pack = (ImportableCachePack) sel;
			total += pack.getZipFileSize();
		}
		this.totalSize = total;
	}

	private void computeAvailableSpace() {
		this.availableSpace = cachePacks.computeAvailableSpaceOnCacheFileStore();
	}

	private void updateTotalSizeField() {
		if (this.totalSize < 0) {
			computeTotalSize();
		}
		txtTotalSize.setText(CacheUtil.makeSizeDescription(this.totalSize));
	}

	private void updateAvailableSpaceField() {
		if (this.availableSpace < 0) {
			this.computeAvailableSpace();
		}
		txtAvailableSpace.setText(CacheUtil.makeSizeDescription(this.availableSpace));
	}

	private void updateSizesAndButtons() {
		computeTotalSize();
		updateTotalSizeField();
		computeAvailableSpace();
		updateAvailableSpaceField();
		getWizard().getContainer()
				.updateButtons();
	}

	private class CachePathRootSelectionAdapter extends SelectionAdapter {
		private Text txtBox;
		private CheckboxTableViewer viewer;
		private ImportableCachePacks cachePacks;
		private Button scanSubfolders;

		public CachePathRootSelectionAdapter(Text txtBox, CheckboxTableViewer viewer, ImportableCachePacks cachePacks,
				Button scanSubfolders) {
			super();
			this.txtBox = txtBox;
			this.viewer = viewer;
			this.cachePacks = cachePacks;
			this.scanSubfolders = scanSubfolders;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			// User has selected to save a file
			DirectoryDialog dlg = new DirectoryDialog(getShell());
			String fn = dlg.open();
			if (fn != null) {
				txtBox.setText(fn);
				cachePacks.setPath(fn);
				viewer.setInput(cachePacks.getList(scanSubfolders.getSelection()));
			}
		}
	}

	private class RefreshListSelectionAdapter extends SelectionAdapter {
		private CheckboxTableViewer viewer;
		private ImportableCachePacks cachePacks;
		private Button scanSubfolders;

		public RefreshListSelectionAdapter(CheckboxTableViewer viewer, ImportableCachePacks cachePacks,
				Button scanSubfolders) {
			super();
			this.viewer = viewer;
			this.cachePacks = cachePacks;
			this.scanSubfolders = scanSubfolders;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			Button btn = (Button) event.getSource();
			this.viewer.setInput(this.cachePacks.getList(scanSubfolders.getSelection()));// getList
																							// is
																							// always
																							// refreshed
			updateSizesAndButtons();
		}
	}

	private class SelectAllSelectionAdapter extends SelectionAdapter {
		private CheckboxTableViewer viewer;
		private boolean check;

		public SelectAllSelectionAdapter(CheckboxTableViewer viewer, boolean check) {
			super();
			this.viewer = viewer;
			this.check = check;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			this.viewer.setAllChecked(this.check);
			updateSizesAndButtons();
		}
	}

	private class SelectionNotEmptyCheckStateListener implements ICheckStateListener {
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			updateSizesAndButtons();
		}
	}

}
