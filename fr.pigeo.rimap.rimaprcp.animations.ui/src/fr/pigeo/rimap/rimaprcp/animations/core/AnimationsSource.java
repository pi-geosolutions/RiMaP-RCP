package fr.pigeo.rimap.rimaprcp.animations.core;

import java.util.List;

import org.jdom2.Element;

public class AnimationsSource {
	private String id, label, srs, info, timeunit, timestampformatter, timestampRegexMatch, timestampRegexFormat;
	private int timeextent, imagewidth, imageheight;
	private double minlon, maxlon, minlat, maxlat;
	
	private String serverPath, extension, serviceURL;
	private List<String> filenames;

	public AnimationsSource(String lbl) {
		this.label = lbl;
	}

	/**
	 * Parses a JDOM2 XML Element as input
	 * 
	 * @param dataset
	 */
	public AnimationsSource(Element dataset) {
		id = dataset.getChildText("id");
		label = dataset.getChildText("label");
		srs = dataset.getChildText("srs");
		info = dataset.getChildText("info");
		minlon = Double.parseDouble(dataset.getChild("geographicbounds")
				.getChildText("minlon"));
		maxlon = Double.parseDouble(dataset.getChild("geographicbounds")
				.getChildText("maxlon"));
		minlat = Double.parseDouble(dataset.getChild("geographicbounds")
				.getChildText("minlat"));
		maxlat = Double.parseDouble(dataset.getChild("geographicbounds")
				.getChildText("maxlat"));
		imagewidth = Integer.parseInt(dataset.getChild("imagesize")
				.getChildText("width"));
		imageheight = Integer.parseInt(dataset.getChild("imagesize")
				.getChildText("height"));
		timestampformatter = dataset.getChildText("timestampformatter");
		//timestampRegexMatch = dataset.getChildText("timestampRegexMatch ");
		//timestampRegexFormat = dataset.getChildText("timestampRegexFormat");
		// TODO read real regex declarations and update all geoportals configs
		// TODO meaning I have to update config-pigeo-animation.xml AND xsl/pigeo/geoportal/listAnimations.xsl
		// TODO (note : no need to reload the webapp afterwards)
		timestampRegexMatch = "mpe_(\\d{2})(\\d{2})(\\d{2})_(\\d{2})(\\d{2}).*";
		timestampRegexFormat = "20$1-$2-$3 T $4:$5";
		timeextent = Integer.parseInt(dataset.getChildText("timeextent"));
		timeunit = dataset.getChildText("timeunit");
	}

	public void setLabel(String lbl) {
		this.label = lbl;
	}

	public String getLabel() {
		return this.label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTimeunit() {
		return timeunit;
	}

	public void setTimeunit(String timeunit) {
		this.timeunit = timeunit;
	}

	public String getTimestampformatter() {
		return timestampformatter;
	}

	public void setTimestampformatter(String timestampformatter) {
		this.timestampformatter = timestampformatter;
	}

	public int getTimeextent() {
		return timeextent;
	}

	public void setTimeextent(int timeextent) {
		this.timeextent = timeextent;
	}

	public int getImagewidth() {
		return imagewidth;
	}

	public void setImagewidth(int imagewidth) {
		this.imagewidth = imagewidth;
	}

	public int getImageheight() {
		return imageheight;
	}

	public void setImageheight(int imageheight) {
		this.imageheight = imageheight;
	}

	public double getMinlon() {
		return minlon;
	}

	public void setMinlon(double minlon) {
		this.minlon = minlon;
	}

	public double getMaxlon() {
		return maxlon;
	}

	public void setMaxlon(double maxlon) {
		this.maxlon = maxlon;
	}

	public double getMinlat() {
		return minlat;
	}

	public void setMinlat(double minlat) {
		this.minlat = minlat;
	}

	public double getMaxlat() {
		return maxlat;
	}

	public void setMaxlat(double maxlat) {
		this.maxlat = maxlat;
	}

	public String getTimestampRegexMatch() {
		return timestampRegexMatch;
	}

	public void setTimestampRegexMatch(String timestampRegexMatch) {
		this.timestampRegexMatch = timestampRegexMatch;
	}

	public String getTimestampRegexFormat() {
		return timestampRegexFormat;
	}

	public void setTimestampRegexFormat(String timestampRegexFormat) {
		this.timestampRegexFormat = timestampRegexFormat;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public List<String> getFilenames() {
		return filenames;
	}

	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}

	public String toString() {
		return this.label;
	}

}
