package fr.pigeo.rimap.rimaprcp.core.catalog;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

public class CatalogViewLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		// System.out.println(element.getClass().getName());
		INode node = (INode) element;
		cell.setImage(node.getImage());
		text.append(node.getName());

		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}
}