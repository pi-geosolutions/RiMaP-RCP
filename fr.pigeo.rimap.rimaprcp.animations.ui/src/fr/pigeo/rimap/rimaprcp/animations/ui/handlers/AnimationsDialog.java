package fr.pigeo.rimap.rimaprcp.animations.ui.handlers;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.animations.constants.AnimationsEventConstants;
import fr.pigeo.rimap.rimaprcp.animations.core.Animations;
import fr.pigeo.rimap.rimaprcp.animations.core.AnimationsSource;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;

public class AnimationsDialog extends Dialog {
	@Inject
	Animations animations;

	@Inject
	IEventBroker eventBroker;

	private LocalResourceManager resManager;
	private Text txtDate;
	private Button btnLoad;
	private ComboViewer comboViewer;
	private ProgressBar progressBar;
	private Composite compositeControls, compositeButtons;
	private Scale scale;
	private Label lblDate;
	private Button buttonLast, btnFirst, btnPrev, btnBPlay, buttonPause, buttonFPlay, buttonNext;
	private int loopDirection = 0;

	private AnimationsSource currentDataset = null;

	protected AnimationsDialog(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AnimationsSource) {
					AnimationsSource source = (AnimationsSource) element;
					return source.getLabel();
				}
				return super.getText(element);
			}
		});
		comboViewer.setInput(animations.getSources()
				.toArray());
		comboViewer.addSelectionChangedListener(this.getComboSelectionChangeListener());
		final ISelection selection = new StructuredSelection(animations.getSources()
				.get(0));
		comboViewer.setSelection(selection, true);
		// combo.select(0);

		btnLoad = new Button(container, SWT.NONE);
		// btnLoad.setEnabled(false);
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLoad.setText("   Load   ");
		btnLoad.addSelectionListener(this.getLoadButtonSelectionListener());

		progressBar = new ProgressBar(container, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		progressBar.setToolTipText("Please choose a dataset first...");
		progressBar.setSize(200, 20);
		// progressBar.setVisible(false);

		compositeControls = new Composite(container, SWT.NONE);
		compositeControls.setLayout(new GridLayout(2, false));
		compositeControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeControls.setVisible(false);

		scale = new Scale(compositeControls, SWT.NONE);
		scale.setSelection(100);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		lblDate = new Label(compositeControls, SWT.NONE);
		GridData gd_lblDate = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDate.widthHint = 100;
		lblDate.setLayoutData(gd_lblDate);
		lblDate.setText("Date:");

		txtDate = new Text(compositeControls, SWT.BORDER);
		txtDate.setText("");
		txtDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtDate.setEditable(false);

		compositeButtons = new Composite(compositeControls, SWT.NONE);
		compositeButtons.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnFirst = new Button(compositeButtons, SWT.NONE);
		btnFirst.setText("|<");

		btnPrev = new Button(compositeButtons, SWT.NONE);
		btnPrev.setText("<");

		btnBPlay = new Button(compositeButtons, SWT.NONE);
		btnBPlay.setText("b");

		buttonPause = new Button(compositeButtons, SWT.NONE);
		buttonPause.setText("||");

		buttonFPlay = new Button(compositeButtons, SWT.NONE);
		buttonFPlay.setSelection(true);
		buttonFPlay.setText("f");

		buttonNext = new Button(compositeButtons, SWT.NONE);
		buttonNext.setText(">");

		buttonLast = new Button(compositeButtons, SWT.NONE);
		buttonLast.setText(">|");

		addListeners();

		return container;
	}

	private void addListeners() {
		SelectionAdapter scaleSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentDataset == null) {
					return;
				}
				eventBroker.send(AnimationsEventConstants.ANIMATIONS_SELECTED_DATE_CHANGED, scale.getSelection());

			}
		};
		scale.addSelectionListener(scaleSelectionListener);
		/*
		 * //To get noticed only when the mouse is released
		 * scale.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override
		 * public void mouseUp(MouseEvent e) {
		 * if (currentDataset == null) {
		 * return;
		 * }
		 * eventBroker.send(AnimationsEventConstants.
		 * ANIMATIONS_SELECTED_DATE_CHANGED, scale.getSelection());
		 * }
		 * 
		 * });
		 */

		SelectionListener btnFirstSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loopDirection = 0;
				int index = scale.getMinimum();
				scale.setSelection(index);
				// setSelection does not trigger the scale's listener, hence we
				// have to send the event here too
				eventBroker.send(AnimationsEventConstants.ANIMATIONS_SELECTED_DATE_CHANGED, index);
			}
		};
		btnFirst.addSelectionListener(btnFirstSelectionListener);
		SelectionListener btnLastSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loopDirection = 0;
				int index = scale.getMaximum();
				scale.setSelection(index);
				// setSelection does not trigger the scale's listener, hence we
				// have to send the event here too
				eventBroker.send(AnimationsEventConstants.ANIMATIONS_SELECTED_DATE_CHANGED, index);
			}
		};
		buttonLast.addSelectionListener(btnLastSelectionListener);
		SelectionAdapter lbtnPrevSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				increment(-1, false);
			}
		};
		btnPrev.addSelectionListener(lbtnPrevSelectionListener);
		SelectionAdapter btnBPlaySelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loopDirection = -1;
				increment(-1, true);
			}
		};
		btnBPlay.addSelectionListener(btnBPlaySelectionListener);
		SelectionAdapter btnPauseSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				increment(0, false);
			}
		};
		buttonPause.addSelectionListener(btnPauseSelectionListener);
		SelectionAdapter btnFPlaySelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loopDirection = 1;
				increment(1, true);
			}
		};
		buttonFPlay.addSelectionListener(btnFPlaySelectionListener);
		SelectionAdapter btnNextSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				increment(1, false);
			}
		};
		buttonNext.addSelectionListener(btnNextSelectionListener);

	}

	private void increment(final int step, final boolean loop) {
		int index = scale.getSelection() + step;
		int interval = scale.getMaximum() - scale.getMinimum();
		if (index > scale.getMaximum()) {
			index -= interval;
		} else if (index < scale.getMinimum()) {
			index += interval;
		}

		// System.out.println(scale.getMinimum() + " / " + index + " / " +
		// scale.getMaximum());

		scale.setSelection(index);
		// setSelection does not trigger the scale's listener, hence we have to
		// send the event here too
		eventBroker.send(AnimationsEventConstants.ANIMATIONS_SELECTED_DATE_CHANGED, index);

		if (loop) {
			this.getShell()
					.getDisplay()
					.timerExec(1000, new Runnable() {
						public void run() {
							if (loopDirection * step > 0) {
								// means go the same direction
								increment(step, true);
							} /*
								 * else if (loopDirection*step < 0) {
								 * //means go the opposite direction => we
								 * cancel this loop
								 * return;
								 * } else {
								 * //means one of them is =0
								 * return;
								 * }
								 */
							else {
								// means either the direction changed of we
								// stopped animating. In both cases, we should
								// stop this loop
								return;
							}
						}
					});
		} else {
			loopDirection = 0;
		}
	}

	private SelectionListener getLoadButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				progressBar.setToolTipText("Loading, please wait...");
				progressBar.setVisible(true);
				if (currentDataset != null && animations != null) {
					animations.loadDataset(currentDataset);
				}
			}

		};
	}

	private ISelectionChangedListener getComboSelectionChangeListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0 && ((selection.getFirstElement()) instanceof AnimationsSource)) {
					AnimationsSource src = (AnimationsSource) selection.getFirstElement();
					currentDataset = src;
					/*
					 * if (animations != null) {
					 * animations.setCurrentDataset(src);
					 * //btnLoad.setEnabled(true);
					 * }
					 */
					reset();

					// System.out.println(src.toString());
				}
			}
		};
	}

	// We do not dispose the dialog : it would dispose the contained widgets and
	// generate an error if we try to open it again
	@Override
	public boolean close() {
		this.getShell()
				.setVisible(false);
		return true;
	}

	/**
	 * Resets the widgets' state
	 */
	private void reset() {
		if (btnLoad != null) {
			btnLoad.setText("   Load   ");
		}
		if (progressBar != null) {
			// progressBar.setToolTipText("Please choose a dataset first...");
			progressBar.setVisible(false);
			progressBar.setSelection(0);
		}
		if (compositeControls != null) {
			compositeControls.setVisible(false);
		}
		if (scale != null) {
			scale.setSelection(100);
		}
		if (txtDate != null) {
			txtDate.setText("");
		}
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
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(499, 311);
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

	/*
	 * returns true if txtData needed an update (i.e. input != from its previous
	 * value
	 */
	protected boolean updateDate(AnimationsSource ds, int index) {
		String filename = ds.getFilenames()
				.get(index);
		String timestamp = filename.replaceAll(ds.getTimestampRegexMatch(), ds.getTimestampRegexFormat());
		if (txtDate.getText()
				.equals(timestamp)) {
			return false;
		} else {
			txtDate.setText(timestamp);
			return true;
		}
	}

	@Inject
	@Optional
	void animationsSourceConfigured(
			@UIEventTopic(AnimationsEventConstants.ANIMATIONS_DATASET_CONFIGURED) AnimationsSource ds) {
		progressBar.setMaximum(ds.getFilenames()
				.size());
	}

	@Inject
	@Optional
	void filesLoadProgress(@UIEventTopic(AnimationsEventConstants.ANIMATIONS_FILES_LOAD_PROGRESS) int count) {
		progressBar.setSelection(count);
	}

	@Inject
	@Optional
	void filesLoadComplete(@UIEventTopic(AnimationsEventConstants.ANIMATIONS_FILES_LOAD_COMPLETE) AnimationsSource ds) {
		progressBar.setToolTipText("Ready to play !");
		compositeControls.setVisible(true);

		// configure the scale bar
		int max = ds.getFilenames()
				.size() - 1;
		scale.setMaximum(max);
		scale.setSelection(max);
		updateDate(ds, max);
		animations.showImage(ds, max);
		btnLoad.setText("Update");
	}

	@Inject
	@Optional
	void selectedDateChanged(@UIEventTopic(AnimationsEventConstants.ANIMATIONS_SELECTED_DATE_CHANGED) int scaleIndex) {
		if (updateDate(currentDataset, scaleIndex)) {
			animations.showImage(currentDataset, scaleIndex);
		}
	}

}
