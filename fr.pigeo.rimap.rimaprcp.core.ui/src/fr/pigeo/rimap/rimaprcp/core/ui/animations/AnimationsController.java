package fr.pigeo.rimap.rimaprcp.core.ui.animations;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
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

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
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
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.render.SurfaceSector;

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

	@Inject
	Shell shell;

	private AnimationsModel model;
	private AnimationsExtent animationExtent;
	private AnimationsDialog animationsDialog;
	private RimapWMSTiledImageLayer layer;
	private String storagePath = "wmst/";
	private Job preloadJob;
	private boolean cancelPreloadJob = false;
	private RenderableLayer wwjLayer;
	private SurfaceImage wwjSurfaceImage;
	// current* are used to freeze the values during preload, even if View
	// changes
	private double currentResolution;
	private int playStep = 0;
	//used to  track play direction changes (and stop deprecated play loops)
	private int playSeqId=0;

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
		return this.animationExtent.getSurfaceSector() != null && this.animationExtent.getSurfaceSector()
				.getSector() != null
				&& this.animationExtent.getSurfaceSector()
						.getSector()
						.isWithinLatLonLimits();
	}

	public boolean preloadImages() {
		// get timestamps list

		// TODO : clean expired images

		String[] timestamps = model.getTimestamps();
		String category = this.getFolderName();
		cancelPreloadJob = false;
		this.currentResolution = ViewUtils.getViewResolution(wwj.getWwd()
				.getView());
		this.animationExtent.freezeExtent(true);
		preloadJob = new Job("[Animations] Preload Images job") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				int count = 0;
				for (String timestamp : timestamps) {
					if (isPreloadJobCanceled()) {
						return Status.CANCEL_STATUS;
					}
					String filename = getFilename(timestamp);
					// makes it suitable for a filename
					filename = filename.replaceAll("[=.:]", "-");
					filename = filename.replaceAll("[&,]", "_");
					// add extension
					filename += ".png";
					resourceService.getResource(buildImageURL(timestamp), category, filename,
							WebUsageLevel.PRIORITY_LOCAL);
					count++;
					if (!isPreloadJobCanceled()) {
						eventBroker.send(RiMaPEventConstants.ANIMATIONS_FILES_LOAD_PROGRESS, count);
					}
				}
				eventBroker.send(RiMaPEventConstants.ANIMATIONS_FILES_LOAD_COMPLETE, timestamps);

				animationExtent.freezeExtent(false);
				return Status.OK_STATUS;
			}
		};
		preloadJob.schedule();

		return true;
	}

	private String getFolderName() {
		return this.storagePath + layer.getParent()
				.getLayers();
	}

	private String getFilename(String timestamp) {
		return timestamp + "__" + animationExtent.getExtentAsWmsBBOXString() + "__" + getImageDimensionsAsWmsString();
	}

	private String buildImageURL(String timestamp) {
		String url = "";
		// base URL
		url += layer.getParent()
				.getUrl();
		if (!url.endsWith("?")) {
			url += "?";
		}

		// Parameters
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
		this.animationExtent.freezeExtent(false);
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
		SurfaceSector imageSector = animationExtent.getSurfaceSector();
		if (imageSector == null) {
			return null;
		}
		// width, in pixels, for the image at normal resolution
		int x = (int) (imageSector.getWidth(wwj.getModel()
				.getGlobe()) / this.currentResolution);
		int y = (int) (imageSector.getHeight(wwj.getModel()
				.getGlobe()) / this.currentResolution);

		return new Rectangle((int) (x * resolutionFactor), (int) (y * resolutionFactor));
	}

	private Rectangle estimateImageDimensions() {
		return estimateImageDimensions(1);
	}

	public void initPlayer() {
		// hide the original layer
		layer.setEnabled(false);

		String[] ts = this.model.getTimestamps();
		showImage(ts.length - 1);
		eventBroker.send(RiMaPEventConstants.LAYERSLIST_REFRESH, "");
	}

	public BufferedImage getBufferedImage(String timestamp) {
		BufferedImage bufferedImage = null;
		byte[] file = resourceService.getResource(buildImageURL(timestamp), this.getFolderName(),
				getFilename(timestamp), WebUsageLevel.PRIORITY_LOCAL);
		InputStream in = new ByteArrayInputStream(file);
		try {
			bufferedImage = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bufferedImage;
	}

	public void cleanup() {
		this.cancelPreloadJob();
		// remove extent box
		this.animationExtent.removeRenderables();

		// remove image layer and re-enable normal layer
		if (this.wwjLayer != null) {
			wwj.removeLayer(this.wwjLayer);
		}
		layer.setEnabled(true);
		wwj.getWwd()
				.redraw();
		eventBroker.send(RiMaPEventConstants.LAYERSLIST_REFRESH, "");

		// cleanup model (because of data binding to UI)
		model.setCurrentDate("");
	}

	@Inject
	@Optional
	void sectorChanged(@UIEventTopic(RiMaPEventConstants.ANIMATIONS_SECTORSELECTOR_SECTOR_CHANGED) Sector sector) {
		this.model.setExtentType(i18n.ANIM_EXTENT_CUSTOM);
	}

	public void showImage(int index) {
		index = normalizeIndex(index);
		model.setCurrentDateIndex(index);
		BufferedImage bi = getBufferedImage(model.getCurrentDate());
		if (bi == null) {
			return;
		}
		if (wwjLayer == null) {
			wwjLayer = new RenderableLayer();
			wwjLayer.setPickEnabled(false);
			wwjSurfaceImage = new SurfaceImage(bi, this.animationExtent.getSurfaceSector()
					.getSector());
			wwjLayer.addRenderable(wwjSurfaceImage);
		} else {
			// then wwjSurfaceImage should already exist and be part of wwjLayer
			wwjSurfaceImage.setImageSource(bi, this.animationExtent.getSurfaceSector()
					.getSector());
		}
		//send event. Used at least for slider update in UI
		eventBroker.send(RiMaPEventConstants.ANIMATIONS_PLAYER_DATE_CHANGED, index);
		
		wwjLayer.setName(layer.getName() + "(animation)");

		// Will actually update the layer, if it is already in the model:
		wwj.addLayer(wwjLayer);
		wwjLayer.setEnabled(true);
		wwj.getWwd()
				.redraw();
	}

	private int normalizeIndex(int index) {
		int length = model.getTimestamps().length;
		if (index < 0) {
			index = length + index;
		} else if (index > length - 1) {
			index = index - length;
		}
		return index;

	}

	public void showPrevImage() {
		int index = normalizeIndex(model.getCurrentDateIndex() - 1);
		showImage(index);
	}

	public void showNextImage() {
		int index = normalizeIndex(model.getCurrentDateIndex() + 1);
		showImage(index);
	}

	public void showFirstImage() {
		showImage(0);
	}

	public void showLastImage() {
		showImage(-1);
	}

	/**
	 * Runs the animation (Play buttons)
	 * 
	 * @param step
	 *            : usually +1 or -1 to play forward or backward. If set to 0,
	 *            stops the animation
	 */
	public void play(int step, int sleepTime) {
		this.playStep = step;
		this.playSeqId++;
		if (step != 0) {
			this.playAnimation(sleepTime, this.playSeqId);
		}
	}

	private void playAnimation(int sleepTime, int seqId) {
		if (this.playStep == 0 || seqId < this.playSeqId) {
			return;
		}
		int index = normalizeIndex(model.getCurrentDateIndex() + playStep);
		showImage(index);
		if (shell != null) {
			//Used to check, at next iteration, we can continue on playing this loop
			int s = seqId;
			shell.getDisplay()
					.timerExec(sleepTime, new Runnable() {
						public void run() {
							playAnimation(sleepTime, seqId);
						}
					});
		}
	}
	
	public void stop() {
		this.playStep = 0;
		this.playSeqId++;
	}

}
