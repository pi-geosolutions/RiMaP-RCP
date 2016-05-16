package dialogs;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import fr.pigeo.rimap.rimaprcp.core.services.session.internal.Messages;

public class LoginDialog extends Dialog {
	private Text txtUser;
	private Text txtPassword;
	private String user = "";
	private String password = "";
	private Image bgImage;

	@Inject
	@Translation
	Messages messages;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		bgImage = this.getBgImage("fr.pigeo.rimap.rimaprcp.core.ui", "loginBackground.png");
		container.setBackgroundImage(bgImage);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblUser = new Label(container, SWT.NONE);
		lblUser.setText(messages.loginDialogUserLbl);

		txtUser = new Text(container, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtUser.setText(user);
		txtUser.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textWidget = (Text) e.getSource();
				String userText = textWidget.getText();
				user = userText;
			}
		});

		Label lblPassword = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.horizontalIndent = 1;
		lblPassword.setLayoutData(gd_lblNewLabel);
		lblPassword.setText(messages.loginDialogPwdLbl);

		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassword.setText(password);
		txtPassword.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textWidget = (Text) e.getSource();
				String passwordText = textWidget.getText();
				password = passwordText;
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
		createButton(parent, IDialogConstants.OK_ID, messages.loginDialogOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, messages.loginDialogCancel, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
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

	private Image getBgImage(String bundlename, String path) {
		Bundle bundle = Platform.getBundle(bundlename);
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