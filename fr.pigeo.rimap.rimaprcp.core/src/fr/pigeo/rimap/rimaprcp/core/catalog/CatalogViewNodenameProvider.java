package fr.pigeo.rimap.rimaprcp.core.catalog;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class CatalogViewNodenameProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider {
	
	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof INode) {
			INode node = (INode) element;
			StyledString styledString;
			if (node.isAvailable()) {
				styledString = new StyledString(node.getName());
			} else {
				Styler disabledStyler = new Styler() {
					public void applyStyles(TextStyle textStyle) {
						textStyle.strikeout = true;
					}
				};
				
				styledString = new StyledString(node.getName(), disabledStyler);
			}
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

	@Override
	public Color getForeground(Object element) {
		if (element instanceof INode) {
			INode node = (INode) element;
			StyledString styledString;
			if (!node.isAvailable()) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}