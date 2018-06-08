package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;

public class FillSMSGatewayCredentials extends Dialog {
	protected Label lblForNowThis;
	protected Label lblServiceName;
	protected Label lblAkKey;
	protected Label lblAsKey;
	protected Label lblCkKey;
	protected Text txtServiceName;
	protected Text txtAK;
	protected Text txtAS;
	protected Text txtCK;
	protected Label lblPhoneNumbers;
	protected Text txtPhones;
	protected String service, AK, AS, CK, phones;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FillSMSGatewayCredentials(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		lblForNowThis = new Label(container, SWT.WRAP);
		lblForNowThis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblForNowThis.setText("For now, this is only a prototype using an OVH SMS account to send SMS. This is a paid service and thus should not be abused. Please note it will not send the message to the collected list, but to the contacts provided below (for demo purpose). To avoid abuse, SMS account credentials are not included and you will have to provide them here.");
		
		lblServiceName = new Label(container, SWT.NONE);
		lblServiceName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServiceName.setText("Service name");
		
		txtServiceName = new Text(container, SWT.BORDER);
		txtServiceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblAkKey = new Label(container, SWT.NONE);
		lblAkKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAkKey.setText("AK key");
		
		txtAK = new Text(container, SWT.BORDER);
		txtAK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblAsKey = new Label(container, SWT.NONE);
		lblAsKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAsKey.setText("AS Key");
		
		txtAS = new Text(container, SWT.BORDER);
		txtAS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblCkKey = new Label(container, SWT.NONE);
		lblCkKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCkKey.setText("CK Key");
		
		txtCK = new Text(container, SWT.BORDER);
		txtCK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblPhoneNumbers = new Label(container, SWT.NONE);
		lblPhoneNumbers.setToolTipText("comma separated list of international phone numbers (prefixed by 00+country code)");
		lblPhoneNumbers.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPhoneNumbers.setText("phone numbers");
		
		txtPhones = new Text(container, SWT.BORDER);
		txtPhones.setToolTipText("comma separated list of international phone numbers (prefixed by 00+country code)");
		txtPhones.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Send", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 500);
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		this.service = this.txtServiceName.getText();
		this.AK = this.txtAK.getText();
		this.AS = this.txtAS.getText();
		this.CK = this.txtCK.getText();
		this.phones = this.txtPhones.getText();
		super.okPressed();
	}

	public String getService() {
		return service;
	}

	public String getAK() {
		return AK;
	}

	public String getAS() {
		return AS;
	}

	public String getCK() {
		return CK;
	}

	public String getPhones() {
		return phones;
	}

}
