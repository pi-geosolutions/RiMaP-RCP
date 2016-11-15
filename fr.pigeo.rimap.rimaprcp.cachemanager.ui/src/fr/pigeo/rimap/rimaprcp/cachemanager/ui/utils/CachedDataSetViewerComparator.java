package fr.pigeo.rimap.rimaprcp.cachemanager.ui.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.CachedDataSet;

public class CachedDataSetViewerComparator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public CachedDataSetViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		CachedDataSet c1 = (CachedDataSet) e1;
		CachedDataSet c2 = (CachedDataSet) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = c1.getFilename()
					.compareTo(c2.getFilename());
			break;
		case 1:
			rc = c1.getNumLevels() > c2.getNumLevels() ? 1 : -1;
			break;
		case 2:
			if (c1.getDirectorySize() == null) {
				rc = -1;
			} else if (c2.getDirectorySize() == null) {
				rc = 1;
			} else {
				rc = c1.getDirectorySize() > c2.getDirectorySize() ? 1 : -1;
			}
			break;
		case 3:
			rc = c1.getLastModif() > c2.getLastModif() ? 1 : -1;
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
