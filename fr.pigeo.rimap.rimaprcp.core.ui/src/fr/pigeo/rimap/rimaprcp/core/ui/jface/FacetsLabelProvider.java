package fr.pigeo.rimap.rimaprcp.core.ui.jface;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Category;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Dimension;
import fr.pigeo.rimap.rimaprcp.core.ui.swt.GeocatSearchFormImpl;

public class FacetsLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		if (element instanceof Dimension) {
			Dimension d = (Dimension) element;
			text.append(d.getTranslatedLabel());
		}
		if (element instanceof Category) {
			Category c = (Category) element;
			text.append(c.get_label());
			text.append(" (" + c.get_count() + ") ", StyledString.COUNTER_STYLER);
			cell.setImage(GeocatSearchFormImpl.getCheckboxImage(c.isChecked()));
		}
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}

}
