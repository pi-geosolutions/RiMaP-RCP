
package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContActs;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContactEntry;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.filter.ContActsFilter;

public class ContActsViewPart {
	@Inject
	MPart part;
	
	@Inject
	@Translation
	Messages messages;

	@PostConstruct
	public void postConstruct(Composite parent) {
		/*Label lbl = new Label(parent, SWT.NONE);
		lbl.setText((String) part.getTransientData()
				.get("message"));*/
		String mode = (String) part.getTransientData()
				.get("mode");
		ContActs contacts = (ContActs) part.getTransientData()
				.get("contacts");
		TableViewer tv = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);

		// make lines and header visible
		final Table tbl = tv.getTable();
		tbl.setLinesVisible(true);
		tbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tbl.setHeaderVisible(true);
		tv.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn colMode = createTableViewerColumn(tv, messages.polygonquery_ms_view_columnlabel_phone, 120);
		colMode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String mode = ((ContactEntry) element).getTel();
				return mode;
			}
		});
		TableViewerColumn col2 = createTableViewerColumn(tv, messages.polygonquery_ms_view_columnlabel_hab, 160);
		col2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int h = ((ContactEntry) element).getInhabitants();
				return String.valueOf(h);
			}
		});
		ContActsFilter filter = new ContActsFilter(mode);
		tv.addFilter(filter);
		tv.setInput(contacts.getContactsList());

	}
	private TableViewerColumn createTableViewerColumn(TableViewer parent, String header, int width) {
		TableViewerColumn column = new TableViewerColumn(parent, SWT.NONE);
		column.getColumn()
				.setText(header);
		column.getColumn()
				.setWidth(width);
		column.getColumn()
				.setResizable(true);
		column.getColumn()
				.setMoveable(true);

		return column;
	}

}