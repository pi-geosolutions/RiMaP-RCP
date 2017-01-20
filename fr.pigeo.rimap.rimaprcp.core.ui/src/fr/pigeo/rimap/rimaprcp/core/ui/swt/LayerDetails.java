
package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings.ScaleToOpacityConverter;
import gov.nasa.worldwind.layers.Layer;

public class LayerDetails {
	protected DataBindingContext m_bindingContext;
	protected Layer layer;

	protected Label lblLayerName, lblOpacity, lblDescription;
	protected Scale scaleOpacity;
	protected Group grpDetails;
	protected Button btnShowMetadata, btnShowLegend;
	protected Text txtLayerDescription;
	protected Button btnZoomToExtent;

	@Inject
	public LayerDetails() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		parent.setLayout(new GridLayout(1, false));

		grpDetails = new Group(parent, SWT.NONE);
		grpDetails.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		grpDetails.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		grpDetails.setText("Layer details");
		grpDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDetails.setLayout(new GridLayout(2, false));

		lblLayerName = new Label(grpDetails, SWT.WRAP);
		lblLayerName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		lblLayerName.setText("Layer name");

		btnZoomToExtent = new Button(grpDetails, SWT.NONE);

		btnZoomToExtent.setToolTipText("Zoom to layer's extent");
		btnZoomToExtent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnZoomToExtent.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/icon_zoomlayer.png"));

		lblOpacity = new Label(grpDetails, SWT.NONE);
		lblOpacity.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblOpacity.setText("Opacity:");
		new Label(grpDetails, SWT.NONE);

		scaleOpacity = new Scale(grpDetails, SWT.NONE);
		scaleOpacity.setSelection(100);
		scaleOpacity.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		scaleOpacity.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				scaleOpacity.setToolTipText(String.valueOf(scaleOpacity.getSelection()) + "%");

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		lblDescription = new Label(grpDetails, SWT.NONE);
		lblDescription.setText("Description:");
		new Label(grpDetails, SWT.NONE);

		txtLayerDescription = new Text(grpDetails, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtLayerDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		txtLayerDescription.setText(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus quis fringilla massa, at ullamcorper neque. Integer scelerisque malesuada leo, eget tincidunt ante interdum sed. Morbi non purus vitae sapien fringilla semper in eget velit. Nulla volutpat arcu sed pulvinar aliquet. Praesent lectus nisi, iaculis at commodo sed, auctor eget ipsum. Nulla imperdiet lacus eget libero fermentum, quis placerat metus hendrerit. Nam laoreet lectus eget massa tempor, nec convallis massa sagittis. Curabitur egestas condimentum condimentum. Maecenas porta purus et fermentum gravida. Sed sed velit metus. Sed pretium efficitur arcu ut cursus. Curabitur ornare dolor nec felis fermentum, in eleifend orci maximus. Fusce libero libero, consectetur id finibus et, tristique eu lectus. Quisque justo sapien, rutrum faucibus turpis a, gravida lobortis elit. Vestibulum at metus non turpis aliquam iaculis. ");
		txtLayerDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		btnShowMetadata = new Button(grpDetails, SWT.NONE);
		btnShowMetadata.setImage(
				ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/icon_metadata_16px.png"));
		btnShowMetadata.setToolTipText(
				"Show the associated metadata (information sheet about the data)\nOpens a window in your favorite browser.");
		btnShowMetadata.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnShowMetadata.setText(" - Show more");

		btnShowLegend = new Button(grpDetails, SWT.NONE);
		GridData gd_btnShowLegend = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnShowLegend.heightHint = 34;
		btnShowLegend.setLayoutData(gd_btnShowLegend);
		btnShowLegend.setToolTipText("Show the associated metadata (information sheet about the data)");
		btnShowLegend.setText("Show legend");
		btnShowLegend.setImage(null);
		m_bindingContext = initDataBindings();

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblNewLabel_1ObserveWidget = WidgetProperties.text().observe(lblLayerName);
		IObservableValue nameLayerObserveValue = PojoProperties.value("name").observe(layer);
		bindingContext.bindValue(observeTextLblNewLabel_1ObserveWidget, nameLayerObserveValue, null, null);
		//
		IObservableValue observeSelectionScaleObserveWidget = WidgetProperties.selection().observe(scaleOpacity);
		IObservableValue opacityLayerObserveValue = PojoProperties.value("opacity").observe(layer);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new ScaleToOpacityConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new OpacityToScaleConverter());
		bindingContext.bindValue(observeSelectionScaleObserveWidget, opacityLayerObserveValue, strategy, strategy_1);
		//
		return bindingContext;
	}
}