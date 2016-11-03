package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;

import fr.pigeo.rimap.rimaprcp.core.ui.core.Central;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.layers.IQueryableLayer;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;

@Creatable
@Singleton
public class FeatureInfo {
	@Inject
	Logger logger;
	@Inject
	WwjInstance wwj;
	@Inject
	private EPartService partService;
	@Inject
	EModelService modelService;
	@Inject
	MApplication application;
	@Inject
	Central central;
	@Inject
	@Translation
	Messages messages;

	private IQueryableLayer[] layers;
	private boolean enabled;
	private MouseAdapter clickListener;
	private Locale locale = Locale.ENGLISH;

	@Inject
	public FeatureInfo(IEclipseContext context) {
		locale = (Locale) context.get(TranslationService.LOCALE);
	}

	/**
	 * @return the queryable layers list
	 */
	public IQueryableLayer[] getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(IQueryableLayer[] layers) {
		this.layers = layers;
	}

	/**
	 * @return the FeatureInfo tool status
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            enable FeatureInfo tool
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		 //logger.info("FeatureInfo button status is " + enabled);

		if (this.enabled) {
			if (clickListener == null) { // lazy init
				clickListener = buildMouseListener();
			}
			this.wwj.getWwd().getInputHandler().addMouseListener(clickListener);
			((Component) wwj.getWwd()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			//logger.debug("added listener");
		} else {
			if (clickListener != null) {
				this.wwj.getWwd().getInputHandler().removeMouseListener(clickListener);
				((Component) wwj.getWwd()).setCursor(Cursor.getDefaultCursor());
				//logger.debug("removed listener");
			}
		}
	}

	private MouseAdapter buildMouseListener() {
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					getFeatureInfo();
				}
			}

		};
		return ma;
	}

	private void getFeatureInfo() {
		final Position pos = wwj.getWwd().getCurrentPosition();
		//logger.info("TODO: retrieve FI at pos " + pos.toString());
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ArrayList<FeatureInfoTarget> targets = new ArrayList<FeatureInfoTarget>();
					Layer[] wwjlayers = wwj.getLayersList();
					for (Layer layer : wwjlayers) {
						//System.out.println(layer.getClass());
						if (layer instanceof IQueryableLayer) {
							IQueryableLayer qlayer = (IQueryableLayer) layer;
							if (qlayer.isQueryable()) {
								URL req = qlayer.buildFeatureInfoRequest(pos, locale.getISO3Country());
								if (req != null) {
									//logger.info("GetFeatureInfoURL: "+req.toString());
									targets.add(new FeatureInfoTarget(qlayer, pos));
								}
							}
						}
					}
					
					if (targets.isEmpty()) {
						return;
					}
					central.forceSet("fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfoTargets", targets);
					MPart part = partService.createPart("fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.partdescriptor.fi");
					/*MPart part = modelService.createModelElement(MPart.class);
					part.setContributionURI(
							"bundleclass://fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui/fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views.FeatureInfoResultsPart");
							*/
					// create a nice label for the part header
					String header = messages.fi_results_position + pos.toString();
					part.setCloseable(true);
					// add it an existing stack and show it
					MPartStack stack = (MPartStack) modelService.find("fr.pigeo.rimap.rimaprcp.partstack.bottom",
							application);
					
					stack.getChildren().add(part);
					int siblings = countSiblings(stack);
					if (siblings > 1) {
						part.setLabel("(" + siblings + ")");
					}
					if (!stack.isVisible()) {
						stack.setVisible(true);
					}
					partService.showPart(part, EPartService.PartState.ACTIVATE);
				}

				private int countSiblings(MPartStack stack) {
					List<String> tags = new ArrayList<>();
					tags.add("FeatureInfo");
					List<MPart> elementsWithTags = modelService.findElements(application, "fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.partdescriptor.fi"
							, MPart.class, null);
					//System.out.println("Found parts(s) : " + elementsWithTags.size());
					return elementsWithTags.size();
				}
			});
	}

	/**
	 * @param enabled
	 *            toggles the FeatureInfo tool status
	 */
	public void toggleEnabled() {
		this.setEnabled(!this.isEnabled());
	}

}
