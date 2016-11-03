package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.wwj;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;

public class PolygonBuilder extends AVListImpl {
	private final WorldWindow wwd;
	private boolean armed = false;
	private ArrayList<Position> positions = new ArrayList<Position>();
	private final RenderableLayer layer;
	private final SurfacePolygon polygon;
	private boolean active = false;

	@Inject
	@Optional
	IEventBroker eventBroker;

	public PolygonBuilder(final WorldWindow wwd, RenderableLayer polygonLayer, SurfacePolygon polygon) {
		this.wwd = wwd;

		if (polygon != null) {
			this.polygon = polygon;
		} else {
			ShapeAttributes attr = new BasicShapeAttributes();
			attr.setOutlineWidth(1.0);
			attr.setInteriorOpacity(0.3);
			attr.setEnableAntialiasing(true);
			ShapeAttributes hattr = attr.copy();
			hattr.setOutlineWidth(3.0);
			hattr.setOutlineMaterial(Material.RED);
			hattr.setInteriorOpacity(0.5);
			this.polygon = new SurfacePolygon(attr);
			this.polygon.setHighlightAttributes(hattr);
		}
		this.layer = polygonLayer != null ? polygonLayer : new RenderableLayer();
		this.layer.addRenderable(this.polygon);
		this.wwd.getModel()
				.getLayers()
				.add(this.layer);
		this.wwd.getInputHandler()
				.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent mouseEvent) {
						if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
							if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
								if (!mouseEvent.isControlDown()) {
									active = true;
									addPosition();
								}
							}
							mouseEvent.consume();
						}
					}

					public void mouseReleased(MouseEvent mouseEvent) {
						if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
							/*
							 * if (positions.size() == 1)
							 * removePosition();
							 */
							active = false;
							mouseEvent.consume();
						}
					}

					public void mouseClicked(MouseEvent mouseEvent) {
						if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
							if (mouseEvent.isControlDown())
								removePosition();
							mouseEvent.consume();
						}
						if (mouseEvent.getClickCount() == 2) {
							finishDrawing();
						}
					}
				});

		this.wwd.getInputHandler()
				.addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent mouseEvent) {
						if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
							// Don't update the polyline here because the wwd
							// current cursor position will not
							// have been updated to reflect the current mouse
							// position. Wait to update in the
							// position listener, but consume the event so the
							// view doesn't respond to it.
							if (active)
								mouseEvent.consume();
						}
					}
				});

		this.wwd.addPositionListener(new PositionListener() {
			public void moved(PositionEvent event) {
				if (!active)
					return;

				if (positions.size() == 1)
					addPosition();
				else
					replacePosition();
			}
		});
	}

	/**
	 * Finishes drawing the polygon :
	 * - disarms the PolygonBuilder
	 * - send the polygon
	 */
	protected void finishDrawing() {
		this.setArmed(false);
		this.polygon.setHighlighted(true);
		if (eventBroker != null) {
			eventBroker.post(QueryEventConstants.POLYGONQUERY_POLYGON_CLOSED, this.polygon);
		}
	}

	/**
	 * Returns the layer holding the polyline being created.
	 *
	 * @return the layer containing the polyline.
	 */
	public RenderableLayer getLayer() {
		return this.layer;
	}

	/**
	 * Returns the layer currently used to display the polyline.
	 *
	 * @return the layer holding the polyline.
	 */
	public SurfacePolygon getPolygon() {
		return this.polygon;
	}

	/**
	 * Removes all positions from the polyline.
	 */
	public void clear() {
		while (this.positions.size() > 0)
			this.removePosition();
		this.polygon.setHighlighted(false);
	}

	/**
	 * Identifies whether the line builder is armed.
	 *
	 * @return true if armed, false if not armed.
	 */
	public boolean isArmed() {
		return this.armed;
	}

	/**
	 * Arms and disarms the polygon builder. When armed, the polygon builder
	 * monitors user input and builds the polygon in
	 * response to the actions mentioned in the overview above. When disarmed,
	 * the polygon builder ignores all user input.
	 *
	 * @param armed
	 *            true to arm the polygon builder, false to disarm it.
	 */
	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	private void addPosition() {
		Position curPos = this.wwd.getCurrentPosition();
		if (curPos == null)
			return;

		this.positions.add(curPos);
		this.polygon.setLocations(this.positions);
		this.firePropertyChange("PolygonBuilder.AddPosition", null, curPos);
		this.wwd.redraw();
	}

	private void replacePosition() {
		Position curPos = this.wwd.getCurrentPosition();
		if (curPos == null)
			return;

		int index = this.positions.size() - 1;
		if (index < 0)
			index = 0;

		Position currentLastPosition = this.positions.get(index);
		this.positions.set(index, curPos);
		this.polygon.setLocations(this.positions);
		this.firePropertyChange("PolygonBuilder.ReplacePosition", currentLastPosition, curPos);
		this.wwd.redraw();
	}

	private void removePosition() {
		if (this.positions.size() == 0)
			return;

		Position currentLastPosition = this.positions.get(this.positions.size() - 1);
		this.positions.remove(this.positions.size() - 1);
		this.polygon.setLocations(this.positions);
		this.firePropertyChange("PolygonBuilder.RemovePosition", currentLastPosition, null);
		this.wwd.redraw();
	}

}
