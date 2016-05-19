package fr.pigeo.rimap.rimaprcp.core.catalog;

import java.io.File;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

public class CatalogViewNodenameProvider extends LabelProvider implements IStyledLabelProvider {

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof INode) {
			INode node = (INode) element;
			StyledString styledString = new StyledString(node.getName());
			return styledString;
		}
		return new StyledString("unknown node");
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof INode) {
			INode node = (INode) element;
			return node.getImage();
		}

		return super.getImage(element);
	}

}