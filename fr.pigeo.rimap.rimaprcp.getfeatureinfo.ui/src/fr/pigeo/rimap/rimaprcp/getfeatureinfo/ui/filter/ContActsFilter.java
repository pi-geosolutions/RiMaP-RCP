package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContactEntry;

public class ContActsFilter extends ViewerFilter {
	String contact_mode="";
	
	public ContActsFilter(String contact_mode) {
		this.contact_mode = contact_mode;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ContactEntry ce = (ContactEntry) element;
		if (ce.getContact_mode().equalsIgnoreCase(this.contact_mode)) {
			return true;
		}
		return false;
	}

}
