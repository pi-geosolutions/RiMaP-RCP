package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

import geocatalog.GeocatMetadataEntity;

public class GeocatSearchResultImpl extends GeocatSearchResult {

	public GeocatSearchResultImpl(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	public GeocatSearchResultImpl(GeocatMetadataEntity entity, Composite parent, int style) {
		super(parent, style);
		load(entity);
	}
	/*
	 * Loads values from the GeocatMetadataEntity into the form fields
	 */
	public void load(GeocatMetadataEntity entity) {
		this.setTitle(entity.getDefaultTitle());
		this.setSummary(entity.get_abstract());
		this.setOriginator(entity.getResponsibleParty().get(0));
	}
	
	public void setTitle(String title) {
		this.txtTitle.setText(title);
	}
	
	public void setSummary(String sum) {
		this.txtSummary.setText(sum);
	}
	
	public void setOriginator(String ref) {
		this.lblOriginator.setText(ref);
	}
	
	public void setThumbnail(String path) {
		//TODO: manage resources (Garbage collecting). 
		// Maybe using SWTResourceManager
		URL url;
		try {
			url = new URL(path);
			ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			this.lblThumbnail.setImage(desc.createImage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
