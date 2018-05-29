package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants;

public interface QueryEventConstants {
	String QUERY_ALL = "fr/pigeo/rimap/rimaprcp/query/*";
	
	// Polygon query related
	String POLYGONQUERY_ALL = "fr/pigeo/rimap/rimaprcp/query/polygonquery/*";
	String POLYGONQUERY_POLYGON_CLOSED = "fr/pigeo/rimap/rimaprcp/query/polygonquery/polygonclosed";
	String POLYGONQUERY_READY = "fr/pigeo/rimap/rimaprcp/query/polygonquery/ready";

	String TOOLITEM_SELECTED = "fr/pigeo/rimap/rimaprcp/query/toolitem/selected";

	String POLYGONQUERY_MS_SHOW_SMS_CONTACTS_LIST = "fr/pigeo/rimap/rimaprcp/query/mobileservice/contactslist/sms/show";
	String POLYGONQUERY_MS_SHOW_EMAIL_CONTACTS_LIST = "fr/pigeo/rimap/rimaprcp/query/mobileservice/contactslist/email/show";
}
