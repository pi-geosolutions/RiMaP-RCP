package fr.pigeo.rimap.rimaprcp.core.services.session.internal;

public interface SessionConstants {
	String PREFERENCES_NODE = "fr.pigeo.rimap.rimaprcp";
	
	// preference strings
	String P_BASE_URL = "project.baseurl";
	String P_LOGIN_SERVICE = "project.services.login";
	
	//default values
	String LOGIN_SERVICE = "j_spring_security_check" ;
	String AUTH_SESSIONID="JSESSIONID";
	String AUTH_URL_FAILURE_ENDSWITH = "?failure=true";
	
	//Return codes
	String RETURNCODE_AUTH_FAILURE = "authFailure";
	String RETURNCODE_IOEXCEPTION = "IOException";
	String RETURNCODE_CLIENT_PROTOCOL_EXCEPTION = "ClientProtocolException";
	
	//Session Credential auth levels
	int CREDS_LEVEL_WEB_VALIDATED = 2;
	int CREDS_LEVEL_LOCAL_VALIDATED = 1;
	int CREDS_LEVEL_NULL = 0;
	int CREDS_LEVEL_VALIDATION_FAILED = -1;
	
}