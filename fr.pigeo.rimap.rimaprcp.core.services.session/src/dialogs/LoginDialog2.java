package dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LoginDialog2 extends Shell {
	private Text txtLogin;
	private Text txtPasswd;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			LoginDialog2 shell = new LoginDialog2(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 * 
	 */
	public LoginDialog2(Display display) {
		//super(display, SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		super(display, SWT.RESIZE | SWT.APPLICATION_MODAL);
		setLayout(new FormLayout());
		
		Composite composite = new Composite(this, SWT.NONE);
		//composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 22);
		fd_composite.right = new FormAttachment(100, -70);
		fd_composite.bottom = new FormAttachment(100, -39);
		fd_composite.left = new FormAttachment(0, 73);
		composite.setLayoutData(fd_composite);
		
		Button btnOK = new Button(composite, SWT.NONE);
		btnOK.setBounds(211, 100, 56, 28);
		btnOK.setText("Log in");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(67, 100, 138, 28);
		btnCancel.setText("I don't have a login");
		
		Label lblLogin = new Label(composite, SWT.NONE);
		lblLogin.setLocation(108, 45);
		lblLogin.setSize(34, 15);
		lblLogin.setText("Login");
		
		txtLogin = new Text(composite, SWT.BORDER);
		txtLogin.setLocation(148, 32);
		txtLogin.setSize(119, 28);
		txtLogin.setText("");
		
		Label lblPasswd = new Label(composite, SWT.NONE);
		lblPasswd.setLocation(83, 79);
		lblPasswd.setSize(59, 15);
		lblPasswd.setText("Password");
		
		txtPasswd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPasswd.setLocation(148, 66);
		txtPasswd.setSize(119, 28);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 214, 19);
		//lblNewLabel.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		lblNewLabel.setText("Please identify yourself:");
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Login");
		setSize(431, 209);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
