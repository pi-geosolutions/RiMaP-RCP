package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class SendMessageDialog extends Dialog {
	private String msg_title, msg_content;
	protected Label lblCommunicationChannel;
	protected Label lblSms;
	protected Group grpMessage;
	protected Label lblTitle;
	protected Text text;
	protected Label lblContent;
	protected Text text_1;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SendMessageDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE); 
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		lblCommunicationChannel = new Label(container, SWT.NONE);
		lblCommunicationChannel.setText("Communication channel: ");
		
		lblSms = new Label(container, SWT.NONE);
		lblSms.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		lblSms.setText("SMS");
		
		grpMessage = new Group(container, SWT.NONE);
		grpMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpMessage.setText("Message");
		grpMessage.setLayout(new GridLayout(2, false));
		
		lblTitle = new Label(grpMessage, SWT.NONE);
		lblTitle.setText("Title: ");
		
		text = new Text(grpMessage, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblContent = new Label(grpMessage, SWT.NONE);
		lblContent.setText("Content:");
		
		text_1 = new Text(grpMessage, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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
		return new Point(450, 300);
	}

	public String getMessageTitle() {
		return this.msg_title;
	}
	public String getMessageContent() {
		return this.msg_content;
	}

	@Override
	protected void okPressed() {
		this.msg_title = text.getText();
		this.msg_content = text_1.getText();
		super.okPressed();
	}
}
