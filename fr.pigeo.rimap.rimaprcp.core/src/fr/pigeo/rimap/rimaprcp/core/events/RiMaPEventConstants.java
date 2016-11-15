package fr.pigeo.rimap.rimaprcp.core.events;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 *
 * Only used for constant definition for the RiMaP RCP project
 * 
 * @author jean.pommier@pi-geosolutions.fr
 *
 */
public interface RiMaPEventConstants {
	//Layer related
	String LAYER_ALLEVENTS			="fr/pigeo/rimap/rimaprcp/layer/*" ;
	String LAYER_CHECKED			="fr/pigeo/rimap/rimaprcp/layer/checked" ;
	String LAYER_CHECKCHANGE		="fr/pigeo/rimap/rimaprcp/layer/checkchange" ;
	//1st layer selected
	String LAYER_SELECTED			="fr/pigeo/rimap/rimaprcp/layer/selected/first" ;
	//all selected layers
	String LAYER_SELECTED_LAYERS	="fr/pigeo/rimap/rimaprcp/layer/selected/all" ;
	
	//INode related
	String FOLDERNODE_EXPANDCHANGE	="fr/pigeo/rimap/rimaprcp/inode/folder/folderchange";
	String CHECKABLENODE_CHECKCHANGE		="fr/pigeo/rimap/rimaprcp/inode/wms/checkchange";
	
	//Session related
	String SESSION_CHANGED			="fr/pigeo/rimap/rimaprcp/session/*";
	String SESSION_GUEST			="fr/pigeo/rimap/rimaprcp/session/guest";
	String SESSION_SERVER_VALIDATED	="fr/pigeo/rimap/rimaprcp/session/servervalidated";
	String SESSION_LOCALLY_VALIDATED="fr/pigeo/rimap/rimaprcp/session/locallyvalidated";
	

	//Perspective related
	String LEAVING_PERSPECTIVE			="fr/pigeo/rimap/rimaprcp/perspective/leaving";
}
