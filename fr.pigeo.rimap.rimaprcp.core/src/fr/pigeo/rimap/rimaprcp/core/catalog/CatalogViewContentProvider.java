package fr.pigeo.rimap.rimaprcp.core.catalog;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CatalogViewContentProvider implements ITreeContentProvider{
	private static Object[] EMPTY_ARRAY = new Object[0];
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof INode) {
			INode node = (INode) parentElement;
			if (node.hasLeaves()) {
				return node.getLeaves().toArray();
			}
		} 
		//else
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		INode layer = (INode) element;
		return layer.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof INode) {
			INode node = (INode) element;
			return node.hasLeaves();
		} 
		return false;
	}

}
