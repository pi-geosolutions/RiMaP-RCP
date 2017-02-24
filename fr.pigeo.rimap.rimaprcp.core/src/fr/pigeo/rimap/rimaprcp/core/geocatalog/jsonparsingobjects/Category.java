package fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
	@JsonProperty("@value")
	protected String _value;

	@JsonProperty("@label")
	protected String _label;
	
	@JsonProperty("@count")
	protected String _count;
	
	public Category() {
		
	}

	public String get_value() {
		return _value;
	}

	public void set_value(String _value) {
		this._value = _value;
	}

	public String get_label() {
		return _label;
	}

	public void set_label(String _label) {
		this._label = _label;
	}

	public String get_count() {
		return _count;
	}

	public void set_count(String _count) {
		this._count = _count;
	}

}