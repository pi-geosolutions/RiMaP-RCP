package fr.pigeo.rimap.rimaprcp.swt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.swt.bindings.LayerOpacityChangeListener;
import fr.pigeo.rimap.rimaprcp.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.swt.bindings.ScaleToOpacityConverter;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;

public class LayerDetailsImpl extends LayerDetails {

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
		if (this.layer==null)
			return null;

		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLblNewLabel_1ObserveWidget = WidgetProperties.text().observe(lblNewLabel_1);
		IObservableValue nameLayerObserveValue = PojoProperties.value("name").observe(layer);
		bindingContext.bindValue(observeTextLblNewLabel_1ObserveWidget, nameLayerObserveValue, null, null);
		//
		IObservableValue observeSelectionScaleObserveWidget = WidgetProperties.selection().observe(scale);
		IObservableValue opacityLayerObserveValue = PojoProperties.value("opacity").observe(layer);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new ScaleToOpacityConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new OpacityToScaleConverter());
		bindingContext.bindValue(observeSelectionScaleObserveWidget, opacityLayerObserveValue, strategy, strategy_1);
		//
		IObservableValue observeTextTxtLayerNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtLayerName);
		bindingContext.bindValue(observeTextTxtLayerNameObserveWidget, nameLayerObserveValue, null, null);
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
		m_bindingContext = initDataBindings();
	}

}
