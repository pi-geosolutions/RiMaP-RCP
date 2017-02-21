package fr.pigeo.rimap.rimaprcp.core.services.session.internal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import dialogs.LoginDialog;
import fr.pigeo.rimap.rimaprcp.core.constants.RimapConstants;
import fr.pigeo.rimap.rimaprcp.core.events.RiMaPEventConstants;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;
import fr.pigeo.rimap.rimaprcp.core.security.SessionConstants;

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

	@Inject
	IEventBroker eventBroker;

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
		String baseurl = prefService.getString(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
				RimapConstants.PROJECT_BASEURL_PREF_TAG, RimapConstants.PROJECT_BASEURL_PREF_DEFAULT, null);

		// Initiate HttpClient (sets a context variable
		this.getHttpClient();

		// if baseurl is null, we open an anonymous session
		// else we open a login dialog
		session = new Session();
		if (baseurl != null) {
			String loginService = prefService.getString(SessionConstants.PREFERENCES_NODE,
					SessionConstants.P_LOGIN_SERVICE, SessionConstants.LOGIN_SERVICE, null);
			session.setAuthentificationURL(baseurl + loginService);
			logger.info("Session service full URL : " + session.getAuthentificationURL());

			String profileService = prefService.getString(SessionConstants.PREFERENCES_NODE,
					SessionConstants.PROFILE_SERVICE_PREF_TAG, SessionConstants.PROFILE_SERVICE_PREF_DEFAULT, null);
			session.setProfileURL(baseurl + profileService);
			askForLogin();

			// TODO: check session is validated then send session_validated
			// event
		}
		return session;
	}

	@Override
	public boolean closeSession() {
		// TODO: send session_closed event
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
			logger.info("Credentials set for " + username + ". Now checking for their validity...");
			this.tryOpenOnlineSession();
			dialog.dispose();
		} else {
			// we need to reset session vars if necessary (e.g. in case of auth
			// failure, username + pwd were previously set nonetheless
			session.setAnonymous();
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
			MessageDialog.openError(shell, messages.wrongCredentialsDialogTitle, messages.wrongCredentialsDialogMsg);
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
			// session.setProfile(getMe());
			getProfile(session);
			//set variable in context to use for core expression (menu entries visibility)
			context.set("sessionProfile", session.getProfile());
			eventBroker.send(RiMaPEventConstants.SESSION_SERVER_VALIDATED, session);
		}
	}

	/**
	 * Gets profile information from xml.info?type=me geonetwork service
	 * @param s Session to store the profile information into
	 * @return true if valid profile XML  was returned
	 */
	private boolean getProfile(Session s) {
		String url = s.getProfileURL();
		CloseableHttpClient httpclient = this.getHttpClient();
		CloseableHttpResponse response;
		Document doc = null;
		try {
			response = httpclient.execute(new HttpGet(url));
			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK) {
				
				HttpEntity entity = response.getEntity();
				
				SAXBuilder sxb = new SAXBuilder();
				try {
					doc = sxb.build(entity.getContent());
					Element root = doc.getRootElement();
					Element me = root.getChild("me");
					if (me==null) {return false; }
					s.setProfile(me.getChildText("profile"));
					s.setProfile_name(me.getChildText("name"));
					s.setProfile_surname(me.getChildText("surname"));
					s.setProfile_email(me.getChildText("email"));
				} catch (UnsupportedOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} 
				finally {
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private void OpenNoConnectionMessage(Session session) {
		MessageDialog d = new MessageDialog(shell, messages.noConnectionMessageDialogTitle, null,
				messages.noConnectionMessageDialogMsg, MessageDialog.WARNING,
				new String[] { messages.noConnectionMessageDialogTryAgain, messages.noConnectionMessageDialogGoLocal,
						messages.noConnectionMessageDialogAbort },
				0);
		int result = d.open();
		switch (result) {
		case 0:
			this.askForLogin();
			break;
		case 1: // TODO : check credentials locally (ie can decrypt the
				// layertree)
			checkCredentialsLocally();
			break;
		case 2:
			System.exit(0);
		}
		logger.info("chosen " + result);
	}

	private void checkCredentialsLocally() {
		// Won't work because of loop-dependency. In place, we just try when
		// loading the catalog and display an error in case it won't load.

		/*
		 * boolean check = catalogService.getMainCatalog()
		 * .testCredentials(session.getUsername(), session.getPassword(), true);
		 * 
		 * if (check) {
		 * System.out.println(
		 * "~~~~~~~~~~~~ Creds locally validated ~~~~~~~~~~~~");
		 * session.setCredsCheckLevel(SessionConstants.
		 * CREDS_LEVEL_LOCAL_VALIDATED);
		 * } else {
		 * MessageDialog.openWarning(shell, "Credentials invalid",
		 * "Your credentials are invalid. Falling back to anonymous session");
		 * System.out.println(
		 * "~~~~~~~~~~~~ Creds are really invalid (even locally) ~~~~~~~~~~~~");
		 * session.setAnonymous();
		 * }
		 */

	}

	private String getGeonetworkSessionID() {
		String url = session.getAuthentificationURL();

		CloseableHttpClient httpclient = this.getHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("username", session.getUsername()));
			params.add(new BasicNameValuePair("password", session.getPassword()));

			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			CloseableHttpResponse response = httpclient.execute(httppost);

			if (response.getHeaders("Location")[0].getValue()
					.endsWith(SessionConstants.AUTH_URL_FAILURE_ENDSWITH)) {
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
			response.close();
			//httppost.releaseConnection();
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
		}
		return ""; // shouldn't occur
	}

	public CloseableHttpClient getHttpClient() {
		CloseableHttpClient client = context.get(CloseableHttpClient.class);

		if (client == null) {
			int timeout = prefService.getInt(RimapConstants.RIMAP_DEFAULT_PREFERENCE_NODE,
					RimapConstants.WEB_CONNECTION_TIMEOUT_PREF_TAG, RimapConstants.WEB_CONNECTION_TIMEOUT_PREF_DEFAULT,
					null);

			//Customized in order to allow multiple concurrent running connections when recovering the WMS capabilities at startup
			PoolingHttpClientConnectionManager oConnectionMgr = new PoolingHttpClientConnectionManager();
			oConnectionMgr.setMaxTotal(50);
			oConnectionMgr.setDefaultMaxPerRoute(20);
			
			RequestConfig config = RequestConfig.custom()
					.setCookieSpec(CookieSpecs.DEFAULT)
					.setConnectTimeout(timeout * 1000)
					.setConnectionRequestTimeout(timeout * 1000)
					.setSocketTimeout(timeout * 1000)
					.build();

			CookieStore httpCookieStore = new BasicCookieStore();

			client = HttpClientBuilder.create()
			        .setConnectionManager(oConnectionMgr)
					.setDefaultRequestConfig(config)
					.setDefaultCookieStore(httpCookieStore)
					.build();

			context.set(CloseableHttpClient.class, client);
		}
		return client;
	}

}
