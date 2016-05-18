package fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs;

public class CatalogParams {
	private String url,
					name,
					type,
					cachePath;
	private int web_usage_level;
	
	public CatalogParams(String url, String name, String type, int web_usage_level, String cachePath) {
		super();
		this.url = url;
		this.name = name;
		this.type = type;
		this.web_usage_level = web_usage_level;
		this.cachePath = cachePath;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWeb_usage_level() {
		return web_usage_level;
	}

	public void setWeb_usage_level(int web_usage_level) {
		this.web_usage_level = web_usage_level;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
