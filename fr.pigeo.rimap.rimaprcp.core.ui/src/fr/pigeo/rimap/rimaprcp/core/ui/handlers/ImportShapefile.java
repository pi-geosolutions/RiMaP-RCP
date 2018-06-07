
package fr.pigeo.rimap.rimaprcp.core.ui.handlers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.RimapAVKey;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.ShapefileExtrudedPolygons;
import gov.nasa.worldwind.formats.shapefile.ShapefileLayerFactory;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;

public class ImportShapefile {
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute(Shell shell, WwjInstance wwj, IEventBroker evtBroker) {
		this.eventBroker = evtBroker;
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.shp", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (selected != null) {
			importShapefile(selected, wwj);
		}
	}

	protected void importShapefile(String path, WwjInstance wwj) {
		// Construct a factory that loads Shapefiles on a background thread.
		ShapefileLayerFactory factory = new ShapefileLayerFactory();

		ShapeAttributes sideAttributes = new BasicShapeAttributes();
		sideAttributes.setInteriorMaterial(Material.LIGHT_GRAY);
		sideAttributes.setOutlineOpacity(0.8);
		sideAttributes.setInteriorOpacity(0.5);
		sideAttributes.setOutlineMaterial(Material.DARK_GRAY);
		sideAttributes.setOutlineWidth(2);
		sideAttributes.setDrawOutline(true);
		sideAttributes.setDrawInterior(true);
		sideAttributes.setEnableLighting(true);

		ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
		sideHighlightAttributes.setOutlineMaterial(Material.BLACK);
		sideHighlightAttributes.setOutlineOpacity(1);
		sideHighlightAttributes.setOutlineWidth(3);
		sideHighlightAttributes.setInteriorMaterial(Material.GREEN);
		sideHighlightAttributes.setInteriorOpacity(0.8);

		factory.setNormalShapeAttributes(sideAttributes);
		factory.setHighlightShapeAttributes(sideHighlightAttributes);

		// Load a Shapefile in the San Francisco bay area containing
		// per-shape height attributes.
		factory.createFromShapefileSource(path, new ShapefileLayerFactory.CompletionCallback() {
			@Override
			public void completion(Object result) {
				final RenderableLayer layer = (RenderableLayer) result; // the result is
													// the layer the
													// factory
													// created
				layer.setName(WWIO.getFilename(layer.getName()));
				layer.setValue(RimapAVKey.LAYER_TYPE, "shapefile");
				layer.addPropertyChangeListener(AVKey.OPACITY, new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						double opacity = layer.getOpacity();
						System.out.println(opacity);
						for (Renderable renderable : layer.getRenderables()) { 
							if (renderable instanceof Polygon) {
				                Polygon polygon = (Polygon) renderable;
				                polygon.getAttributes().setInteriorOpacity(opacity);
				                polygon.getAttributes().setOutlineOpacity(opacity);
				            } else if (renderable instanceof ExtrudedPolygon) {
				                ExtrudedPolygon polygon = (ExtrudedPolygon) renderable;
				                polygon.getAttributes().setInteriorOpacity(opacity);
				                polygon.getAttributes().setOutlineOpacity(opacity);
				                polygon.getSideAttributes().setInteriorOpacity(opacity);
				                polygon.getSideAttributes().setOutlineOpacity(opacity);
				            } else {
				                System.out.println("setOpacity not handled on :" + renderable);
				            }
						}
						
					}
				});
				// Add the layer to the World Window's layer list on
				// the Event Dispatch Thread.
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						wwj.getWwd()
								.getModel()
								.getLayers()
								.add(layer);
						wwj.getWwd()
								.redraw();

						// notify Layers list for refresh
	                    eventBroker.post(RiMaPEventConstants.LAYERSLIST_REFRESH, null);
					}
				});
			}

			@Override
			public void exception(Exception e) {
				Logging.logger()
						.log(java.util.logging.Level.SEVERE, e.getMessage(), e);
			}
		});
	}

}