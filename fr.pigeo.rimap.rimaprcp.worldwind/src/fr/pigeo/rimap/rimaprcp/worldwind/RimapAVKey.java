package fr.pigeo.rimap.rimaprcp.worldwind;

import gov.nasa.worldwind.avlist.AVKey;

public interface RimapAVKey extends AVKey {
    final String HAS_RIMAP_EXTENSIONS = "fr.pigeo.rimap.worldwind.avkey.HasRimapExtensions";
	
	//Layer related
    final String LAYER_TYPE = "fr.pigeo.rimap.worldwind.avkey.layertype";
    final String LAYER_PARENTNODE = "fr.pigeo.rimap.worldwind.avkey.ParentNode";
    final String LAYER_ISPOLYGONQUERYABLE = "fr.pigeo.rimap.worldwind.avkey.IsPolygonQueryable";
    final String LAYER_POLYGONQUERYPARAMS = "fr.pigeo.rimap.worldwind.avkey.PolygonQuery.params";
    final String LAYER_TIME_DIMENSION_ENABLED = "fr.pigeo.rimap.worldwind.avkey.layer.time.enabled";
    final String LAYER_TIME_DIMENSION_DEFAULT_VALUE = "fr.pigeo.rimap.worldwind.avkey.layer.time.defaultvalue";
    final String LAYER_TIME_DIMENSION_CURRENT_VALUE = "fr.pigeo.rimap.worldwind.avkey.layer.time.currentvalue";
    final String LAYER_TIME_DIMENSION_VALUES = "fr.pigeo.rimap.worldwind.avkey.layer.time.values";
    final String LAYER_TIME_DIMENSION_ZONEDATETIMELIST = "fr.pigeo.rimap.worldwind.avkey.layer.time.zonedatetime.list";
}
