package fr.pigeo.rimap.rimaprcp.lifecycle;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
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

	@Inject
	Logger logger;

	@PostContextCreate
	void postContextCreate(IApplicationContext appContext, Display display,
			IPreferencesService prefService /* default preferences */,
			WwjInstance wwj /*Needed to instanciate from custom config before any call to worldwind, like cache file path*/) {
		final Shell shell = null;// new Shell(SWT.SHELL_TRIM);
		LoginDialog dialog = new LoginDialog(shell);

		// close the static splash screen
		// appContext.applicationRunning();

		// position the shell
		// setLocation(display, shell);

		Preferences preferences = InstanceScope.INSTANCE.getNode("fr.pigeo.rimap.rimaprcp");

		if (dialog.open() == Window.OK) {
			String username = dialog.getUser();
			String password = dialog.getPassword();
			Preferences user = preferences.node("user");
			user.put("name", username);
			user.put("password", password);
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

		try {
			// forces the application to save the preferences
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		/*
		 * Display d = Display.getCurrent();
		 * 
		 * final LoginDialog2 s = new LoginDialog2(d); Image image =
		 * createBackgroundImage(s); //s.setBackgroundImage(image);
		 * s.setSize(image.getBounds().width, image.getBounds().height);
		 * //s.pack(); Point p = s.getSize(); Rectangle r =
		 * s.getMonitor().getBounds(); s.setLocation(r.width / 2 - p.x / 2,
		 * r.height / 2 - p.y / 2);
		 * 
		 * //Platform.endSplash(); //deprecated. According to Vogella
		 * (book,132.3) prefer : appContext.applicationRunning();
		 * 
		 * s.open();
		 * 
		 * while (!s.isDisposed()) { if (!d.readAndDispatch()) { d.sleep(); } }
		 */
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
