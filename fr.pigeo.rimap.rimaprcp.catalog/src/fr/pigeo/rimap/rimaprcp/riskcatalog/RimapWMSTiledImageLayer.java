package fr.pigeo.rimap.rimaprcp.riskcatalog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class RimapWMSTiledImageLayer extends WMSTiledImageLayer {
	
	private WmsLayer parent;

	public RimapWMSTiledImageLayer(AVList params) {
		super(params);
	}

	public RimapWMSTiledImageLayer(Document dom, AVList params) {
		this(dom.getDocumentElement(), params);
	}

	public RimapWMSTiledImageLayer(Element domElement, AVList params) {
		this(wmsGetParamsFromDocument(domElement, params));
	}

	public RimapWMSTiledImageLayer(WMSCapabilities caps, AVList params) {
		this(wmsGetParamsFromCapsDoc(caps, params));
	}

	public RimapWMSTiledImageLayer(String stateInXml) {
		super(stateInXml);
		// TODO Auto-generated constructor stub
	}

	public WmsLayer getParent() {
		return parent;
	}

	public void setParent(WmsLayer parent) {
		this.parent = parent;
	}
	
	

}
