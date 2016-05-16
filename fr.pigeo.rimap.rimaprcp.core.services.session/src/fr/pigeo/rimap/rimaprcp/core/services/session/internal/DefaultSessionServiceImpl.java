package fr.pigeo.rimap.rimaprcp.core.services.session.internal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import dialogs.LoginDialog;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;

public class DefaultSessionServiceImpl implements ISessionService {
	private Session session;

	@Inject
	IEclipseContext context;

	@Inject
	IPreferencesService prefService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	Shell shell;

	@Inject
	@Translation
	Messages messages;

	@Inject
	Logger logger;

	public DefaultSessionServiceImpl() {
	}

	@Override
	public Session getSession() {
		if (session != null) {
			return session;
		}
		// else
		return this.openSession(false);
	}

	@Override
	public Session openSession(boolean anonymous) {
		// Retrieve Preferences;
		String baseurl = prefService.getString(SessionConstants.PREFERENCES_NODE, SessionConstants.P_BASE_URL, null,
				null);

		// if baseurl is null, we open an anonymous session
		// else we open a login dialog
		session = new Session(baseurl == null);
		if (baseurl != null) {
			String loginService = prefService.getString(SessionConstants.PREFERENCES_NODE,
					SessionConstants.P_LOGIN_SERVICE, SessionConstants.LOGIN_SERVICE, null);
			session.setAuthentificationURL(baseurl + loginService);
			logger.info("Session service full URL : %s", session.getAuthentificationURL());
			askForLogin();

			//TODO: check session is validated then send session_validated event
		}
		return session;
	}	

	@Override
	public boolean closeSession() {
		//TODO: send session_closed event
		session = null;
		return true;
	}

	@Override
	public boolean isCredentialValid() {
		if (session == null) {
			session = openSession(false);
		}
		return session.getCredsCheckLevel() > 0;
	}

	private void askForLogin() {
		if (session == null) {
			return;
		}

		// shell can be null => no parent shell, only the dialog, detached
		LoginDialog dialog = new LoginDialog(shell);
		// inject dialog, so that it gets the translation service
		ContextInjectionFactory.inject(dialog, context);

		if (dialog.open() == Window.OK) {
			String username = dialog.getUser();
			String password = dialog.getPassword();
			session.setUsername(username);
			session.setPassword(password);
			logger.info("Credentials set for %s. Now checking for their validity...", username);
			this.tryOpenOnlineSession();
			dialog.dispose();
		}

	}

	private void tryOpenOnlineSession() {
		String geonetworkSessionID = getGeonetworkSessionID();
		logger.info("Jsession ID : " + geonetworkSessionID);
		switch (geonetworkSessionID) {
		case "":
			logger.warn("Returned sessionID is empty String. This shouldn't occur");
			OpenNoConnectionMessage(session);
			break;
		case SessionConstants.RETURNCODE_AUTH_FAILURE:
			MessageDialog.openError(shell, "Wrong credentials",
					"Authentification failure. " + "Try login again or consider continuing as guest");
			this.askForLogin();
			break;
		case SessionConstants.RETURNCODE_IOEXCEPTION:
		case SessionConstants.RETURNCODE_CLIENT_PROTOCOL_EXCEPTION:
			OpenNoConnectionMessage(session);
			break;
		default:
			// Means it worked
			logger.info("Authentification is valid (server-checked)");
			session.setSessionID(geonetworkSessionID);
			session.setCredsCheckLevel(SessionConstants.CREDS_LEVEL_WEB_VALIDATED);
		}
	}

	private void OpenNoConnectionMessage(Session session) {
		MessageDialog d = new MessageDialog(shell, messages.noConnectionMessageDialogTitle, null,
				messages.noConnectionMessageDialogMsg, MessageDialog.WARNING,
				new String[] { messages.noConnectionMessageDialogTryAgain, 
						messages.noConnectionMessageDialogGoLocal,
						messages.noConnectionMessageDialogAbort },
				0);
		int result = d.open();
		switch (result) {
		case 0:
			this.askForLogin();
			break;
		case 1: // TODO : check credentials locally (ie can decrypt the layertree) 
			checkCredentialsLocally();
			// storeAuth(user, pwd, null);
			break;
		case 2:
			System.exit(0);
		}
		logger.info("chosen " + result);
	}

	private void checkCredentialsLocally() {
		//TODO: Use SecureFilesIOService to check if the credentials are OK
	}

	private String getGeonetworkSessionID() {
		String url = session.getAuthentificationURL();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		try {

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("username", session.getUsername()));
			params.add(new BasicNameValuePair("password", session.getPassword()));

			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			CloseableHttpResponse response = httpclient.execute(httppost);
			/*
			 * Header[] headers = response.getAllHeaders(); for (Header h :
			 * headers) { System.out.println(h.getName() + ": " + h.getValue());
			 * }
			 */
			if (response.getHeaders("Location")[0].getValue().endsWith(SessionConstants.AUTH_URL_FAILURE_ENDSWITH)) {
				return SessionConstants.RETURNCODE_AUTH_FAILURE;
			} else {
				// return JSESSIONID value
				String cookieChain = response.getHeaders("Set-Cookie")[0].getValue();
				String[] chunks = cookieChain.split(";");
				for (String chunk : chunks) {
					if (chunk.startsWith(SessionConstants.AUTH_SESSIONID)) {
						String jsessionid = chunk.split("=")[1];
						return jsessionid;
					}
				}

			}
			// TODO : check if we can close response and httpclient without
			// response.close();
			// httpclient.close();
			httppost.releaseConnection();
		} catch (ClientProtocolException | UnknownHostException e) {
			logger.error(e.getClass() + ": Could not reach the server for authentification. "
					+ "It is probable that your internet connection or the server is down. ");
			// logger.error(e);
			return SessionConstants.RETURNCODE_CLIENT_PROTOCOL_EXCEPTION;
		} catch (IOException e) {
			logger.error("IOException: Error while getting authentification from the server. "
					+ "It is probable that your internet connection or the server is down. ");
			// logger.error(e);
			return SessionConstants.RETURNCODE_IOEXCEPTION;
		} finally {
			httppost.releaseConnection();
			// httpclient.close();
		}
		return ""; // shouldn't occur
	}

}
