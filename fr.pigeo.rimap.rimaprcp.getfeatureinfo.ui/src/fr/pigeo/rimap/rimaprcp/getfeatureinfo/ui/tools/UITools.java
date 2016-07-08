package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.tools;

import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

public class UITools {
	public static void disableSiblingToolItems(MToolItem item) {
		// disable all other toolItems
		List<MUIElement> elements = item.getParent()
				.getChildren();
		Iterator<MUIElement> it = elements.iterator();
		while (it.hasNext()) {
			MUIElement muiel = it.next();
			if (muiel instanceof MToolItem && muiel != item && muiel.getTags().equals(item.getTags())) {
				((MToolItem) muiel).setSelected(false);
			}
		}
	}
}
