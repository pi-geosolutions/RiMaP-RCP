
package fr.pigeo.rimap.rimaprcp.swt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.swt.bindings.ScaleToOpacityConverter;
import gov.nasa.worldwind.layers.Layer;

public class LayerDetails {
	protected DataBindingContext m_bindingContext;
	protected Layer layer;
	
	protected Label lblNoLayer, lblLayerName,lblLayerDescription,lblOpacity;
	protected Scale scaleOpacity;
	protected Group grpDetails;
	protected ScrolledComposite scrolledCompositeDescriptionContainer;
	protected Button btnShowMetadata, btnShowLegend;

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

		lblNoLayer = new Label(grpDetails, SWT.WRAP);
		GridData gd_lblNoLayer = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_lblNoLayer.exclude = true;
		gd_lblNoLayer.widthHint = 350;
		lblNoLayer.setLayoutData(gd_lblNoLayer);
		lblNoLayer.setText("No Layer selected. Please select a layer to view its related details.");

		lblLayerName = new Label(grpDetails, SWT.WRAP);
		GridData gd_lblLayerName = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gd_lblLayerName.minimumWidth = 200;
		lblLayerName.setLayoutData(gd_lblLayerName);
		lblLayerName.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		lblLayerName.setText("Layer name");

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
		new Label(grpDetails, SWT.NONE);
		new Label(grpDetails, SWT.NONE);

		scrolledCompositeDescriptionContainer = new ScrolledComposite(grpDetails, SWT.BORDER | SWT.V_SCROLL);
		scrolledCompositeDescriptionContainer.setExpandHorizontal(true);
		scrolledCompositeDescriptionContainer.setExpandVertical(true);
		scrolledCompositeDescriptionContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		scrolledCompositeDescriptionContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrolledCompositeDescriptionContainer.getClientArea();
				scrolledCompositeDescriptionContainer.setMinSize(grpDetails.computeSize(r.width, SWT.DEFAULT));
			}
		});

		lblLayerDescription = new Label(scrolledCompositeDescriptionContainer, SWT.WRAP);
		lblLayerDescription.setFont(SWTResourceManager.getFont("Sans", 8, SWT.ITALIC));
		lblLayerDescription.setText(
				"R É S U M É\n\n1ÈRE PARTIE : PRINCIPES DIRECTEURS\n\nIntroduction\n\nObjectifs et structure\n\nLa rédaction de directives pour des technologies et ap-proches de gestion durable des terres en Afrique subsaha-rienne (ASS) fait partie du programme TerrAfrica de 2009- 2010. L’objectif de ces recommandations et études de cas est de contribuer à créer une cadre pour les investis-sements liés aux pratiques de gestion durable des terres (GDT). Le but est, en particulier, d’identifier, d’analyser, de discuter et de diffuser des pratiques de GDT prometteuses – incluant à la fois les technologies et les approches – à la lumière des dernières tendances et nouvelles opportuni-tés. L’étude cible surtout les pratiques qui produisent des résultats et un retour sur investissement rapides et / ou les autres facteurs qui incitent à l’adoption de ces pratiques.\n\nCe document s’adresse aux parties-prenantes clés des programmes et projets de GDT aux stades de l’élaboration et de la mise en oeuvre : il s’agit surtout des praticiens, des gestionnaires, des décideurs, des planificateurs, en collaboration avec les institutions financières et tech¬niques et les donateurs. Les directives sont divisées en deux parties principales. La 1ère partie met en lumière les grands principes de la GDT ainsi que les éléments impor-tants à prendre en compte qui permettront de qualifier les technologies et approches de « bonnes pratiques » pour une transposition à grande échelle. La 2ème partie pré-sente douze groupes de technologies de GDT ainsi qu’un module sur les approches de GDT. Celles-ci sont illus¬trées par des études de cas spécifiques. Les principales personnes ressources et experts en GDT en ASS ont été sollicités afin de finaliser les groupes de GDT et de décrire les études de cas spécifiques. Ce produit s’efforce d’être à la pointe de la recherche.\n\nFocus sur la gestion durable des terres en Afrique subsaharienne");
		scrolledCompositeDescriptionContainer.setContent(lblLayerDescription);
		scrolledCompositeDescriptionContainer.setMinSize(lblLayerDescription.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		btnShowMetadata = new Button(grpDetails, SWT.NONE);
		btnShowMetadata
				.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp", "icons/icon_featureinfo_16px.png"));
		btnShowMetadata.setToolTipText("Show the associated metadata (information sheet about the data)");
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