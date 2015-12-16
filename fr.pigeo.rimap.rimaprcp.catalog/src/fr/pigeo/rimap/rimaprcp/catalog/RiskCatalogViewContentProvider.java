package fr.pigeo.rimap.rimaprcp.catalog;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.pigeo.rimap.rimaprcp.riskcatalog.AbstractLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.FolderLayer;
import fr.pigeo.rimap.rimaprcp.riskcatalog.LayerType;

public class RiskCatalogViewContentProvider implements ITreeContentProvider{
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
		if(parentElement instanceof FolderLayer) {
			FolderLayer folder = (FolderLayer) parentElement;
			return folder.getChildren().toArray();
		} else
			return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		AbstractLayer layer = (AbstractLayer) element;
		return layer.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof FolderLayer) {
			FolderLayer folder = (FolderLayer) element;
			return !folder.getChildren().isEmpty();
		} else
			return false;
	}

}
