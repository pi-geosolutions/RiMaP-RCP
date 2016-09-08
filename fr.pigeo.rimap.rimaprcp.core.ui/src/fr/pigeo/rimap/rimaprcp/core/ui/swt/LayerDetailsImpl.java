package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.WmsNode;
import fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings.LayerOpacityChangeListener;
import fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.core.ui.swt.bindings.ScaleToOpacityConverter;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class LayerDetailsImpl extends LayerDetails {
	private SelectionAdapter btnMetadataSelectionAdapter, btnExtentSelectionAdapter, btnLegendSelectionAdapter;

	private WmsNode wmsNode;
	private WwjInstance wwj;
	private Composite parent;
	LayerLegendDialog legendDialog;

	@Inject
	@Translation
	private Messages messages;

	@Inject
	IPreferencesService prefService;

	@Inject
	IEclipseContext context;

	public LayerDetailsImpl() {

	}

	@PostConstruct
	public void getInjections(Composite parent, WwjInstance wwjInst) {
		this.wwj = wwjInst;
		this.parent = parent;

		grpDetails.setText(messages.parts_layerdetails_title); // $NON-NLS-1$
		// lblLayerName.setText(messages.parts_layerdetails_layername);
		// //$NON-NLS-1$
		btnZoomToExtent.setToolTipText(messages.parts_layerdetails_zoomtoextent); // $NON-NLS-1$
		lblOpacity.setText(messages.parts_layerdetails_opacity); // $NON-NLS-1$
		lblDescription.setText(messages.parts_layerdetails_description); // $NON-NLS-1$
		btnShowMetadata.setToolTipText(messages.parts_layerdetails_showmetadata_tooltip); // $NON-NLS-1$
		btnShowMetadata.setText(messages.parts_layerdetails_showmetadata); // $NON-NLS-1$
		btnShowLegend.setToolTipText(messages.parts_layerdetails_showlegend_tooltip); // $NON-NLS-1$
		btnShowLegend.setText(messages.parts_layerdetails_showlegend); // $NON-NLS-1$
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
		IObservableValue observeTextLblNewLabel_1ObserveWidget = WidgetProperties.text()
				.observe(lblLayerName);
		IObservableValue nameLayerObserveValue = PojoProperties.value("name")
				.observe(layer);
		bindingContext.bindValue(observeTextLblNewLabel_1ObserveWidget, nameLayerObserveValue, null, null);
		//
		IObservableValue observeSelectionScaleObserveWidget = WidgetProperties.selection()
				.observe(scaleOpacity);
		IObservableValue opacityLayerObserveValue = PojoProperties.value("opacity")
				.observe(layer);
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
	private void subscribeLayerSelected(@UIEventTopic(RiMaPEventConstants.LAYER_SELECTED) Layer layer) {
		// System.out.println("Selected layer "+layer.getName());
		// System.out.println(" Opacity "+layer.getOpacity());
		this.layer = layer;
		if (this.m_bindingContext != null)
			this.m_bindingContext.dispose();
		this.initComponents();
		// this.updateLegendShell(layer);
		m_bindingContext = initDataBindings();
	}

	private void initComponents() {
		boolean isLayer = (layer instanceof Layer);
		boolean isRimapLayer;
		if (layer != null && layer.hasKey(RimapAVKey.HAS_RIMAP_EXTENSIONS)) {
			isRimapLayer = (boolean) layer.getValue(RimapAVKey.HAS_RIMAP_EXTENSIONS);
		} else {
			isRimapLayer = false;
		}

		if (!isLayer) {
			this.lblLayerName.setText(messages.parts_layerdetails_isnolayer);
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
			final WMSTiledImageLayer l = (WMSTiledImageLayer) layer;
			wmsNode = (WmsNode) l.getValue(RimapAVKey.LAYER_PARENTNODE);
			this.txtLayerDescription.setText(wmsNode.getComments());

			this.btnShowMetadata.setEnabled(wmsNode.getMetadata_uuid() != "");
			// this.btnShowLegend.setEnabled(wms.getLegendurl() != "");

			// Show more button events
			if (this.btnMetadataSelectionAdapter != null)
				this.btnShowMetadata.removeSelectionListener(this.btnMetadataSelectionAdapter);
			if (wmsNode.getMetadata_uuid() != "") {
				this.btnMetadataSelectionAdapter = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String baseurl = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
								RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT,
								null);
						// TODO : use constants
						String mtdService = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
								RimapConstants.CATALOG_METADATA_BY_UUID_RELPATH_PREF_TAG,
								RimapConstants.CATALOG_METADATA_BY_UUID_PREF_DEFAULT, null);
						String link = baseurl + mtdService + wmsNode.getMetadata_uuid();
						Program.launch(link);
					}
				};
				this.btnShowMetadata.addSelectionListener(this.btnMetadataSelectionAdapter);
			}

			// Show extent button events
			if (this.btnExtentSelectionAdapter != null)
				this.btnZoomToExtent.removeSelectionListener(this.btnExtentSelectionAdapter);
			this.btnExtentSelectionAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (wwj != null) {
						// System.out.println("Zooming to extent");
						Sector sector = (Sector) l.getValue(AVKey.SECTOR);
						WorldWindowGLCanvas wwd = wwj.getWwd();
						Extent extent = Sector.computeBoundingCylinder(wwd.getModel()
								.getGlobe(),
								wwd.getSceneController()
										.getVerticalExaggeration(),
								sector);

						Angle fov = wwd.getView()
								.getFieldOfView();
						Position centerPos = new Position(sector.getCentroid(), 0d);
						double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

						wwd.getView()
								.goTo(centerPos, zoom);
					}
				}
			};
			this.btnZoomToExtent.addSelectionListener(this.btnExtentSelectionAdapter);

			// Show legend button events
			if (this.btnLegendSelectionAdapter != null)
				this.btnShowLegend.removeSelectionListener(this.btnLegendSelectionAdapter);
			this.btnLegendSelectionAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String legend_path = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
							RimapConstants.WMS_LEGEND_RELPATH_PREF_TAG, RimapConstants.WMS_LEGEND_RELPATH_PREF_DEFAULT,
							null);
					LayerLegendDialog dialog = new LayerLegendDialog(parent.getShell(), legend_path);
					ContextInjectionFactory.inject(dialog, context);
					dialog.setLayer(wmsNode);
					dialog.open();
				}
			};
			this.btnShowLegend.addSelectionListener(this.btnLegendSelectionAdapter);
			btnShowLegend.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
				}
			});
		}

	}

	/*
	 * Not used. Would be used in case we keep only one legend widget
	 * (this.legendDialog) and change its content when we select another layer.
	 * Poses the pb of what happens if the user closes the legend widget : it is
	 * then disposed and i didn't find how to deal with that.
	 */
	private void updateLegendShell(Layer l) {
		if (legendDialog == null) {
			System.out.println("Legend dialog is null");
			return;
		} else
			System.out.println("Legend dialog is " + legendDialog.toString());

		if (l instanceof WMSTiledImageLayer) {
			WmsNode wms = (WmsNode) l.getValue(RimapAVKey.LAYER_PARENTNODE);
			legendDialog.setLayer(wms);
		}

	}

}