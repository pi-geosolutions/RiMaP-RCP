package fr.pigeo.rimap.rimaprcp.core.ui.animations;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapEventConstants;
import fr.pigeo.rimap.rimaprcp.core.ui.animations.AnimationsExtent.InvalidExtentException;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class AnimationsDialog extends Dialog {
	@Inject
	@Translation
	Messages messages;

	@Inject
	WwjInstance wwj;

	@Inject
	AnimationsController controller;

	protected DataBindingContext ctx;

	private LocalResourceManager resManager;
	protected Label txtSelectedLayer;
	protected Label lblSelectedLayer;
	protected Label lblSelectExtent;
	protected Button btnView;
	protected Button btnFullExtent;
	protected Label lblExtentType;
	protected Label lblSelectResolution;
	protected Combo resolutionCombo;
	private ComboViewer resolutionComboViewer;
	protected Label lblLoadImages;
	protected Button btnLoadImages;
	protected ProgressBar progressBar;
	protected Button btnX;
	protected Label lblLoading;
	protected Composite playerComposite;
	protected Scale playerScale;
	protected Label playerLblDate;
	protected Text playerTxtDate;
	protected Composite playerButtonsComposite;
	protected Button playerBtnLast, playerBtnFirst, playerBtnPrev, playerBtnBPlay, playerBtnPause, playerBtnFPlay, playerBtnNext;
	protected Composite composite_1;
	protected ControlDecoration controlDecoration;

	public AnimationsDialog(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		FormLayout fl_container = new FormLayout();
		fl_container.spacing = 5;
		fl_container.marginWidth = 5;
		fl_container.marginTop = 5;
		container.setLayout(fl_container);

		txtSelectedLayer = new Label(container, SWT.NONE);
		txtSelectedLayer.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		txtSelectedLayer.setText(controller.getModel()
				.getName());
		FormData fd_txtSelectedLayer = new FormData();
		fd_txtSelectedLayer.top = new FormAttachment(0);
		fd_txtSelectedLayer.right = new FormAttachment(100, -10);
		txtSelectedLayer.setLayoutData(fd_txtSelectedLayer);

		lblSelectedLayer = new Label(container, SWT.NONE);
		lblSelectedLayer.setFont(SWTResourceManager.getFont("Sans", 10, SWT.ITALIC));
		lblSelectedLayer.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblSelectedLayer = new FormData();
		fd_lblSelectedLayer.right = new FormAttachment(txtSelectedLayer);
		fd_lblSelectedLayer.bottom = new FormAttachment(txtSelectedLayer, 0, SWT.BOTTOM);
		lblSelectedLayer.setLayoutData(fd_lblSelectedLayer);
		lblSelectedLayer.setText(messages.animations_dialog_lbl_layername);

		lblSelectExtent = new Label(container, SWT.NONE);
		FormData fd_lblSelectExtent = new FormData();
		fd_lblSelectExtent.top = new FormAttachment(0, 40);
		fd_lblSelectExtent.left = new FormAttachment(0, 10);
		lblSelectExtent.setLayoutData(fd_lblSelectExtent);
		lblSelectExtent.setText(messages.animations_dialog_lbl_extent);

		btnView = new Button(container, SWT.NONE);
		btnView.addSelectionListener(new ExtentSelectionListener(this, messages.ANIM_EXTENT_VIEW));
		FormData fd_btnView = new FormData();
		fd_btnView.top = new FormAttachment(lblSelectExtent, -5, SWT.TOP);
		fd_btnView.right = new FormAttachment(100);
		btnView.setLayoutData(fd_btnView);
		btnView.setText(messages.animations_dialog_extent_viewextent);
		btnView.setToolTipText(messages.animations_dialog_extent_viewextent_ttip);

		btnFullExtent = new Button(container, SWT.NONE);
		btnFullExtent.addSelectionListener(new ExtentSelectionListener(this, messages.ANIM_EXTENT_FULL));
		FormData fd_btnFullExtent = new FormData();
		fd_btnFullExtent.right = new FormAttachment(btnView);
		fd_btnFullExtent.bottom = new FormAttachment(btnView, 0, SWT.BOTTOM);
		btnFullExtent.setLayoutData(fd_btnFullExtent);
		btnFullExtent.setText(messages.animations_dialog_extent_fullextent);
		btnFullExtent.setToolTipText(messages.animations_dialog_extent_fullextent_ttip);

		lblExtentType = new Label(container, SWT.NONE);
		lblExtentType.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblExtentType = new FormData();
		fd_lblExtentType.right = new FormAttachment(btnFullExtent, -5);
		fd_lblExtentType.left = new FormAttachment(lblSelectExtent);
		fd_lblExtentType.bottom = new FormAttachment(lblSelectExtent, 0, SWT.BOTTOM);
		lblExtentType.setLayoutData(fd_lblExtentType);
		lblExtentType.setText(messages.ANIM_EXTENT_UNDEFINED);

		lblSelectResolution = new Label(container, SWT.NONE);
		FormData fd_lblSelectResolution = new FormData();
		fd_lblSelectResolution.top = new FormAttachment(lblSelectExtent, 15);
		fd_lblSelectResolution.left = new FormAttachment(lblSelectExtent, 0, SWT.LEFT);
		lblSelectResolution.setLayoutData(fd_lblSelectResolution);
		lblSelectResolution.setText(messages.animations_dialog_lbl_resolution);

		controlDecoration = new ControlDecoration(lblSelectResolution, SWT.RIGHT | SWT.TOP);
		controlDecoration.setDescriptionText(messages.animations_dialog_lbl_resolution_ttip);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
	            FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoration.setImage(fieldDecoration.getImage());

		resolutionComboViewer = new ComboViewer(container, SWT.NONE);
		resolutionCombo = resolutionComboViewer.getCombo();
		resolutionCombo.addSelectionListener(new ResolutionComboSelectionListener());
		FormData fd_resolutionCombo = new FormData();
		fd_resolutionCombo.top = new FormAttachment(lblSelectResolution, -5, SWT.TOP);
		fd_resolutionCombo.left = new FormAttachment(lblSelectResolution, 30);
		fd_resolutionCombo.right = new FormAttachment(100);
		resolutionCombo.setLayoutData(fd_resolutionCombo);
		// initialize resolutionCombo
		resolutionComboViewer.setContentProvider(new ObservableListContentProvider());
		resolutionComboViewer.setInput(controller.getModel()
				.getResolutionsList());
		resolutionComboViewer.setSelection(new StructuredSelection(controller.getModel()
				.getResolution()));
		// resolutionCombo.select(1);
		// controller.getModel().setResolution(resolutionCombo);

		lblLoadImages = new Label(container, SWT.NONE);
		FormData fd_lblLoadImages = new FormData();
		fd_lblLoadImages.top = new FormAttachment(lblSelectResolution, 15);
		fd_lblLoadImages.left = new FormAttachment(0, 10);
		lblLoadImages.setLayoutData(fd_lblLoadImages);
		lblLoadImages.setText("3.");

		btnLoadImages = new Button(container, SWT.NONE);
		btnLoadImages.setEnabled(controller.isExtentValid());
		btnLoadImages.addSelectionListener(new BtnLoadImagesSelectionListener());
		FormData fd_btnLoadImages = new FormData();
		fd_btnLoadImages.left = new FormAttachment(lblLoadImages);
		fd_btnLoadImages.top = new FormAttachment(lblLoadImages, -5, SWT.TOP);
		btnLoadImages.setLayoutData(fd_btnLoadImages);
		btnLoadImages.setText("Load images");

		btnX = new Button(container, SWT.NONE);
		btnX.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		btnX.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		btnX.addSelectionListener(new BtnXSelectionListener());
		FormData fd_btnX = new FormData();
		fd_btnX.bottom = new FormAttachment(btnLoadImages, 0, SWT.BOTTOM);
		fd_btnX.width = 20;
		fd_btnX.right = new FormAttachment(100);
		btnX.setLayoutData(fd_btnX);
		btnX.setText("X");

		progressBar = new ProgressBar(container, SWT.NONE);
		FormData fd_progressBar = new FormData();
		fd_progressBar.right = new FormAttachment(btnX);
		fd_progressBar.left = new FormAttachment(btnLoadImages);
		fd_progressBar.bottom = new FormAttachment(lblLoadImages, 0, SWT.BOTTOM);
		fd_progressBar.top = new FormAttachment(lblLoadImages, -20);
		progressBar.setLayoutData(fd_progressBar);
		progressBar.setMaximum(controller.getModel()
				.getTimestamps().length);

		lblLoading = new Label(container, SWT.NONE);
		lblLoading.setAlignment(SWT.CENTER);
		lblLoading.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblLoading = new FormData();
		fd_lblLoading.right = new FormAttachment(progressBar, -10, SWT.RIGHT);
		fd_lblLoading.left = new FormAttachment(progressBar, 10, SWT.LEFT);
		fd_lblLoading.top = new FormAttachment(progressBar, 0, SWT.DEFAULT);
		lblLoading.setLayoutData(fd_lblLoading);

		playerComposite = new Composite(container, SWT.NONE);
		playerComposite.setEnabled(false);
		playerComposite.setLayout(new GridLayout(2, false));
		FormData fd_playerComposite = new FormData();
		fd_playerComposite.top = new FormAttachment(lblLoading, 10);
		fd_playerComposite.bottom = new FormAttachment(100, -10);
		fd_playerComposite.right = new FormAttachment(btnView, 0, SWT.RIGHT);
		fd_playerComposite.left = new FormAttachment(0, 10);
		playerComposite.setLayoutData(fd_playerComposite);

		playerScale = new Scale(playerComposite, SWT.NONE);
		playerScale.addSelectionListener(new PlayerScaleSelectionListener());
		playerScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		playerScale.setSelection(100);

		playerLblDate = new Label(playerComposite, SWT.NONE);
		GridData gd_playerLblDate = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_playerLblDate.widthHint = 100;
		playerLblDate.setLayoutData(gd_playerLblDate);
		playerLblDate.setText("Date:");

		playerTxtDate = new Text(playerComposite, SWT.BORDER);
		playerTxtDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		playerButtonsComposite = new Composite(playerComposite, SWT.NONE);
		playerButtonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		playerButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		playerBtnFirst = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnFirst.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-first.png"));
		playerBtnFirst.setToolTipText(messages.animations_dialog_btn_first_ttip);

		playerBtnPrev = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnPrev.addSelectionListener(new PlayerBtnPrevSelectionListener());
		playerBtnPrev.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-fb.png"));
		playerBtnPrev.setToolTipText(messages.animations_dialog_btn_prev_ttip);

		playerBtnBPlay = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnBPlay.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-b.png"));
		playerBtnBPlay.setToolTipText(messages.animations_dialog_btn_playbackward_ttip);

		playerBtnPause = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnPause.setToolTipText(messages.animations_dialog_btn_pause_ttip);
		playerBtnPause.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/stop.png"));

		playerBtnFPlay = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnFPlay.setSelection(true);
		playerBtnFPlay.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play.png"));
		playerBtnFPlay.setToolTipText(messages.animations_dialog_btn_playforward_ttip);

		playerBtnNext = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnNext.addSelectionListener(new PlayerBtnNextSelectionListener());
		playerBtnNext.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-ff.png"));
		playerBtnNext.setToolTipText(messages.animations_dialog_btn_next_ttip);

		playerBtnLast = new Button(playerButtonsComposite, SWT.NONE);
		playerBtnLast.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-last.png"));
		playerBtnLast.setToolTipText(messages.animations_dialog_btn_last_ttip);

		defineBindings();

		controller.init();

		return container;
	}

	private void defineBindings() {
		// define DataBindingContext as field
		if (ctx != null) {
			ctx.dispose();
		}
		ctx = new DataBindingContext();
		// layer name
		IObservableValue oWidgetLayername = WidgetProperties.text()
				.observe(txtSelectedLayer);
		IObservableValue oAnimLayername = BeanProperties.value(AnimationsModel.FIELD_NAME)
				.observe(controller.getModel());
		ctx.bindValue(oWidgetLayername, oAnimLayername);

		// extent type
		IObservableValue oWidgetExtenttype = WidgetProperties.text()
				.observe(lblExtentType);
		IObservableValue oAnimExtenttype = BeanProperties.value(AnimationsModel.FIELD_EXTENTTYPE)
				.observe(controller.getModel());
		ctx.bindValue(oWidgetExtenttype, oAnimExtenttype);

		// not tested
		// final IObservableValue oComboWidgetResolutions = ViewersObservables
		// .observeSingleSelection(resolutionComboViewer);
		// final IObservableValue oAnimModelResolutions =
		// BeanProperties.value(AnimationsModel.FIELD_RESOLUTIONS)
		// .observe(controller.getModel());
		// ctx.bindValue(oComboWidgetResolutions, oAnimModelResolutions);
		

		// extent type
		IObservableValue oWidgetCurrentdate = WidgetProperties.text()
				.observe(playerTxtDate);
		IObservableValue oAnimCurrentdate = BeanProperties.value(AnimationsModel.FIELD_CURRENTDATE)
				.observe(controller.getModel());
		ctx.bindValue(oWidgetCurrentdate, oAnimCurrentdate);

	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Animations");
		newShell.setAlpha(200);
		newShell.setImage(this.getAnImage("clock_play.png", newShell));

		// newShell.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui",
		// "icons/clock_play.png"));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, messages.CLOSE_LABEL, true);
		createButton(parent, IDialogConstants.OK_ID, messages.STOPANDCLOSE_LABEL, true);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(570, 476);
	}

	protected Image getAnImage(String file, Control owner) {
		if (resManager == null) {
			// create the manager and bind to a widget
			resManager = new LocalResourceManager(JFaceResources.getResources(), owner);
		}

		Bundle bundle = FrameworkUtil.getBundle(AnimationsDialog.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return resManager.createImage(image);
	}

	private class ExtentSelectionListener extends SelectionAdapter {
		String extentType = messages.ANIM_EXTENT_UNDEFINED;
		Dialog parentDialog;

		public ExtentSelectionListener(Dialog parentDialog, String extent_type) {
			this.extentType = extent_type;
			this.parentDialog = parentDialog;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				controller.setExtentType(this.extentType);
			} catch (InvalidExtentException e1) {
				MessageBox warn = new MessageBox(parentDialog.getShell(), SWT.ICON_ERROR | SWT.OK);
				warn.setText(messages.animations_extent_invalid);
				warn.setMessage(e1.getLocalizedMessage());
				warn.open();
			}
			if (controller.isExtentValid()) {
				btnLoadImages.setEnabled(true);
			} else {
				btnLoadImages.setEnabled(false);
			}
		}
	}

	private class BtnLoadImagesSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			lblLoading.setText(messages.loading);
			progressBar.setMaximum(controller.getModel()
					.getTimestamps().length);
			controller.preloadImages();
		}
	}

	private class BtnXSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			controller.cancelPreloadJob();
		}
	}

	private class ResolutionComboSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			IStructuredSelection selection = resolutionComboViewer.getStructuredSelection();
			String res = (String) selection.getFirstElement();
			controller.getModel()
					.setResolution(res);
			System.out.println("current resolution is " + res);
		}
	}
	private class PlayerBtnPrevSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			controller.showPrevImage();
			playerScale.setSelection(controller.getModel().getCurrentDateIndex());
		}
	}
	private class PlayerBtnNextSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			controller.showNextImage();
			playerScale.setSelection(controller.getModel().getCurrentDateIndex());
		}
	}
	private class PlayerScaleSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			controller.showImage(playerScale.getSelection());
		}
	}

	@Inject
	@Optional
	void filesLoadProgress(@UIEventTopic(RimapEventConstants.ANIMATIONS_FILES_LOAD_PROGRESS) int count) {
		progressBar.setSelection(count);
	}

	@Inject
	@Optional
	void filesLoadComplete(@UIEventTopic(RimapEventConstants.ANIMATIONS_FILES_LOAD_COMPLETE) String[] ts) {
		lblLoading.setText(messages.animations_dialog_progressbar_ttip_ready);
		playerComposite.setEnabled(true);
		controller.initPlayer();
		playerScale.setMaximum(controller.getModel().getTimestamps().length-1);
		playerScale.setSelection(controller.getModel().getCurrentDateIndex());
		
		
		// compositeControls.setVisible(true);

		// configure the scale bar
		// int max = ds.getFilenames()
		// .size() - 1;
		// scale.setMaximum(max);
		// scale.setSelection(max);
		// updateDate(ds, max);
		// animations.showImage(ds, max);
		// btnLoad.setText(messages.animations_dialog_update);
	}

	@Override
	public boolean close() {
		controller.cleanup();
		return super.close();
	}

	public void resetUI() {
		try { // controls may not exist, although they should
			this.progressBar.setSelection(0);
			this.progressBar.setMaximum(10);
			this.lblLoading.setText("");
			playerComposite.setEnabled(false);
		} catch (Exception ex) {
		}
	}
}
