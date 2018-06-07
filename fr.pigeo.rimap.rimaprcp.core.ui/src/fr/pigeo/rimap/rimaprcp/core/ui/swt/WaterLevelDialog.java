package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;

public class WaterLevelDialog extends Dialog {
	private IEventBroker eventBroker;

	protected Composite composite1;
	protected Button btnDrawSector;
	protected Button btnFlood;
	protected Label lblWaterHeight;
	protected Spinner heightSpinner;
	protected Label lblElevation;

	@Inject
	@Translation
	Messages messages;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public WaterLevelDialog(Shell parentShell, IEventBroker evt) {
		super(parentShell);
		this.eventBroker = evt;
		setShellStyle(SWT.RESIZE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		composite1 = new Composite(container, SWT.NONE);
		composite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite1 = new GridLayout(3, false);
		gl_composite1.verticalSpacing = 20;
		gl_composite1.horizontalSpacing = 15;
		composite1.setLayout(gl_composite1);

		btnDrawSector = new Button(composite1, SWT.NONE);
		GridData gd_btnDrawSector = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnDrawSector.heightHint = 40;
		btnDrawSector.setLayoutData(gd_btnDrawSector);
		btnDrawSector.setText(messages.flood_dialog_btn_drawsector_label);
		btnDrawSector.setFocus();

		lblWaterHeight = new Label(composite1, SWT.WRAP);
		lblWaterHeight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		lblWaterHeight.setText(messages.flood_dialog_lbl_waterheight_label);

		btnFlood = new Button(composite1, SWT.NONE);
		GridData gd_btnFlood = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnFlood.heightHint = 40;
		btnFlood.setLayoutData(gd_btnFlood);
		btnFlood.setEnabled(false);
		btnFlood.setText(messages.flood_dialog_btn_flood_label);

		lblElevation = new Label(composite1, SWT.NONE);
		lblElevation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblElevation.setAlignment(SWT.CENTER);
		lblElevation.setText(messages.flood_dialog_txt_waterheight_label);

		heightSpinner = new Spinner(composite1, SWT.BORDER);
		heightSpinner.setPageIncrement(100);
		heightSpinner.setDigits(1);
		heightSpinner.setToolTipText(messages.flood_dialog_spinner_height_tooltip);
		heightSpinner.setMaximum(5000);
		GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner.minimumWidth = 50;
		gd_spinner.widthHint = 50;
		heightSpinner.setLayoutData(gd_spinner);

		createListeners();
		return container;
	}

	protected void createListeners() {
		SelectionAdapter drawSectorSelectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				eventBroker.send(RiMaPEventConstants.WATER_SECTORSELECTOR_DRAW, null);
			}

		};
		btnDrawSector.addSelectionListener(drawSectorSelectionAdapter);

		SelectionAdapter floodAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				eventBroker.send(RiMaPEventConstants.WATER_DIALOG_FLOOD, null);
				btnDrawSector.setEnabled(false);
				heightSpinner.setFocus();
			}

		};
		btnFlood.addSelectionListener(floodAdapter);

		SelectionAdapter waterHeightAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				double height = 0f;
				if (e.getSource() instanceof Spinner) {
					Spinner spinner = (Spinner) e.getSource();
					height = spinner.getSelection() / Math.pow(10, spinner.getDigits());
				}
				eventBroker.send(RiMaPEventConstants.WATER_HEIGHT_CHANGED, height);
			}

		};
		heightSpinner.addSelectionListener(waterHeightAdapter);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, messages.flood_dialog_close_label, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 280);
	}

	@Override
	protected void okPressed() {
		this.removeSubscribers();
		this.eventBroker.send(RiMaPEventConstants.WATER_DIALOG_CLOSED, null);
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		this.removeSubscribers();
		this.eventBroker.send(RiMaPEventConstants.WATER_DIALOG_CLOSED, null);
		super.cancelPressed();
	}

	void addSubscribers() {
		this.eventBroker.subscribe(RiMaPEventConstants.WATER_SECTORSELECTOR_DRAWN, activateFloodBtnHandler);
	}

	void removeSubscribers() {
		eventBroker.unsubscribe(activateFloodBtnHandler);
	}

	private EventHandler activateFloodBtnHandler = new EventHandler() {
		public void handleEvent(Event event) {
			btnFlood.setEnabled(true);
			btnFlood.setFocus();
		}
	};
	//
	// private org.osgi.service.event.EventHandler closeHandler = new
	// EventHandler() {
	// public void handleEvent(Event event) {
	//
	// // Useful work that has access
	// foo.Bar payload = (foo.Bar) event.getProperty(IEventBroker.DATA);
	// }
	// };

	@Override
	public int open() {
		this.addSubscribers();
		return super.open();
	}
}
