package fr.pigeo.rimap.rimaprcp.core.ui.translation;

import java.lang.reflect.Field;

public class Messages {
	// Display menu
	public String menu_display_show;
	public String menu_display_show_Stars;
	public String menu_display_show_Stars_tooltip;
	public String menu_display_show_Compass;
	public String menu_display_show_Compass_tooltip;
	public String menu_display_show_WorldMap;
	public String menu_display_show_WorldMap_tooltip;
	public String menu_display_show_Scalebar;
	public String menu_display_show_Scalebar_tooltip;
	public String menu_display_show_ViewControls;
	public String menu_display_show_ViewControls_tooltip;
	public String menu_display_show_SkyGradient;
	public String menu_display_show_SkyGradient_tooltip;

	// Details part
	public String parts_layerdetails_title ;
	public String parts_layerdetails_isnolayer;
	public String parts_layerdetails_layername;
	public String parts_layerdetails_zoomtoextent;
	public String parts_layerdetails_description;
	public String parts_layerdetails_opacity;
	public String parts_layerdetails_showlegend;
	public String parts_layerdetails_showlegend_tooltip;
	public String parts_layerdetails_showmetadata;
	public String parts_layerdetails_showmetadata_tooltip;
	
	//Legend dialog
	public String dialog_legend_title;
	
	//Error messages
	public String catalog_load_error_title;
	public String catalog_load_error_msg;
	
	//Sort By combo box values
	public String sortby_relevance;
	public String sortby_changeDate;
	public String sortby_title;
	public String sortby_rating;
	public String sortby_popularity;
	public String sortby_denominatorDesc;
	public String sortby_denominatorAsc;
	
	//Extent combo box values
	public String extent_intersection;
	public String extent_within;
	
	/*-----------------------------------------*/
	/*------  Animations labels     -----------*/
	/*-----------------------------------------*/
	public String loading;
	
	//Error messages
	public String animations_service_error_httpclient;
	public String animations_service_error_urlnotset;
	
	//JFace overrides/translations
	public String CLOSE_LABEL;
	public String STOPANDCLOSE_LABEL;
	
	//AnimationsDialog translations
	public String animations_dialog_title;
	public String animations_dialog_close_label;
	public String animations_dialog_choosedataset;
	public String animations_dialog_lbl_layername;
	public String animations_dialog_layername_isnull;
	public String animations_dialog_lbl_extent;
	public String animations_dialog_extent_fullextent;
	public String animations_dialog_extent_viewextent;
	public String animations_dialog_extent_fullextent_ttip;
	public String animations_dialog_extent_viewextent_ttip;
	public String animations_dialog_lbl_resolution;
	public String animations_dialog_lbl_resolution_ttip;
	public String animations_dialog_grp_play;
	public String animations_dialog_load;
	public String animations_dialog_update;
	public String animations_dialog_date_label;
	public String animations_dialog_date_text;
	public String animations_dialog_btn_first_ttip;
	public String animations_dialog_btn_prev_ttip;
	public String animations_dialog_btn_playbackward_ttip;
	public String animations_dialog_btn_pause_ttip;
	public String animations_dialog_btn_playforward_ttip;
	public String animations_dialog_btn_next_ttip;
	public String animations_dialog_btn_last_ttip;
	public String animations_dialog_progressbar_ttip_wait;
	public String animations_dialog_progressbar_ttip_ready;
	public String animations_extent_invalid;
	
	//AnimationsModel translations
	public String ANIM_EXTENT_FULL;
	public String ANIM_EXTENT_VIEW;
	public String ANIM_EXTENT_CUSTOM;
	public String ANIM_EXTENT_UNDEFINED;
	
	//Animations Resolutions (combo values)
	public String ANIM_RESOL_HIGH;
	public String ANIM_RESOL_MEDIUM;
	public String ANIM_RESOL_LOW;

	public Messages() {
	}
}
