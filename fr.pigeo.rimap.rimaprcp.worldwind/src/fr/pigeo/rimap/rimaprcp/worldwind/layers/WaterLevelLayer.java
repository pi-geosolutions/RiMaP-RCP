package fr.pigeo.rimap.rimaprcp.worldwind.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;

public class WaterLevelLayer extends RenderableLayer {
	double elevation = 0d;
	double opacity = 1d;
	TexturedLayer tl;

	public WaterLevelLayer() {
		this.setName("Water Level Layer");
	}

	public void createWaterPolygon(Sector sector) {
		// Create the blue image used for water color, needed by TexturedLayer
		BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.BLUE);
		g2.fillRect(00, 0, 24, 24);
		g2.finalize();

		// Create all-blue TexturedLayer
		tl = new TexturedLayer(img, sector);
		tl.setElevation(this.elevation);
		tl.setOpacity(this.opacity);
		this.addRenderable(tl);

	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
		if (tl != null) {
			tl.setElevation(elevation);
		}
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
		if (tl != null) {
			tl.setOpacity(this.opacity);
		}
	}

}
