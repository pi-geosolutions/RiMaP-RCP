package fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pigeo.rimap.rimaprcp.core.Plugin;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dimension {
	@JsonProperty("@name")
	protected String _name;

	@JsonProperty("@label")
	protected String _label;

	protected List<Category> category;

	public Dimension() {
		category = new ArrayList<Category>();
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_label() {
		return _label;
	}

	@JsonIgnore
	public String getTranslatedLabel() {
		return Plugin.translate("facet_dimension_"+_label);
	}

	public void set_label(String _label) {
		this._label = _label;
	}

	public List<Category> getCategory() {
		return category;
	}

	/*
	 * public void setCategory(List<Category> category) {
	 * this.category = category;
	 * }
	 */
	@SuppressWarnings("unchecked")
	public void setCategory(Object o) {
		if (o == null) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		if (o instanceof java.util.ArrayList) {
			List<Category> c = mapper.convertValue(o, new TypeReference<List<Category>>() {
			});
			this.category = c;

			return;
		}
		//else
		// not a list if only one result
		Category c = mapper.convertValue(o, Category.class);
		this.category.add(c);
		return;
	}
	
	@JsonIgnore
	public boolean hasChildren() {
		return !this.category.isEmpty();
	}
}
