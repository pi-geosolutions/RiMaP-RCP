package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pigeo.rimap.rimaprcp.core.utils.JsonUtils;


public class ContactEntry {
	int inhabitants;
	String contact_mode;
	String tel;
	String mail;
	String contact_mode_other;
	Date date;
	
	public ContactEntry(int inhabitants, String contact_mode, String tel, String mail, String contact_mode_other,
			Date date) {
		this.inhabitants = inhabitants;
		this.contact_mode = contact_mode;
		this.tel = tel;
		this.mail = mail;
		this.contact_mode_other = contact_mode_other;
		this.date = date;
	}
	
	public ContactEntry(JsonNode node) {
		this.inhabitants = JsonUtils.parseInt(node, "total_inhabitants", 0);
		this.contact_mode = JsonUtils.parseString( node, "contact_mode", "unspecified");
		this.tel = JsonUtils.parseString( node, "tel", "");
		this.mail = JsonUtils.parseString( node, "mail", "");
		this.contact_mode_other = JsonUtils.parseString( node, "contact_mode_other", "");
		this.date = JsonUtils.parseDate(node, "date");
	}
	
	public int getInhabitants() {
		return inhabitants;
	}
	public void setInhabitants(int inhabitants) {
		this.inhabitants = inhabitants;
	}
	public String getContact_mode() {
		return contact_mode;
	}
	public void setContact_mode(String contact_mode) {
		this.contact_mode = contact_mode;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getContact_mode_other() {
		return contact_mode_other;
	}
	public void setContact_mode_other(String contact_mode_other) {
		this.contact_mode_other = contact_mode_other;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
