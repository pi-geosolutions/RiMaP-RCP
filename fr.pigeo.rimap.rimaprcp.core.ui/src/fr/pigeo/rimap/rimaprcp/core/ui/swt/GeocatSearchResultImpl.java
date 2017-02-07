package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity.GeoBox;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatSearchTools;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;

public class GeocatSearchResultImpl extends GeocatSearchResult {
	private GeocatSearchTools searchTools;
	private Image thumbnail;
	private GeocatMetadataEntity entity;
	private SurfacePolygon polygon=null;

	public GeocatSearchResultImpl(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public GeocatSearchResultImpl(GeocatMetadataEntity entity, GeocatSearchTools searchTools, Composite parent,
			int style) {
		super(parent, style);
		load(entity, searchTools);
	}

	/*
	 * Loads values from the GeocatMetadataEntity into the form fields
	 */
	public void load(GeocatMetadataEntity entity, GeocatSearchTools searchTools) {
		this.searchTools = searchTools;
		this.entity = entity;
		this.setTitle(entity.getDefaultTitle());
		/*
		 * this.txtTitle.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override
		 * public void mouseUp(MouseEvent e) {
		 * String mtdLink =
		 * "http://ne-risk.pigeo.fr/geonetwork/srv/fre/md.viewer#/pigeo_simple_view/183";
		 * Program.launch(mtdLink);
		 * }
		 * 
		 * });
		 */
		this.btnOpenMTD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String mtdLink = "http://ne-risk.pigeo.fr/geonetwork/srv/fre/md.viewer#/pigeo_simple_view/"
						+ entity.get_geonet_info()
								.getId();
				Program.launch(mtdLink);
			}
		});
		this.setSummary(entity.get_abstract());
		this.setOriginator(entity.getFirstResponsibleParty());
		// set thumbnail
		String tn = entity.getImageAsThumbnail();
		String mtdid = entity.get_geonet_info()
				.getId();
		if (tn != null && mtdid != null && searchTools != null) {
			String url = searchTools.getFullResourcesServicePath() + "fname=" + tn + "&access=public&id=" + mtdid;
			this.setThumbnail(url);
		}
	}

	public void setTitle(String title) {
		this.txtTitle.setText(title);
		this.txtTitle.setToolTipText(title);
	}

	public void setSummary(String sum) {
		this.txtSummary.setText(sum);
		this.txtSummary.setToolTipText(sum);
	}

	public void setOriginator(String ref) {
		this.lblOriginator.setText(ref);
	}

	public void setThumbnail(String path) {
		// TODO: manage resources (Garbage collecting).
		// Maybe using SWTResourceManager
		URL url;
		try {
			/*
			 * url = new URL(path);
			 * ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			 * this.lblThumbnail.setImage(desc.createImage());
			 */
			this.thumbnail = getResizedImage(path, 100, 100);
			if (thumbnail != null) {
				this.lblThumbnail.setImage(thumbnail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Image getResizedImage(String path, int width, int height) throws MalformedURLException {
		Image scaled = null;
		URL url;
		url = new URL(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		Image image = desc.createImage(false);
		if (image != null) {
			// compute scale factor to fit in the bounds defined by width,
			// height
			int w = image.getBounds().width;
			int h = image.getBounds().height;
			double wScaleFactor = (double) width / w;
			double hScaleFactor = (double) height / h;
			double scaleFactor = Math.min(wScaleFactor, hScaleFactor);

			scaled = new Image(Display.getDefault(), image.getImageData()
					.scaledTo((int) (w * scaleFactor), (int) (h * scaleFactor)));
			image.dispose();
		}
		return scaled;
	}

	public SurfacePolygon getPolygon(java.awt.Color color) {
		if (this.polygon!=null) {
			return polygon;
		}
		if (this.entity.getGeoBox() != null && !this.entity.getGeoBox()
				.isEmpty()) {
			//update color hint int the panel
			this.lblColorHint.setBackground(
					SWTResourceManager.getColor(color.getRed(), color.getGreen(), color.getBlue()));
			
			GeoBox box = this.entity.getFirstGeoBox();
			
			ArrayList<LatLon> surfaceLinePositions = new ArrayList<LatLon>();
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getEast()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getWest()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getSouth(), box.getWest()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getSouth(), box.getEast()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getEast()));

			// define apparence
			ShapeAttributes attr = new BasicShapeAttributes();
			attr.setOutlineWidth(2.0);
			attr.setOutlineOpacity(1);
			attr.setOutlineMaterial(new Material(color));
			attr.setEnableAntialiasing(true);
			attr.setDrawInterior(false);
			
			ShapeAttributes highlightAttributes = new BasicShapeAttributes(attr);
			highlightAttributes.setInteriorOpacity(0.3);
			highlightAttributes.setInteriorMaterial(attr.getOutlineMaterial());
			highlightAttributes.setDrawInterior(true);

			// create poly & add to WWJ
			this.polygon = new SurfacePolygon(attr, surfaceLinePositions);
			polygon.setHighlightAttributes(highlightAttributes);
			
			return polygon;
		}
		return null;
	}

	@Override
	public void dispose() {
		if (this.thumbnail != null)
			this.thumbnail.dispose();
		super.dispose();
	}

}
