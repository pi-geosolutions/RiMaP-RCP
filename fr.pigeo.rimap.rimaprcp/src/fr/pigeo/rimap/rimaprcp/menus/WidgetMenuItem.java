
package fr.pigeo.rimap.rimaprcp.menus;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import fr.pigeo.rimap.rimaprcp.worldwind.Widget;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.layers.Layer;

public class WidgetMenuItem { 

	public static final String SHOW_WIDGET_COMMAND_ID = "fr.pigeo.rimap.rimaprcp.command.showWidget"; 
	public static final String SHOW_WIDGET_PARAM_ID = "fr.pigeo.rimap.rimaprcp.commandparameter.show_wwj_widget_ref"; 
	
	@Inject protected MApplication app; 

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, EModelService modelService, WwjInstance wwj) {
		MCommand command = this.getCommand(SHOW_WIDGET_COMMAND_ID);
				
		List<Widget> widgetsList = wwj.getWidgetsClassLists();
		Iterator<Widget> iterator = widgetsList.iterator();
		while (iterator.hasNext()) {
			Widget w = iterator.next();
			MHandledMenuItem mitem = modelService.createModelElement(MHandledMenuItem.class);
			mitem.setLabel("Show "+w.getLabel());
			mitem.setType(ItemType.CHECK);
			mitem.setSelected(w.isVisible());
			mitem.setCommand(command);
			MParameter p = MCommandsFactory.INSTANCE.createParameter();
			p.setName(SHOW_WIDGET_PARAM_ID);
			p.setValue(w.getWidgetClassName());
			mitem.getParameters().add(p);
			items.add(mitem);
		}
		

	}
	
	/**
	  * This method returns the first object of type {@link MCommand} among all commands registered with this application to match the provided element id String. 
	  * @param elementId the element id to match for 
	  * @return the matching {@link MCommand} object or null if none 
	  */ 
	 public MCommand getCommand(String elementId) { 
	  List<MCommand> commands = app.getCommands(); 
	   
	  for(MCommand command : commands) { 
	   if(command.getElementId().equals(elementId)) { 
	    return command; 
	   } 
	  } 
	   
	  return null; 
	 } 

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {

	}

}