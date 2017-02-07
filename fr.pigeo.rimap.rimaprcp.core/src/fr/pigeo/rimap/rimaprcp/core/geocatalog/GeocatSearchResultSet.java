package fr.pigeo.rimap.rimaprcp.core.geocatalog;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocatSearchResultSet {
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

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Summary {
		@JsonProperty("@count")
		protected String _count;

		@JsonProperty("@type")
		protected String _type;

		public String get_count() {
			return _count;
		}

		public void set_count(String _count) {
			this._count = _count;
		}

		public String get_type() {
			return _type;
		}

		public void set_type(String _type) {
			this._type = _type;
		}

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

	public void setMetadata(List<GeocatMetadataEntity> metadata) {
		this.metadata = metadata;
	}

}
