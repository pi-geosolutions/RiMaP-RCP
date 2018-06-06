
package fr.pigeo.rimap.rimaprcp.core.ui.handlers;

import javax.swing.SwingUtilities;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.formats.shapefile.ShapefileLayerFactory;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;

public class ImportShapefile {
	@Execute
	public void execute(Shell shell, WwjInstance wwj) {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.shp", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (selected != null) {
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
			factory.createFromShapefileSource(selected,
					new ShapefileLayerFactory.CompletionCallback() {
						@Override
						public void completion(Object result) {
							final Layer layer = (Layer) result; // the result is
																// the layer the
																// factory
																// created
							layer.setName(WWIO.getFilename(layer.getName()));
							// Add the layer to the World Window's layer list on
							// the Event Dispatch Thread.
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									wwj.getWwd().getModel()
											.getLayers()
											.add(layer);
									wwj.getWwd().redraw();
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

}