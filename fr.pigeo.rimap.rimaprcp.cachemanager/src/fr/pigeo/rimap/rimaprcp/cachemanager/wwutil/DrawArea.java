package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.inject.Inject;

import org.eclipse.swt.graphics.Point;

import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class DrawArea {
	@Inject
	WwjInstance wwj;
	private static boolean clickSelect = false;
	private static boolean moveGraphActive = false;
	private static Point moveStartPos = new Point(0, 0);
	private static Point startOrigin;
	private boolean enabled;
	private MouseAdapter clickListener;
	
	
	public DrawArea(){
		//this.wwj = wwjInst;
		
		
		if (wwj != null) {

			System.out.println("value of click  " + clickSelect);
			clickSelect = true;

			System.out.println("value of click  " + clickSelect);

			if (clickSelect) {
				System.out.println("test position");
				
				setEnabled(clickSelect);
				clickSelect = false;

			}
			return;
		}
		
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		// logger.info("FeatureInfo button status is " + enabled);

		if (this.enabled) {
			if (clickListener == null) { // lazy init
				clickListener = buildMouseListener();
			}
			this.wwj.getWwd().getInputHandler().addMouseListener(clickListener);
			// logger.debug("added listener");
		} else {
			if (clickListener != null) {
				this.wwj.getWwd().getInputHandler().removeMouseListener(clickListener);
				// logger.debug("removed listener");
			}
		}
	}

	private MouseAdapter buildMouseListener() {
		// final java.awt.Point pt ;//= new Point();
		MouseAdapter ma = new MouseAdapter() {
			//@Override
			//public void addMouseMoveListener(MouseEvent e){
			//	if (e.getButton() == MouseEvent.BUTTON1) {
			//}
			
			/*
			sc.addMouseMoveListener(new MouseMoveListener() {
				 * 
				 * @Override public void mouseMove(final MouseEvent e) { if
				 * (moveGraphActive) { // FIXED final int newX = startOrigin.x +
				 * moveStartPos.x - e.x; final int newY = startOrigin.y + moveStartPos.y -
				 * e.y; sc.setOrigin(newX, newY); } } });
			*/
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// PositionListener(wwd);
					System.out.println("test position2");
					// System.out.println(wwd.getCurrentPosition().toString());

					 //java.awt.Point pt = e.getLocationOnScreen();
					 //System.out.println("xpt,ypt " + pt.toString() );

					moveStartPos.x = e.getXOnScreen();
					moveStartPos.y = e.getYOnScreen();
					System.out.println("x,y " + moveStartPos.x + "   " + moveStartPos.y);
					// moveStartPos.y = e.MOUSE_CLICKED;
					// System.out.println("y " + moveStartPos.y);
					// getFeatureInfo();

				}
			}

		};
		return ma;
	}


}
