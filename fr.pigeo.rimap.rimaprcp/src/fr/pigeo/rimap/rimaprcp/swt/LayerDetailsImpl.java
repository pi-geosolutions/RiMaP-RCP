package fr.pigeo.rimap.rimaprcp.swt;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.riskcatalog.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.WmsLayer;
import fr.pigeo.rimap.rimaprcp.swt.bindings.LayerOpacityChangeListener;
import fr.pigeo.rimap.rimaprcp.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.swt.bindings.ScaleToOpacityConverter;
import gov.nasa.worldwind.layers.Layer;

public class LayerDetailsImpl extends LayerDetails {
	private String isNoLayer = "No Layer selected. Please select a layer to view its related details.";

	public LayerDetailsImpl() {
		
	}

	/*
	 * Override necessary with complete reuse of the original databindings declarations. If not, the last part (update the view 
	 * when the opacity changes) won't work. Don't know why
	 * TODO : find why, lighten the code.
	 * @see fr.pigeo.rimap.rimaprcp.swt.LayerDetails#initDataBindings()
	 */
	@Override
	protected DataBindingContext initDataBindings() {

		this.setVisibleComponents();
		if (this.layer==null)
			return null;

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
		
		//needed for instant apply of the opacity change, in the WWJ window
		opacityLayerObserveValue.addValueChangeListener(new LayerOpacityChangeListener(layer));
		return bindingContext;
	}
	
	@Inject
	@Optional
	private void subscribeLayerSelected(@UIEventTopic(RimaprcpConstants.LAYER_SELECTED) Layer layer) {
		//System.out.println("Selected layer "+layer.getName());
		//System.out.println("    Opacity "+layer.getOpacity());
		this.layer = layer;
		if (this.m_bindingContext!=null)
			this.m_bindingContext.dispose();
		this.setVisibleComponents();
		m_bindingContext = initDataBindings();
	}

	private void setVisibleComponents() {
		boolean isLayer = (layer instanceof Layer);
		boolean isRimapLayer= (layer instanceof RimapWMSTiledImageLayer);

		if (!isLayer) {
			this.lblLayerName.setText(isNoLayer);
			this.lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.ITALIC));
		} else {
			this.lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		}
		this.lblOpacity.setVisible(isLayer);
		this.scaleOpacity.setVisible(isLayer);
		
		this.scrolledCompositeDescriptionContainer.setVisible(isRimapLayer);
		this.btnShowMetadata.setVisible(isRimapLayer);
		this.btnShowLegend.setVisible(isRimapLayer);
		
		if (isRimapLayer) {
			RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) layer;
			WmsLayer wms = l.getParent();
			if (wms.getComments()!="") 
				this.lblLayerDescription.setText(wms.getComments());
			this.scrolledCompositeDescriptionContainer.layout();

			this.btnShowMetadata.setEnabled(wms.getMetadata_uuid()!="");
			this.btnShowLegend.setEnabled(wms.getLegendurl()!="");
		}

		//recompute the composite's layout. Needed for proper update of the scrolledComposite's content (layer description)
		this.scaleOpacity.getParent().layout();
		
	}

}
