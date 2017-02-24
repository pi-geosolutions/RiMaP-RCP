package fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {
	@JsonProperty("@count")
	protected String _count;

	@JsonProperty("@type")
	protected String _type;

	protected List<Dimension> dimension;

	public Summary() {
		dimension = new ArrayList<Dimension>();
	}
	
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


	@SuppressWarnings("unchecked")
	public void setFacet(Object o) {
		if (o==null) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		if (o instanceof java.util.ArrayList) {
			List<Dimension> d = mapper.convertValue(o, new TypeReference<List<Dimension>>() { });
			this.dimension = d;
		} else {
			//not a list if only one result
			Dimension d = mapper.convertValue(o, Dimension.class);
			this.dimension.add(d);
		}
	}

	public List<Dimension> getDimension() {
		return dimension;
	}

/*	public void setDimension(List<Dimension> dimension) {
		this.dimension = dimension;
	}*/
	
	public boolean hasChildren() {
		return !this.dimension.isEmpty();
	}

}