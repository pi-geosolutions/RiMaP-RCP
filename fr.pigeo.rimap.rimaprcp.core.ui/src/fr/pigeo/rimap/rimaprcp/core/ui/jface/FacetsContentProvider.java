package fr.pigeo.rimap.rimaprcp.core.ui.jface;

import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Dimension;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.jsonparsingobjects.Summary;

public class FacetsContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		//System.out.println(parentElement.getClass());
		if (parentElement instanceof Dimension) {
			return ((Dimension) parentElement).getCategory().toArray();
		} else if (parentElement instanceof Summary) {
			//filter out empty Dimensions
			return ((Summary) parentElement).getDimension().stream().filter(d->d.hasChildren()).collect(Collectors.toList()).toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Summary) {
			return ((Summary) element).hasChildren();
		}
		if (element instanceof Dimension) {
			return ((Dimension) element).hasChildren();
		}
		return false;
	}

}
