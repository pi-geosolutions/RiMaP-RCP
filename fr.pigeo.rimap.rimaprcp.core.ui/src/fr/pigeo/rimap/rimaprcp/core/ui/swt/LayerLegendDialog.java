
package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.catalog.INode;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.WmsNode;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;

public class LayerLegendDialog extends Dialog {
	private DataBindingContext m_bindingContext;
	private INode node;
	private Label lblLayerName, imgLabel;
	private ScrolledComposite scImageComposite;
	private Composite container;
	private Display display;
	private String layerName="";
	private Image imgLegend=null;
	private ImageData imgLegendData;
	private String legendPath;
	
	@Inject
	@Translation
	private Messages messages;

	public LayerLegendDialog(Shell parentShell, String path) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		setBlockOnOpen(false);
		this.legendPath=path;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		
				lblLayerName = new Label(container, SWT.NONE);
				lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
				lblLayerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
				lblLayerName.setText(this.layerName);
		// The scrolled composite
		scImageComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scImageComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scImageComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
				imgLabel = new Label(scImageComposite, SWT.NONE);
				imgLabel.setImage(imgLegend);
				imgLabel.setSize(imgLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				scImageComposite.setContent(imgLabel);

		// Load an image
		// ImageData imgData = new ImageData( "your image path" );
		this.display = parent.getDisplay();
		/*if (this.imgLegendData==null)
			imgLegendData = new ImageData("/home/jean/tmp/shutter_Sélection_003.png");
		imgLegend = new Image(this.display, imgLegendData);*/

		this.updateDisplay();
		
		m_bindingContext = initDataBindings();

		return container;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(messages.dialog_legend_title);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 300);
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
		setBlockOnOpen(false);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Close", true);
	}

	public void setLayer(INode wms) {
		this.node = wms;
		this.layerName = wms.getName();
		//System.out.println("AbstractLayername : " + wms.getName());
		
		this.updateDisplay();
		/*Image legend = this.getLegendImage();*/
		//this.imgLegendData = new ImageData("/home/jean/tmp/shutter_Sélection_014.png");
		//this.imgLegend = new Image(this.getShell().getDisplay(), imgData);
		/*if (legend != null) {
			if (this.imgLabel != null && this.scImageComposite != null) {
				this.imgLabel.setImage(legend);
				this.scImageComposite.pack();
			}
			getShell().pack();
		}*/
	}
	
	//updates the display according to the current layer
	private void updateDisplay() {
		if (this.container !=null) {
			this.lblLayerName.setText(this.node.getName());
			this.imgLegend = this.getLegendImage();
			if (this.imgLegend!=null) {
				this.imgLabel.setImage(this.imgLegend);
				imgLabel.setSize(imgLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				//this.scImageComposite.pack();
			}
		}
	}

	private URL getLegendURL() throws MalformedURLException {
		String path = "";
		URL url = null;
		if (node instanceof WmsNode) {
			WmsNode wms = (WmsNode) node;
			path = wms.getLegendurl();
			if (path == "") { // then we will get the default geoserver legend
				path = wms.getUrl() + legendPath + wms.getLayers();
			}
			System.out.println("legend path : " + path);
			url = new URL(path);
		}
		return url;

	}

	private Image getLegendImage() {
		Image img = null;
		try {
			URL url = getLegendURL();
			if (url != null && this.container!=null)
				img = new Image(this.container.getDisplay(), url.openStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return img;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblLayerNameObserveWidget = WidgetProperties.text().observe(lblLayerName);
		IObservableValue nameLayerObserveValue = PojoProperties.value("name").observe(node);
		bindingContext.bindValue(observeTextLblLayerNameObserveWidget, nameLayerObserveValue, null, null);
		//
		return bindingContext;
	}
}