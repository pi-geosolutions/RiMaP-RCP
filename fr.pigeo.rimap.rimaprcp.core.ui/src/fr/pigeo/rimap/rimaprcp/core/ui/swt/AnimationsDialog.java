package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.ResourceManager;


public class AnimationsDialog extends Dialog {
	@Inject
	@Translation
	Messages messages;
	
	private LocalResourceManager resManager;
	protected Label txtSelectedLayer;
	protected Label lblSelectedLayer;
	protected Label lblSelectExtent;
	protected Button btnView;
	protected Button btnFullExtent;
	protected Label lblUndefined;
	protected Label lblSelectResolution;
	protected Combo combo;
	private ComboViewer comboViewer;
	protected Label lblLoadImages;
	protected Button btnLoadImages;
	protected ProgressBar progressBar;
	protected Button btnX;
	protected Label lblLoading;
	protected Composite composite;
	protected Scale scale;
	protected Label lblDate;
	protected Text text;
	protected Composite compositeButtons;
	protected Button buttonLast, btnFirst, btnPrev, btnBPlay, buttonPause, buttonFPlay, buttonNext;

	protected AnimationsDialog(Shell parentShell) {
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
		txtSelectedLayer.setText("no layer selected");
		FormData fd_txtSelectedLayer = new FormData();
		fd_txtSelectedLayer.top = new FormAttachment(0);
		fd_txtSelectedLayer.right = new FormAttachment(100, -10);
		txtSelectedLayer.setLayoutData(fd_txtSelectedLayer);
		
		lblSelectedLayer = new Label(container, SWT.NONE);
		lblSelectedLayer.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblSelectedLayer = new FormData();
		fd_lblSelectedLayer.right = new FormAttachment(txtSelectedLayer);
		fd_lblSelectedLayer.bottom = new FormAttachment(txtSelectedLayer, 0, SWT.BOTTOM);
		lblSelectedLayer.setLayoutData(fd_lblSelectedLayer);
		lblSelectedLayer.setText("Selected Layer: ");
		
		lblSelectExtent = new Label(container, SWT.NONE);
		FormData fd_lblSelectExtent = new FormData();
		fd_lblSelectExtent.top = new FormAttachment(0, 40);
		fd_lblSelectExtent.left = new FormAttachment(0, 10);
		lblSelectExtent.setLayoutData(fd_lblSelectExtent);
		lblSelectExtent.setText("1. Select extent: ");
		
		btnView = new Button(container, SWT.NONE);
		FormData fd_btnView = new FormData();
		fd_btnView.top = new FormAttachment(lblSelectExtent, -5, SWT.TOP);
		fd_btnView.right = new FormAttachment(100);
		btnView.setLayoutData(fd_btnView);
		btnView.setText("view");
		
		btnFullExtent = new Button(container, SWT.NONE);
		FormData fd_btnFullExtent = new FormData();
		fd_btnFullExtent.right = new FormAttachment(btnView);
		fd_btnFullExtent.bottom = new FormAttachment(btnView, 0, SWT.BOTTOM);
		btnFullExtent.setLayoutData(fd_btnFullExtent);
		btnFullExtent.setText("full extent");
		
		lblUndefined = new Label(container, SWT.NONE);
		lblUndefined.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblUndefined = new FormData();
		fd_lblUndefined.left = new FormAttachment(lblSelectExtent);
		fd_lblUndefined.bottom = new FormAttachment(lblSelectExtent, 0, SWT.BOTTOM);
		lblUndefined.setLayoutData(fd_lblUndefined);
		lblUndefined.setText("undefined");
		
		lblSelectResolution = new Label(container, SWT.NONE);
		FormData fd_lblSelectResolution = new FormData();
		fd_lblSelectResolution.top = new FormAttachment(lblSelectExtent, 15);
		fd_lblSelectResolution.left = new FormAttachment(lblSelectExtent, 0, SWT.LEFT);
		lblSelectResolution.setLayoutData(fd_lblSelectResolution);
		lblSelectResolution.setText("2. Select image resolution: ");
		
		comboViewer = new ComboViewer(container, SWT.NONE);
		combo = comboViewer.getCombo();
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(lblSelectResolution, -5, SWT.TOP);
		fd_combo.left = new FormAttachment(lblSelectResolution, 30);
		fd_combo.right = new FormAttachment(100);
		combo.setLayoutData(fd_combo);
		
		lblLoadImages = new Label(container, SWT.NONE);
		FormData fd_lblLoadImages = new FormData();
		fd_lblLoadImages.top = new FormAttachment(lblSelectResolution, 15);
		fd_lblLoadImages.left = new FormAttachment(0, 10);
		lblLoadImages.setLayoutData(fd_lblLoadImages);
		lblLoadImages.setText("3.");
		
		btnLoadImages = new Button(container, SWT.NONE);
		FormData fd_btnLoadImages = new FormData();
		fd_btnLoadImages.left = new FormAttachment(lblLoadImages);
		fd_btnLoadImages.top = new FormAttachment(lblLoadImages, -5, SWT.TOP);
		btnLoadImages.setLayoutData(fd_btnLoadImages);
		btnLoadImages.setText("Load images");
		
		btnX = new Button(container, SWT.NONE);
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
		
		lblLoading = new Label(container, SWT.NONE);
		lblLoading.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_lblLoading = new FormData();
		fd_lblLoading.top = new FormAttachment(progressBar, 0, SWT.DEFAULT);
		fd_lblLoading.left = new FormAttachment(btnLoadImages, 111);
		lblLoading.setLayoutData(fd_lblLoading);
		lblLoading.setText("loading...");
		
		composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(lblLoading, 10);
		fd_composite.right = new FormAttachment(btnView, 0, SWT.RIGHT);
		fd_composite.bottom = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		scale = new Scale(composite, SWT.NONE);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		scale.setSelection(100);
		
		lblDate = new Label(composite, SWT.NONE);
		GridData gd_lblDate = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDate.widthHint = 100;
		lblDate.setLayoutData(gd_lblDate);
		lblDate.setText("Date:");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnFirst = new Button(compositeButtons, SWT.NONE);
		btnFirst.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-first.png"));
		btnFirst.setToolTipText(messages.animations_dialog_btn_first_ttip);

		btnPrev = new Button(compositeButtons, SWT.NONE);
		btnPrev.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-fb.png"));
		btnPrev.setToolTipText(messages.animations_dialog_btn_prev_ttip);

		btnBPlay = new Button(compositeButtons, SWT.NONE);
		btnBPlay.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-b.png"));
		btnBPlay.setToolTipText(messages.animations_dialog_btn_playbackward_ttip);

		buttonPause = new Button(compositeButtons, SWT.NONE);
		buttonPause.setToolTipText(messages.animations_dialog_btn_pause_ttip);
		buttonPause.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/stop.png"));

		buttonFPlay = new Button(compositeButtons, SWT.NONE);
		buttonFPlay.setSelection(true);
		buttonFPlay.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play.png"));
		buttonFPlay.setToolTipText(messages.animations_dialog_btn_playforward_ttip);

		buttonNext = new Button(compositeButtons, SWT.NONE);
		buttonNext.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-ff.png"));
		buttonNext.setToolTipText(messages.animations_dialog_btn_next_ttip);

		buttonLast = new Button(compositeButtons, SWT.NONE);
		buttonLast.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/animations/play-last.png"));
		buttonLast.setToolTipText(messages.animations_dialog_btn_last_ttip);
		

		return container;
	}


	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Animations");
		newShell.setAlpha(200);
		newShell.setImage(this.getAnImage("clock_play.png", newShell));
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
}
