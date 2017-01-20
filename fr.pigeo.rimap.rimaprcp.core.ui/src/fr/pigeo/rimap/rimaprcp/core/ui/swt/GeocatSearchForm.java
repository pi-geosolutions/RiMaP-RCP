package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public class GeocatSearchForm extends Composite {
	private Text text;

	public GeocatSearchForm(Composite parent, int style) {
		super(parent, style);
		
		createControls();	
	}
	
	public void createControls() {
		this.setLayout(new FormLayout());
		
		text = new Text(this, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(0, 56);
		fd_text.right = new FormAttachment(0, 225);
		text.setLayoutData(fd_text);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/find.png"));
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(100, -10);
		fd_btnNewButton.right = new FormAttachment(100, -10);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Search");
		
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/cross.png"));
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.top = new FormAttachment(btnNewButton, 0, SWT.TOP);
		fd_btnNewButton_1.right = new FormAttachment(btnNewButton, -6);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("Reset");
		
		Label lblSearch = new Label(this, SWT.NONE);
		fd_text.top = new FormAttachment(lblSearch, 6);
		fd_text.left = new FormAttachment(lblSearch, 0, SWT.LEFT);
		FormData fd_lblSearch = new FormData();
		fd_lblSearch.left = new FormAttachment(0, 10);
		fd_lblSearch.top = new FormAttachment(0, 10);
		lblSearch.setLayoutData(fd_lblSearch);
		lblSearch.setText("Search:");
		
	}
}
