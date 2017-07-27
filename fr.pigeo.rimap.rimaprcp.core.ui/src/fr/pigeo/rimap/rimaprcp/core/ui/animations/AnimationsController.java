package fr.pigeo.rimap.rimaprcp.core.ui.animations;

import java.awt.Rectangle;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.core.constants.RimapEventConstants;
import fr.pigeo.rimap.rimaprcp.core.resource.IResourceService;
import fr.pigeo.rimap.rimaprcp.core.resource.WebUsageLevel;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.worldwind.layers.RimapWMSTiledImageLayer;
import fr.pigeo.rimap.rimaprcp.core.ui.animations.AnimationsExtent.InvalidExtentException;
import fr.pigeo.rimap.rimaprcp.core.ui.translation.Messages;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import fr.pigeo.rimap.rimaprcp.worldwind.util.ViewUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *         Controller class for the Animations widget
 */
@Creatable
@Singleton
public class AnimationsController {
	@Inject
	WwjInstance wwj;

	@Inject
	@Translation
	Messages i18n;

	@Inject
	IResourceService resourceService;

	@Inject
	IEventBroker eventBroker;

	@Inject
	IEclipseContext context;

	private AnimationsModel model;
	private AnimationsExtent animationExtent;
	private AnimationsDialog animationsDialog;
	private RimapWMSTiledImageLayer layer;
	private String storagePath = "wmst/";
	private Job preloadJob;
	private boolean cancelPreloadJob = false;

	@Inject
	public AnimationsController(@Translation Messages i18n, WwjInstance wwj, IEventBroker eventBroker) {
		model = new AnimationsModel(i18n);
		this.animationExtent = new AnimationsExtent(wwj, eventBroker);
	}

	public void init() {
		if (layer != null) {
			animationExtent.drawSector();
		}
	}

	public AnimationsModel getModel() {
		return model;
	}

	public void setLayer(RimapWMSTiledImageLayer layer) {
		this.layer = layer;
		if (model != null) {
			this.model.setName(layer.getName());
			AVList avl = (AVList) this.layer.getValue(AVKey.CONSTRUCTION_PARAMETERS);
			String[] timestamps = avl.getStringValue(RimapAVKey.LAYER_TIME_DIMENSION_VALUES)
					.split(",");
			this.model.setTimestamps(timestamps);
		}
	}

	public RimapWMSTiledImageLayer getLayer() {
		return layer;
	}

	public String getExtentType() {
		if (model == null) {
			return i18n.ANIM_EXTENT_UNDEFINED;
		}
		return model.getExtentType();
	}

	public void setExtentType(String extent) throws InvalidExtentException {
		if (extent.equals(i18n.ANIM_EXTENT_FULL)) {
			this.animationExtent.setFullExtent(this.layer);
		} else if (extent.equals(i18n.ANIM_EXTENT_VIEW)) {
			this.animationExtent.setViewExtent();
		}
		model.setExtentType(extent);
	}

	public boolean isExtentValid() {
		return this.animationExtent.getSector() != null && this.animationExtent.getSector()
				.isWithinLatLonLimits();
	}

	public boolean preloadImages() {
		// get timestamps list

		// TODO : clean expired images

		String[] timestamps = model.getTimestamps();
		String category = this.storagePath + layer.getParent()
				.getLayers();
		cancelPreloadJob = false;
		preloadJob = new Job("[Animations] Preload Images job") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				int count = 0;
				for (String timestamp : timestamps) {
					if (isPreloadJobCanceled()) {
						return Status.CANCEL_STATUS;
					}
					String filename = timestamp + "__" + animationExtent.getExtentAsWmsBBOXString() + "__"
							+ getImageDimensionsAsWmsString();
					// makes it suitable for a filename
					filename = filename.replaceAll("[=.:]", "-");
					filename = filename.replaceAll("[&,]", "_");
					// add extension
					filename += ".png";
					resourceService.getResource(buildImageURL(timestamp), category, filename,
							WebUsageLevel.PRIORITY_LOCAL);
					count++;
					if (!isPreloadJobCanceled()) {
						eventBroker.send(RimapEventConstants.ANIMATIONS_FILES_LOAD_PROGRESS, count);
					}
				}
				eventBroker.send(RimapEventConstants.ANIMATIONS_FILES_LOAD_COMPLETE, timestamps);
				return Status.OK_STATUS;
			}
		};
		preloadJob.schedule();

		return true;
	}

	private String buildImageURL(String timestamp) {
		String url = "";
		url += layer.getParent()
				.getUrl();
		url += "SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&CRS=EPSG:4326&STYLES=";
		url += "&LAYERS=" + layer.getParent()
				.getLayers();
		url += "&" + getImageDimensionsAsWmsString();
		url += "&BBOX=" + animationExtent.getExtentAsWmsBBOXString();
		url += "&TIME=" + timestamp;
		return url;
	}

	public boolean isPreloadJobCanceled() {
		return cancelPreloadJob;
	}

	public void cancelPreloadJob() {
		this.cancelPreloadJob = true;
	}

	public void cleanup() {
		this.cancelPreloadJob();
		this.animationExtent.removeRenderables();
	}

	private String getImageDimensionsAsWmsString() {
		double resolutionFactor = model.getResolutionAsMultiplicationFactor();
		Rectangle dims = estimateImageDimensions(resolutionFactor);
		return "WIDTH=" + dims.width + "&HEIGHT=" + dims.height;
	}

	/**
	 * Estimates the dimensions to require depending on the targeted resolution
	 * multiplicator factor
	 * The estimate is made by computing the cross product imageSector *
	 * viewportSize/viewportSector
	 * 
	 * @return
	 */
	private Rectangle estimateImageDimensions(double resolutionFactor) {
		Rectangle viewport = wwj.getWwd()
				.getView()
				.getViewport();
		Sector viewSector = ViewUtils.getViewExtentAsSector(wwj.getWwd()
				.getView(), 0);
		Sector imageSector = animationExtent.getSector();
		if (imageSector == null) {
			return null;
		}
		// width, in pixels, for the image at normal resolution
		int x = (int) (imageSector.getDeltaLonDegrees() * viewport.getWidth() / viewSector.getDeltaLonDegrees());
		int y = (int) (imageSector.getDeltaLatDegrees() * viewport.getHeight() / viewSector.getDeltaLatDegrees());

		return new Rectangle((int) (x * resolutionFactor), (int) (y * resolutionFactor));
	}

	private Rectangle estimateImageDimensions() {
		return estimateImageDimensions(1);
	}

	public void openDialog(Shell shell, Layer l) {
		if (animationsDialog == null) {
			animationsDialog = new AnimationsDialog(shell);
			ContextInjectionFactory.inject(animationsDialog, context);
		} else {
			this.cancelPreloadJob();
			animationsDialog.resetUI();
		}
		this.setLayer((RimapWMSTiledImageLayer) l);
		animationsDialog.open();
	}

	@Inject
	@Optional
	void sectorChanged(@UIEventTopic(RimapEventConstants.ANIMATIONS_SECTORSELECTOR_SECTOR_CHANGED) Sector sector) {
		this.model.setExtentType(i18n.ANIM_EXTENT_CUSTOM);
	}

}
