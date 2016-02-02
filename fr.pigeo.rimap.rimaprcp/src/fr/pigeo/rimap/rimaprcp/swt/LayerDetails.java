
package fr.pigeo.rimap.rimaprcp.swt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
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

import fr.pigeo.rimap.rimaprcp.RimaprcpConstants;
import fr.pigeo.rimap.rimaprcp.swt.bindings.LayerOpacityChangeListener;
import fr.pigeo.rimap.rimaprcp.swt.bindings.OpacityToScaleConverter;
import fr.pigeo.rimap.rimaprcp.swt.bindings.ScaleToOpacityConverter;
import gov.nasa.worldwind.layers.Layer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.databinding.beans.PojoProperties;

public class LayerDetails {
	protected DataBindingContext m_bindingContext;
	protected Layer layer;
	protected Label lblNewLabel_1;
	protected Scale scale;
	protected Text txtLayerName;

	@Inject
	public LayerDetails() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		parent.setLayout(new GridLayout(1, false));

		final Group grpDetails = new Group(parent, SWT.NONE);
		grpDetails.setFont(SWTResourceManager.getFont("Sans", 12, SWT.BOLD));
		grpDetails.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		grpDetails.setText("Layer details");
		grpDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDetails.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(grpDetails, SWT.WRAP);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_lblNewLabel.widthHint = 350;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("No Layer selected. Please select a layer to view its related details.");

		lblNewLabel_1 = new Label(grpDetails, SWT.WRAP);
		GridData gd_lblNewLabel_1 = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gd_lblNewLabel_1.minimumWidth = 200;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		lblNewLabel_1.setText("Layer name");
		
		txtLayerName = new Text(grpDetails, SWT.BORDER);
		txtLayerName.setText("layer name");
		txtLayerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpDetails, SWT.NONE);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(grpDetails, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(grpDetails.computeSize(r.width, SWT.DEFAULT));
			}
		});

		Label lblNewLabel_2 = new Label(scrolledComposite, SWT.WRAP);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("Sans", 8, SWT.ITALIC));
		lblNewLabel_2.setText(
				"R É S U M É\n\n1ÈRE PARTIE : PRINCIPES DIRECTEURS\n\nIntroduction\n\nObjectifs et structure\n\nLa rédaction de directives pour des technologies et ap-proches de gestion durable des terres en Afrique subsaha-rienne (ASS) fait partie du programme TerrAfrica de 2009- 2010. L’objectif de ces recommandations et études de cas est de contribuer à créer une cadre pour les investis-sements liés aux pratiques de gestion durable des terres (GDT). Le but est, en particulier, d’identifier, d’analyser, de discuter et de diffuser des pratiques de GDT prometteuses – incluant à la fois les technologies et les approches – à la lumière des dernières tendances et nouvelles opportuni-tés. L’étude cible surtout les pratiques qui produisent des résultats et un retour sur investissement rapides et / ou les autres facteurs qui incitent à l’adoption de ces pratiques.\n\nCe document s’adresse aux parties-prenantes clés des programmes et projets de GDT aux stades de l’élaboration et de la mise en oeuvre : il s’agit surtout des praticiens, des gestionnaires, des décideurs, des planificateurs, en collaboration avec les institutions financières et tech¬niques et les donateurs. Les directives sont divisées en deux parties principales. La 1ère partie met en lumière les grands principes de la GDT ainsi que les éléments impor-tants à prendre en compte qui permettront de qualifier les technologies et approches de « bonnes pratiques » pour une transposition à grande échelle. La 2ème partie pré-sente douze groupes de technologies de GDT ainsi qu’un module sur les approches de GDT. Celles-ci sont illus¬trées par des études de cas spécifiques. Les principales personnes ressources et experts en GDT en ASS ont été sollicités afin de finaliser les groupes de GDT et de décrire les études de cas spécifiques. Ce produit s’efforce d’être à la pointe de la recherche.\n\nFocus sur la gestion durable des terres en Afrique subsaharienne");
		scrolledComposite.setContent(lblNewLabel_2);
		scrolledComposite.setMinSize(lblNewLabel_2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button btnNewButton = new Button(grpDetails, SWT.NONE);
		btnNewButton
				.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp", "icons/icon_featureinfo_16px.png"));
		btnNewButton.setToolTipText("Show the associated metadata (information sheet about the data)");
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnNewButton.setText(" - Show more");

		Button btnShowLegend = new Button(grpDetails, SWT.NONE);
		GridData gd_btnShowLegend = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnShowLegend.heightHint = 34;
		btnShowLegend.setLayoutData(gd_btnShowLegend);
		btnShowLegend.setToolTipText("Show the associated metadata (information sheet about the data)");
		btnShowLegend.setText("Show legend");
		btnShowLegend.setImage(null);

		Label lblNewLabel_3 = new Label(grpDetails, SWT.NONE);
		lblNewLabel_3.setText("Opacity:");
		new Label(grpDetails, SWT.NONE);

		scale = new Scale(grpDetails, SWT.NONE);
		scale.setSelection(100);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				scale.setToolTipText(String.valueOf(scale.getSelection())+"%");
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		//m_bindingContext = initDataBindings();

	}


	protected DataBindingContext initDataBindings() {
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
		//opacityLayerObserveValue.addValueChangeListener(new LayerOpacityChangeListener(layer));
		return bindingContext;
	}
}