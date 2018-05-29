package fr.pigeo.rimap.rimaprcp.worldwind.layers;

import java.util.List;

public class PolygonQueryableParams {
	private String layernames, headers;
	private PQType type;
	private int bandNumber, roundValue;
	private List<String> fields;

	public PolygonQueryableParams(String layernames, PQType type, String headers, int bandNumber, int roundValue,
			List<String> fields) {
		super();
		this.layernames = layernames;
		this.type = type;
		this.headers = headers;
		this.bandNumber = bandNumber;
		this.roundValue = roundValue;
		this.fields = fields;
	}

	public String getLayernames() {
		return layernames;
	}

	public void setLayernames(String layernames) {
		this.layernames = layernames;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public int getBandNumber() {
		return bandNumber;
	}

	public void setBandNumber(int bandNumber) {
		this.bandNumber = bandNumber;
	}

	public int getRoundValue() {
		return roundValue;
	}

	public void setRoundValue(int roundValue) {
		this.roundValue = roundValue;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public boolean isValid() {
		return layernames!=null && layernames.length()>0;
	}

	public PQType getType() {
		return type;
	}

	public void setType(PQType type) {
		this.type = type;
	}

}
