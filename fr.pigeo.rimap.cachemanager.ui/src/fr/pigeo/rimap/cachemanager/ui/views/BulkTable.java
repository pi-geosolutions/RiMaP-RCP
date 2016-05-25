package fr.pigeo.rimap.cachemanager.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class BulkTable {

	private TableViewer tableViewer;

	@Inject
	public BulkTable() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
	}
	
	

}
