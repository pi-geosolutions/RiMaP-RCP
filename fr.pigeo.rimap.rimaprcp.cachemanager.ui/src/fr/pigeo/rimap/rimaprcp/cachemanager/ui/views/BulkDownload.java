package fr.pigeo.rimap.rimaprcp.cachemanager.ui.views;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.cachemanager.ui.bulkdownload.BulkDownloadManager;
import fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface.BulkDownloadPanel;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class BulkDownload {

	@PostConstruct
	public void postConstruct(Composite parent, BulkDownloadManager bdm){
		BulkDownloadPanel panel = new BulkDownloadPanel(parent, SWT.BORDER_SOLID, bdm);
	}
}
