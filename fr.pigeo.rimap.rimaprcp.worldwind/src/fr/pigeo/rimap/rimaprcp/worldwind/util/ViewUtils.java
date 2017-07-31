package fr.pigeo.rimap.rimaprcp.worldwind.util;

import java.awt.Rectangle;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;

public class ViewUtils {
	/**
	 * Converts viewport (Rectangle) extent, in pixel, to a geographic bounding box
	 * @param viewport
	 * @param epsilon
	 * @return
	 */
	public static Sector getViewExtentAsSector(View view, int epsilon) {
		try {
			// view extent in screen coordinates
			Rectangle rect = view.getViewport();
			// upper left and lower right corners, in geographic coordinates
			// epsilon is used to get slightly inner boundaries, so that the
			// user sees the box
			Position ul = view.computePositionFromScreenPoint(rect.getX() + epsilon, rect.getY() + epsilon);
			Position lr = view.computePositionFromScreenPoint(rect.getX() + rect.getWidth() - epsilon,
					rect.getY() + rect.getHeight() - epsilon);

			return new Sector(lr.getLatitude(), ul.getLatitude(), ul.getLongitude(), lr.getLongitude());
		} catch (NullPointerException ex) {
			return null;
		}
	}

	/*
	 * pixel resolution (approx. , in meters)
	 */
	public static double getViewResolution(View view) {
		return view.computePixelSizeAtDistance(view.getCurrentEyePosition()
				.getAltitude());
	}

	/**
	 * Fills in a missing function in the Angle class : properly print angle
	 * with fixed number of decimals,
	 * but without any trailing "°" at the end
	 * 
	 * @param angle
	 * @param digits
	 * @return
	 */
	public static String angleToDecimalDegreesString(Angle angle, int digits) {
		if ((digits < 0) || (digits > 15)) {
			String msg = Logging.getMessage("generic.ArgumentOutOfRange", digits);
			Logging.logger()
					.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		return String.format("%." + digits, angle.degrees);
	}
}
