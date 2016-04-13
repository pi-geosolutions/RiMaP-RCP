package fr.pigeo.rimap.rimaprcp.catalog;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.WmsLayer;

public class PadreCatalogViewLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		// System.out.println(element.getClass().getName());
		AbstractLayer layer = (AbstractLayer) element;
		cell.setImage(layer.getImage());
		text.append(layer.getName());

		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}
}