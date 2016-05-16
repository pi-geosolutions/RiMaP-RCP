package fr.pigeo.rimap.rimaprcp.core.security;

public interface ISessionService {
	Session getSession();
	Session openSession(boolean anonymous);
	boolean closeSession();
	boolean isCredentialValid();
}
