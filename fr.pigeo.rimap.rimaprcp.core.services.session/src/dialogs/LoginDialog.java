package dialogs;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.services.session.internal.Messages;

public class LoginDialog extends Dialog {
	private Text txtUser;
	private Text txtPassword;
	private String user = "";
	private String password = "";
	private Image bgImage;
	private Button btnOK, btnCancel;

	@Inject
	@Translation
	Messages messages;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		bgImage = this.getBgImage("loginBackground.png");
		container.setBackgroundImage(bgImage);
		container.setLayout(new FormLayout());

		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		FormData fd_txtPassword = new FormData();
		fd_txtPassword.bottom = new FormAttachment(100, -10);
		fd_txtPassword.right = new FormAttachment(100, -10);
		fd_txtPassword.width = 200;
		txtPassword.setLayoutData(fd_txtPassword);
		txtPassword.setText(password);
		txtPassword.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textWidget = (Text) e.getSource();
				String passwordText = textWidget.getText();
				password = passwordText;
				updateOKButton();
			}

		});

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FormData fd_lblPassword = new FormData();
		fd_lblPassword.bottom = new FormAttachment(txtPassword, 0, SWT.CENTER);
		fd_lblPassword.right = new FormAttachment(txtPassword, -10);
		lblPassword.setLayoutData(fd_lblPassword);
		lblPassword.setText(messages.loginDialogPwdLbl);

		txtUser = new Text(container, SWT.BORDER);
		FormData fd_txtUser = new FormData();
		fd_txtUser.bottom = new FormAttachment(txtPassword, -5);
		fd_txtUser.right = new FormAttachment(100, -10);
		fd_txtUser.width = 200;
		txtUser.setLayoutData(fd_txtUser);
		txtUser.setText(user);
		txtUser.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textWidget = (Text) e.getSource();
				String userText = textWidget.getText();
				user = userText;
				updateOKButton();
			}
		});

		Label lblUser = new Label(container, SWT.NONE);
		lblUser.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		FormData fd_lblUser = new FormData();
		fd_lblUser.bottom = new FormAttachment(txtUser, 0, SWT.CENTER);
		fd_lblUser.right = new FormAttachment(txtUser, -10);
		lblUser.setLayoutData(fd_lblUser);
		lblUser.setText(messages.loginDialogUserLbl);
		container.setTabList(new Control[] { txtUser, txtPassword });
		txtUser.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					System.out.println("canceled");
					cancelPressed();
				}
			}
		});
		return container;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(messages.loginDialogTitle);
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		btnOK = createButton(parent, IDialogConstants.OK_ID, messages.loginDialogOK, true);
		btnOK.setEnabled(false);
		btnCancel = createButton(parent, IDialogConstants.CANCEL_ID, messages.loginDialogCancel, false);
	}

	private void updateOKButton() {
		if (this.user.length() > 0 && this.password.length() > 0) {
			btnOK.setEnabled(true);
		} else {
			btnOK.setEnabled(false);
		}

	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	private Image getBgImage(String path) {
		Bundle bundle = FrameworkUtil.getBundle(LoginDialog.class);
		URL url = FileLocator.find(bundle, new Path(path), null);

		ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
		return imageDescr.createImage();
	}

	public void dispose() {
		// free resources such as background image
		bgImage.dispose();

		this.close();
	}
}