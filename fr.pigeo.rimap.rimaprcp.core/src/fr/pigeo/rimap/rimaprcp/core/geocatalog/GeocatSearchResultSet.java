package fr.pigeo.rimap.rimaprcp.core.geocatalog;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Summary;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocatSearchResultSet {
	protected Exception exception;

	@JsonProperty("@from")
	protected String _from;

	@JsonProperty("@to")
	protected String _to;

	@JsonProperty("@selected")
	protected String _selected;

	protected Summary summary;

	protected List<GeocatMetadataEntity> metadata;

	public GeocatSearchResultSet() {
		metadata = new ArrayList<GeocatMetadataEntity>();
	}

	public GeocatSearchResultSet(Exception exception) {
		exception.printStackTrace();
		this.exception = exception;
	}

	public String get_from() {
		return _from;
	}

	public void set_from(String _from) {
		this._from = _from;
	}

	public String get_to() {
		return _to;
	}

	public void set_to(String _to) {
		this._to = _to;
	}

	public String get_selected() {
		return _selected;
	}

	public void set_selected(String _selected) {
		this._selected = _selected;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public List<GeocatMetadataEntity> getMetadata() {
		return metadata;
	}

	/*
	 * public void setMetadata(List<GeocatMetadataEntity> metadata) {
	 * this.metadata = metadata;
	 * }
	 */
	@SuppressWarnings("unchecked")
	public void setMetadata(Object o) {
		if (o == null) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		if (o instanceof java.util.ArrayList) {
			List<GeocatMetadataEntity> m = mapper.convertValue(o, new TypeReference<List<GeocatMetadataEntity>>() {
			});
			this.metadata = m;
		} else {
			// not a list if only one result
			GeocatMetadataEntity m = mapper.convertValue(o, GeocatMetadataEntity.class);
			this.metadata.add(m);
		}
	}

	public Exception getException() {
		return exception;
	}

	public boolean hadException() {
		return (exception != null);
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
