package fr.pigeo.rimap.rimaprcp.core.geocatalog;

import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * POJO used to parse the search results using Jackson data binding
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocatMetadataEntity {
	//Declare global vars
	protected String IMAGE_THUMBNAIL_ID = "thumbnail";
	protected String RESPONSIBLE_PARTY_TYPE_ID = "resource";
	
	//Variables used for de-serialization of the JSON metadata entry
	protected String idxError;
	protected ArrayList<String> responsibleParty;
	protected ArrayList<String> type;
	protected String isHarvested;
	protected String docLocale;
	protected String popularity;
	protected ArrayList<String> keyword;
	protected ArrayList<String> publishedForGroup;
	protected ArrayList<String> image;
	protected ArrayList<String> mdLanguage;
	protected String maintenanceAndUpdateFrequency_text;
	protected String format;
	protected String root;
	protected String isTemplate;
	protected String valid;
	protected String rating;
	protected String source;
	protected ArrayList<String> status;
	protected String updateFrequency;
	protected ArrayList<String> geoBox;
	protected String owner;
	protected ArrayList<String> link;
	protected String defaultTitle;
	protected ArrayList<String> datasetLang;

	protected String userinfo;
	protected ArrayList<String> topicCat;
	protected String publicationDate;
	protected ArrayList<String> status_text;
	protected String standardName;
	protected String logo;
	@JsonProperty("abstract")
	protected String _abstract;
	@JsonIgnore
	protected String keywordGroup;
	protected String groupOwner;
	protected String _locale;
	@JsonProperty("geonet:info")
	protected GeonetInfo _geonet_info;

	public class GeonetInfo {
		@JsonProperty("@xmlns:geonet")
		protected String _xmlns_geonet;
		protected String id;
		protected String uuid;
		protected String schema;
		protected String createDate;
		protected String changeDate;
		protected String source;
		protected String isPublishedToAll;
		protected String view;
		protected String notify;
		protected String download;
		protected String dynamic;
		protected String featured;
		protected String selected;

		public String get_xmlns_geonet() {
			return _xmlns_geonet;
		}

		public void set_xmlns_geonet(String _xmlns_geonet) {
			this._xmlns_geonet = _xmlns_geonet;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getChangeDate() {
			return changeDate;
		}

		public void setChangeDate(String changeDate) {
			this.changeDate = changeDate;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getIsPublishedToAll() {
			return isPublishedToAll;
		}

		public void setIsPublishedToAll(String isPublishedToAll) {
			this.isPublishedToAll = isPublishedToAll;
		}

		public String getView() {
			return view;
		}

		public void setView(String view) {
			this.view = view;
		}

		public String getNotify() {
			return notify;
		}

		public void setNotify(String notify) {
			this.notify = notify;
		}

		public String getDownload() {
			return download;
		}

		public void setDownload(String download) {
			this.download = download;
		}

		public String getDynamic() {
			return dynamic;
		}

		public void setDynamic(String dynamic) {
			this.dynamic = dynamic;
		}

		public String getFeatured() {
			return featured;
		}

		public void setFeatured(String featured) {
			this.featured = featured;
		}

		public String getSelected() {
			return selected;
		}

		public void setSelected(String selected) {
			this.selected = selected;
		}
	}

	public ArrayList<String> getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = arrayifyString(type);
	}

	public String getDefaultTitle() {
		return defaultTitle;
	}

	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

	public ArrayList<String> getResponsibleParty() {
		return responsibleParty;
	}
	public String getFirstResponsibleParty() {
		Iterator<String> it = responsibleParty.iterator();
		while (it.hasNext()) {
			String s = it.next();
			String[] chunks = s.split("\\|");
			if (chunks.length>1 && chunks[1].equalsIgnoreCase(this.RESPONSIBLE_PARTY_TYPE_ID)) {
				if (!chunks[2].isEmpty()) { //we have an organism name
					return chunks[2]+" ("+chunks[0]+")";
				} else if (!chunks[5].isEmpty()) { //we have a person name
					return chunks[5]+" ("+chunks[0]+")";
				}
			}
		}
		
		return "";
	}

	public void setResponsibleParty(Object responsibleParty) {
		this.responsibleParty = arrayifyString(responsibleParty);
	}

	public String getIsHarvested() {
		return isHarvested;
	}

	public void setIsHarvested(String isHarvested) {
		this.isHarvested = isHarvested;
	}

	public String getDocLocale() {
		return docLocale;
	}

	public void setDocLocale(String docLocale) {
		this.docLocale = docLocale;
	}

	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public ArrayList<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(Object keyword) {
		this.keyword = arrayifyString(keyword);
	}

	public ArrayList<String> getPublishedForGroup() {
		return publishedForGroup;
	}

	public void setPublishedForGroup(Object publishedForGroup) {
		this.publishedForGroup = arrayifyString(publishedForGroup);
	}

	public ArrayList<String> getImage() {
		return image;
	}
	
	public String getImageAsThumbnail() {
		Iterator<String> it = image.iterator();
		while (it.hasNext()) {
			String s = it.next();
			String[] chunks = s.split("\\|");
			if (chunks.length>1 && chunks[0].equalsIgnoreCase(this.IMAGE_THUMBNAIL_ID)) {
				return chunks[1];
			}
		}
		return null;
	}

	public void setImage(Object image) {
		this.image = arrayifyString(image);
	}

	public ArrayList<String> getMdLanguage() {
		return mdLanguage;
	}

	public void setMdLanguage(Object mdLanguage) {
		this.mdLanguage = arrayifyString(mdLanguage);
	}

	public String getMaintenanceAndUpdateFrequency_text() {
		return maintenanceAndUpdateFrequency_text;
	}

	public void setMaintenanceAndUpdateFrequency_text(String maintenanceAndUpdateFrequency_text) {
		this.maintenanceAndUpdateFrequency_text = maintenanceAndUpdateFrequency_text;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(String isTemplate) {
		this.isTemplate = isTemplate;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ArrayList<String> getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = arrayifyString(status);
	}

	public String getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(String updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public ArrayList<String> getGeoBox() {
		return geoBox;
	}
	
	public ArrayList<GeoBox> getGeoBoxes() {
		if (geoBox!=null && !geoBox.isEmpty()) {
			ArrayList<GeoBox> boxes = new ArrayList();
			Iterator<String> it = geoBox.iterator();
			while (it.hasNext()) {
				String s = it.next();
				//Parse GeoBox String (format W|S|E|N)
				String[] chunks = s.split("\\|");
				GeoBox box  = new GeoBox(Double.parseDouble(chunks[3]),
											Double.parseDouble(chunks[2]),
											Double.parseDouble(chunks[1]),
											Double.parseDouble(chunks[0]));
				boxes.add(box);
			}
			return boxes;
		}
		return null;
	}
	
	public GeoBox getFirstGeoBox() {
		ArrayList<GeoBox> boxes = getGeoBoxes();
		if (boxes==null) {
			return null;
		}
		return boxes.get(0);
	}
	
	public class GeoBox {
		private double North, East, West, South;
		
		public GeoBox(double n, double e, double s, double w) {
			this.North = n;
			this.East = e;
			this.South = s;
			this.West = w;
		}

		public double getNorth() {
			return North;
		}

		public void setNorth(double north) {
			North = north;
		}

		public double getEast() {
			return East;
		}

		public void setEast(double east) {
			East = east;
		}

		public double getWest() {
			return West;
		}

		public void setWest(double west) {
			West = west;
		}

		public double getSouth() {
			return South;
		}

		public void setSouth(double south) {
			South = south;
		}
		
	}

	public void setGeoBox(Object geoBox) {
		this.geoBox = arrayifyString(geoBox);
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public ArrayList<String> getLink() {
		return link;
	}

	public void setLink(Object link) {
		this.link = arrayifyString(link);
	}

	public ArrayList<String> getDatasetLang() {
		return datasetLang;
	}

	public void setDatasetLang(Object datasetLang) {
		this.datasetLang = arrayifyString(datasetLang);
	}

	public String getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(String userinfo) {
		this.userinfo = userinfo;
	}

	public ArrayList<String> getTopicCat() {
		return topicCat;
	}

	public void setTopicCat(Object topicCat) {
		this.topicCat = arrayifyString(topicCat);
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public ArrayList<String> getStatus_text() {
		return status_text;
	}

	public void setStatus_text(Object status_text) {
		this.status_text = arrayifyString(status_text);
	}

	public String getStandardName() {
		return standardName;
	}

	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String get_abstract() {
		return _abstract;
	}

	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}

	public String getKeywordGroup() {
		return keywordGroup;
	}

	public void setKeywordGroup(String keywordGroup) {
		this.keywordGroup = keywordGroup;
	}

	public String getGroupOwner() {
		return groupOwner;
	}

	public void setGroupOwner(String groupOwner) {
		this.groupOwner = groupOwner;
	}

	public String get_locale() {
		return _locale;
	}

	public void set_locale(String _locale) {
		this._locale = _locale;
	}

	public GeonetInfo get_geonet_info() {
		return _geonet_info;
	}

	public void set_geonet_info(GeonetInfo _geonet_info) {
		this._geonet_info = _geonet_info;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/*
	 * if object is a String, make it an arraylist of one String
	 * (Workaround to an issue in the json returned by the search,
	 * where a list, if only one item, is made a simple String,
	 * while if several items, is made an array of strings)
	 */
	public ArrayList<String> arrayifyString(Object obj) {
		ArrayList<String> arr = new ArrayList<String>();
		if (obj instanceof java.lang.String) {
			arr.add((String) obj);
		} else if (obj instanceof java.util.ArrayList) {
			arr = (ArrayList<String>) obj;
		} else {
			return null;
		}
		return arr;
	}

	public String getIdxError() {
		return idxError;
	}

	public void setIdxError(String idxError) {
		this.idxError = idxError;
	}
}
