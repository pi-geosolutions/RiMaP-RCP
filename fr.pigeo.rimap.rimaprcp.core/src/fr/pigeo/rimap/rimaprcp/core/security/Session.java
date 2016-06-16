package fr.pigeo.rimap.rimaprcp.core.security;

/**
 * Session class: deals with session-related data (credentials, sessionID, etc)
 * The targeted session is geonetwork's user session
 * It is used in secure storage of the whole RiMaP session : dedicated folder to store
 * layertree, WMS capabilities, etc. Those files are encrypted using the geonetwork 
 * session password
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public class Session {
	private String username,
					password,
					sessionID,
					authentificationURL,
					profileURL,
					profile,
					profile_name,
					profile_surname,
					profile_email;
	
	// credsCheckLevel (use constants form, cf SessionConstants):
	//  - 2 if credentials have been validated by the web service (Geonetwork auth service)
	//  - 1 if no web connection, i.e. creds have only been checked locally (were successful 
	// for reading encrypted session cached data) 
	//  - 0 if not checked at all
	//  - (-1) if invalid (auth failure)
	private int credsCheckLevel = SessionConstants.CREDS_LEVEL_NULL;
	
	public Session () {
		this.reset();
	}

	private void reset() {
		this.username=null;
		this.password = null;
		this.sessionID = null;
		this.credsCheckLevel=SessionConstants.CREDS_LEVEL_NULL;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the anonymous
	 */
	public boolean isAnonymous() {
		return (this.username==null);
	}

	/**
	 * @param anonymous the anonymous to set
	 */
	public void setAnonymous() {
		this.reset();
	}

	/**
	 * @return the credsCheckLevel
	 */
	public int getCredsCheckLevel() {
		return credsCheckLevel;
	}

	/**
	 * @param credsCheckLevel the credsCheckLevel to set
	 */
	public void setCredsCheckLevel(int credsCheckLevel) {
		this.credsCheckLevel = credsCheckLevel;
	}

	public String getAuthentificationURL() {
		return authentificationURL;
	}

	public void setAuthentificationURL(String authentificationURL) {
		this.authentificationURL = authentificationURL;
	}

	public String getProfileURL() {
		return profileURL;
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getProfile_name() {
		return profile_name;
	}

	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}

	public String getProfile_surname() {
		return profile_surname;
	}

	public void setProfile_surname(String profile_surname) {
		this.profile_surname = profile_surname;
	}

	public String getProfile_email() {
		return profile_email;
	}

	public void setProfile_email(String profile_email) {
		this.profile_email = profile_email;
	}
}
