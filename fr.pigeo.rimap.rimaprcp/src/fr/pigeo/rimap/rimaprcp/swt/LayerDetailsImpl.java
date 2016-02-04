package fr.pigeo.rimap.rimaprcp.swt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.catalog.CatalogProperties;
import fr.pigeo.rimap.rimaprcp.riskcatalog.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.WmsLayer;
import fr.pigeo.rimap.rimaprcp.swt.bindings.LayerOpacityChangeListener;
import fr.pigeo.rimap.rimaprcp.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.swt.bindings.ScaleToOpacityConverter;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;

public class LayerDetailsImpl extends LayerDetails {
	private String isNoLayer = "No Layer selected. Please select a layer to view its related details.";
	private SelectionAdapter btnMetadataSelectionAdapter, btnExtentSelectionAdapter;
	
	private WmsLayer wms;
	private WwjInstance wwj;

	public LayerDetailsImpl() {

	}
	

	@PostConstruct
	public void getInjections(Composite parent, WwjInstance wwjInst) {
		this.wwj = wwjInst;
	}

	/*
	 * Override necessary with complete reuse of the original databindings
	 * declarations. If not, the last part (update the view when the opacity
	 * changes) won't work. Don't know why TODO : find why, lighten the code.
	 * 
	 * @see fr.pigeo.rimap.rimaprcp.swt.LayerDetails#initDataBindings()
	 */
	@Override
	protected DataBindingContext initDataBindings() {

		this.initComponents();
		if (this.layer == null)
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

		// needed for instant apply of the opacity change, in the WWJ window
		opacityLayerObserveValue.addValueChangeListener(new LayerOpacityChangeListener(layer));
		return bindingContext;
	}

	@Inject
	@Optional
	private void subscribeLayerSelected(@UIEventTopic(RimaprcpConstants.LAYER_SELECTED) Layer layer) {
		// System.out.println("Selected layer "+layer.getName());
		// System.out.println(" Opacity "+layer.getOpacity());
		this.layer = layer;
		if (this.m_bindingContext != null)
			this.m_bindingContext.dispose();
		this.initComponents();
		m_bindingContext = initDataBindings();
	}

	private void initComponents() {
		boolean isLayer = (layer instanceof Layer);
		boolean isRimapLayer = (layer instanceof RimapWMSTiledImageLayer);

		if (!isLayer) {
			this.lblLayerName.setText(isNoLayer);
			this.lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.ITALIC));
		} else {
			this.lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
			
		}
		this.btnZoomToExtent.setVisible(isLayer);
		this.lblOpacity.setVisible(isLayer);
		this.scaleOpacity.setVisible(isLayer);

		this.lblDescription.setVisible(isRimapLayer);
		this.txtLayerDescription.setVisible(isRimapLayer);
		this.btnShowMetadata.setVisible(isRimapLayer);
		this.btnShowLegend.setVisible(isRimapLayer);
		
		

		if (isRimapLayer) {
			final RimapWMSTiledImageLayer l = (RimapWMSTiledImageLayer) layer;
			wms = l.getParent();
			this.txtLayerDescription.setText(wms.getComments());

			this.btnShowMetadata.setEnabled(wms.getMetadata_uuid() != "");
			this.btnShowLegend.setEnabled(wms.getLegendurl() != "");

			//Show more button events
			if (this.btnMetadataSelectionAdapter != null)
				this.btnShowMetadata.removeSelectionListener(this.btnMetadataSelectionAdapter);
			this.btnMetadataSelectionAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String link = CatalogProperties.getProperty("catalog.baseurl")+CatalogProperties.getProperty("catalog.metadata_relpath")+wms.getMetadata_uuid();
					Program.launch(link);
				}
			};
			this.btnShowMetadata.addSelectionListener(this.btnMetadataSelectionAdapter);
			
			//Show extent button events
			if (this.btnExtentSelectionAdapter != null)
				this.btnZoomToExtent.removeSelectionListener(this.btnExtentSelectionAdapter);
			this.btnExtentSelectionAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println(l.getRestorableState());
					if (wwj!=null) {
						System.out.println("Zooming to extent");
						Sector sector = (Sector) l.getValue(AVKey.SECTOR);
						WorldWindowGLCanvas wwd = wwj.getWwd();
						Extent extent = Sector.computeBoundingCylinder(wwd.getModel().getGlobe(),
				                wwd.getSceneController().getVerticalExaggeration(), sector);

				            Angle fov = wwd.getView().getFieldOfView();
				            Position centerPos = new Position(sector.getCentroid(), 0d);
				            double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

				            wwd.getView().goTo(centerPos, zoom);
					} else 
						System.out.println("TODO : zoom to extent");
				}
			};

			this.btnZoomToExtent.addSelectionListener(this.btnExtentSelectionAdapter);
		}

	}

}