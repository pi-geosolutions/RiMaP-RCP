package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;

/**
 * @author jean.pommier@pi-geosolutions.fr
 *
 *         Manages the Renderable instance dedicated to CacheManager plugin.
 *         Deals with adding the layer to WWJ, adding polygons in the layer,
 *         etc.
 *         and cleaning it from WWJ when quitting the prespective
 */
@Creatable
@Singleton
public class RenderableManager {
	private final RenderableLayer renderableLayer = new RenderableLayer();

	@Inject
	WwjInstance wwj;

	public RenderableLayer getRenderableLayer() {
		wwj.addLayer(renderableLayer);
		return this.renderableLayer;
	}
	
	/*
	 * Use this when you want to replace previous renderables by this one
	 */
	public void setRenderable(Renderable r) {
		renderableLayer.removeAllRenderables();
		renderableLayer.addRenderable(r);
		wwj.addLayer(renderableLayer);
	}

	/*
	 * Use this when you want to add this renderable without removing previous
	 * ones
	 */
	public void addRenderable(Renderable r) {
		renderableLayer.addRenderable(r);
		wwj.addLayer(renderableLayer);
	}

	/*
	 * Empties the layer but does not remove it
	 */
	public void clearLayer() {
		renderableLayer.removeAllRenderables();
	}

	/*
	 * Removes the layer from WWJ
	 */
	public void removeLayer() {
		wwj.removeLayer(renderableLayer);
	}

	@Inject
	@Optional
	void exitingPerspective(@UIEventTopic(RiMaPEventConstants.LEAVING_PERSPECTIVE) String perspectiveId) {
		removeLayer();
	}

}
