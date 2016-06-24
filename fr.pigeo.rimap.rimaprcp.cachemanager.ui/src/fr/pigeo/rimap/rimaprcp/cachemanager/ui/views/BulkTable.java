package fr.pigeo.rimap.rimaprcp.cachemanager.ui.views;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.BulkLayersListTableComposite2;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;

public class BulkTable {

	private BulkLayersListTableComposite2 layersListComposite;

	@PostConstruct
	public void postConstruct(Composite parent, WwjInstance wwjInst, IEclipseContext context){
		this.layersListComposite = new BulkLayersListTableComposite2(parent, SWT.BORDER_SOLID, wwjInst);
		ContextInjectionFactory.inject(this.layersListComposite, context);
	}
}
