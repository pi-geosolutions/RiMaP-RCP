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
					authentificationURL;
	
	private boolean anonymous;
	// credsCheckLevel:
	//  - 2 if credentials have been validated by the web service (Geonetwork auth service)
	//  - 1 if no web connection, i.e. creds have only been checked locally (were successful 
	// for reading encrypted session cached data) 
	//  - 0 if not checked at all
	//  - (-1) if invalid (auth failure)
	private int credsCheckLevel = 0;
	
	public Session (boolean anon) {
		this.anonymous = anon;
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
		return anonymous;
	}

	/**
	 * @param anonymous the anonymous to set
	 */
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
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
}
