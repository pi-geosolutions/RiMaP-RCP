package fr.pigeo.rimap.rimaprcp.lifecycle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import fr.pigeo.rimap.rimaprcp.swt.LoginDialog;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class LifeCycleManager {
	private String splashPath = "splash.bmp";
	private Shell shell = null;
	private Preferences preferences;
	private String preferencesNode = "fr.pigeo.rimap.rimaprcp";
	private String baseurl = "";
	private String loginService = "j_spring_security_check";

	@Inject
	Logger logger;

	@PostContextCreate
	void postContextCreate(IApplicationContext appContext, Display display,
			IPreferencesService prefService /* default preferences */,
			WwjInstance wwj /*
							 * Needed to instanciate from custom config before
							 * any call to worldwind, like cache file path
							 */) {

		// close the static splash screen
		// appContext.applicationRunning();

		baseurl = prefService.getString("fr.pigeo.rimap.rimaprcp", "project.baseurl", null, null);
		loginService = prefService.getString("fr.pigeo.rimap.rimaprcp", "project.loginservice", this.loginService,
				null);
		if (baseurl != null) {
			this.askForLogin();
		}

		/*
		 * get the path where to store persisted data (layertree, etc) for cache
		 * management 1) get WorldWind cache path 2) go up 1 level and create
		 * Padre folder Then write it in the preferences
		 */
		FileStore store = new BasicDataFileStore();
		String cacheFolderName = prefService.getString("fr.pigeo.rimap.rimaprcp", "cache.rootname", "RiMaP", null);
		String cachePath = store.getWriteLocation().getParentFile() + File.separator + cacheFolderName;
		initCacheFolder(cachePath);
		Preferences config = preferences.node("config");
		config.put("cachePath", cachePath);

		System.out.println("Rimap cache storage path: " + cachePath);
	}

	private void askForLogin() {
		LoginDialog dialog = new LoginDialog(shell); // shell can be null => no
														// parent shell, only
														// the dialog, detached

		if (dialog.open() == Window.OK) {
			String username = dialog.getUser();
			String password = dialog.getPassword();
			this.tryOpenOnlineSession(username, password);
		}

	}

	private void tryOpenOnlineSession(String username, String password) {
		String geonetworkSessionID = getGeonetworkSessionID(username, password);
		logger.info("Jsession ID : " + geonetworkSessionID);
		switch (geonetworkSessionID) {
		case "":
			logger.warn("Returned sessionID is empty String. This shouldn't occur");
			break;
		case "authFailure":
			MessageDialog.openError(shell, "Wrong credentials",
					"Authentification failure. " + "Try login again or consider continuing as guest");
			this.askForLogin();
			break;
		case "UnknownHostException":
			MessageDialog d = new MessageDialog(shell, "Unable to connect", null,
					"Could not reach the server for authentification. "
							+ "It is probable that your internet connection or the server is down. ",
					MessageDialog.ERROR, new String[] { "Try again", "Use local cache files only", "Abort" }, 0);
			int result = d.open();
			switch (result) {
			case 0:
				this.askForLogin();
				break;
			case 1: // TODO : load locally using credentials
				break;
			case 2:
				System.exit(0);
			}
			logger.info("chosen " + result);
			break;
		default:
			logger.info("Authentification is valid (server-checked)");
			storeAuth(username, password, geonetworkSessionID);
		}
	}

	private void storeAuth(String username, String password, String geonetworkSessionID) {
		if (preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(this.preferencesNode);
		}
		Preferences user = preferences.node("user");
		user.put("name", username);
		user.put("password", password);
		user.put("JSESSIONID", geonetworkSessionID);
		try {
			// forces the application to save the preferences
			preferences.flush();
			logger.info("Stored auth information in preferences, node 'user'");
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	

	private String getGeonetworkSessionID(String username, String password) {
		Cookie cookieValue = null;
        try {
            String url = this.baseurl + this.loginService;
            String logininfo = "username="+URLEncoder.encode(username,"UTF-8")+"&password="+URLEncoder.encode(password,"UTF-8");

            HttpPost httppost = new HttpPost(url);

            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            StringEntity entity = new StringEntity(logininfo, "UTF-8");
            httppost.setEntity(entity);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            CloseableHttpResponse resp = httpclient.execute(httppost);
            Header[] headers =resp.getAllHeaders();
            for (Header h : headers) {

				System.out.println(h.getName()+": "+h.getValue());
			}
            cookieValue = httpclient.getCookieStore().getCookies().get(0);
            httpclient.getConnectionManager().shutdown();
        } catch (IOException e) {
            logger.error(e);
        }
		return ""; //shouldn't occur
	}

	/**
	 * Creates the directory and ancestors if needed
	 * 
	 * @param cachePath
	 */
	private void initCacheFolder(String cachePath) {
		File path = (new File(cachePath));
		if (path.isFile()) {
			logger.error("Oops ! Cache path points to a file. It should be a directory (or not exist). Exiting");
			// close the application
			System.exit(-1);
		}
		if (path.isDirectory()) {
			return;
		} else {
			path.mkdirs();
		}
	}

	private void setLocation(Display display, Shell shell) {
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle monitorRect = monitor.getBounds();
		Rectangle shellRect = shell.getBounds();
		int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
		int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}

	private Image createBackgroundImage(Shell parent) {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL url = FileLocator.find(bundle, new Path(this.splashPath), null);
		final Image splashImage = ImageDescriptor.createFromURL(url).createImage();
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				splashImage.dispose();
			}
		});
		return splashImage;
	}
}
