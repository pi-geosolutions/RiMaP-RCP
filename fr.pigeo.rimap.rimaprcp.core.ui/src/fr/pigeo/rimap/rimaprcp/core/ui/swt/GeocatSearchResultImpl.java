package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import geocatalog.GeocatMetadataEntity;
import geocatalog.GeocatSearchTools;

public class GeocatSearchResultImpl extends GeocatSearchResult {
	private GeocatSearchTools searchTools;
	private Image thumbnail;

	public GeocatSearchResultImpl(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public GeocatSearchResultImpl(GeocatMetadataEntity entity, GeocatSearchTools searchTools, Composite parent,
			int style) {
		super(parent, style);
		this.searchTools = searchTools;
		load(entity);
	}

	/*
	 * Loads values from the GeocatMetadataEntity into the form fields
	 */
	public void load(GeocatMetadataEntity entity) {
		this.setTitle(entity.getDefaultTitle());
		this.setSummary(entity.get_abstract());
		this.setOriginator(entity.getFirstResponsibleParty());
		// set thumbnail
		String tn = entity.getImageAsThumbnail();
		String mtdid = entity.get_geonet_info()
				.getId();
		if (tn != null && mtdid != null && searchTools != null) {
			String url = searchTools.getResourcesServicePath() + "fname=" + tn + "&access=public&id=" + mtdid;
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

	@Override
	public void dispose() {
		if (this.thumbnail != null)
			this.thumbnail.dispose();
		super.dispose();
	}

}
