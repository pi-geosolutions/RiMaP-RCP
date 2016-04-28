
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import fr.pigeo.rimap.rimaprcp.core.Central;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfoTarget;

public class FeatureInfoResultsPart {
	private TableViewer viewer;
	private Browser browser;
	private Locale locale;

	@Inject
	public FeatureInfoResultsPart() {

	}

	@PostConstruct
	public void postConstruct(Composite parent, Central central, MPart part, IEclipseContext context) {
		if(locale==null) {
			locale = (Locale) context.get(TranslationService.LOCALE);
		}
		part.setIconURI("platform:/plugin/fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui/icons/icon_featureinfo_16px.png");
		GridLayout gl_parent = new GridLayout(2, false);
		/*
		 * gl_parent.marginRight = 5; gl_parent.marginLeft = 5;
		 * gl_parent.horizontalSpacing = 5;
		 */
		gl_parent.marginWidth = 0;
		parent.setLayout(gl_parent);

		/*
		 * Label lblTitle = new Label(parent, SWT.NONE);
		 * lblTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		 * false, 2, 1)); lblTitle.setText("Query results");
			lblTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		 */
		ArrayList<FeatureInfoTarget> targets = (ArrayList<FeatureInfoTarget>) central
				.get("fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.FeatureInfoTargets");
		if (targets == null || targets.size() == 0) {
			return;
		}

		Label lblPos = new Label(parent, SWT.NONE);
		lblPos.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblPos.setText("Latitude " + targets.get(0).getPosition().getLatitude().toDMSString() + " | Longitude "
				+ targets.get(0).getPosition().getLongitude().toDMSString() + " | Altitude "
				+ String.format("%.2f", targets.get(0).getPosition().getAltitude()) + " m");

		// define the TableViewer
		viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		// create the columns
		// not yet implemented
		// createColumns(viewer);

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		table.setHeaderVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// create column for the Name of the layers
		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				FeatureInfoTarget fit = (FeatureInfoTarget) element;
				return fit.getLayer().getName();
			}
		});
		colName.getColumn().setWidth(200);
		colName.getColumn().setText("Layers");

		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		viewer.setInput(targets.toArray());
		
		//needed for Windows env. (elsewise, nothing is selected by default):
		browser.setUrl(targets.get(0).getLayer().buildFeatureInfoRequest(targets.get(0).getPosition(), locale.getISO3Country()).toString());
		

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					FeatureInfoTarget target = (FeatureInfoTarget) selection.getFirstElement();
					URL fiurl = target.getLayer().buildFeatureInfoRequest(target.getPosition(), locale.getISO3Country());
					System.out.println(fiurl.toString());
					browser.setUrl(fiurl.toString());
				}
			}
		});
		/*
		 * //Resizing support. But don't cope with resizing the browser
		 * alongside table.addListener(SWT.MouseDown, new Listener() {
		 * 
		 * public void handleEvent(Event e) {
		 * 
		 * Tracker tracker = new Tracker(table.getParent(), SWT.RESIZE);
		 * tracker.setStippled(true); Rectangle rect = table.getBounds();
		 * tracker.setRectangles(new Rectangle[] { rect }); if (tracker.open())
		 * { Rectangle after = tracker.getRectangles()[0];
		 * table.setBounds(after); } tracker.dispose(); } });
		 */
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}