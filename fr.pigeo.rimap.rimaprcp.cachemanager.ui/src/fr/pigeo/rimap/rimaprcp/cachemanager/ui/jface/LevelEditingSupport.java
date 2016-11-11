package fr.pigeo.rimap.rimaprcp.cachemanager.ui.jface;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import fr.pigeo.rimap.rimaprcp.cachemanager.wwutil.Downloadable;
import gov.nasa.worldwind.layers.BasicTiledImageLayer;

public class LevelEditingSupport extends EditingSupport {

	  private final TableViewer viewer;
	  private String levelType="min"; //"min" or "max"

	  public LevelEditingSupport(TableViewer viewer, String levelType) {
	    super(viewer);
	    this.viewer = viewer;
	    this.levelType = levelType;
	  }

	  @Override
	  protected CellEditor getCellEditor(Object element) {
		//int maxlevels = (int)  ((Downloadable) element).getLayer().getValue(AVKey.LEVEL_NUMBER);
		  //BasicTiledImageLayer l = (BasicTiledImageLayer) ((Downloadable) element).getLayer();
		  //l.computeLevelForResolution(sector, resolution)
		String[] levels = new String[20];
		for (int i=0; i<levels.length; i++)
		{
		  levels[i] = String.format("%d",i);
		}
		return new ComboBoxCellEditor(viewer.getTable(), levels);
	  }

	  @Override
	  protected boolean canEdit(Object element) {
	    return true;
	  }

	  @Override
	  protected Object getValue(Object element) {
	    return ((Downloadable) element).getLevel(levelType);
	  }

	  @Override
	  protected void setValue(Object element, Object userInputValue) {
	    ((Downloadable) element).setLevelFromString(levelType, String.valueOf(userInputValue));
	    viewer.update(element, null);
	  }
	} 