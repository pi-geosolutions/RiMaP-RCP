package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class GeocatSearchResult extends Composite {
	protected Composite headerComposite;
	protected Button btnCheck;
	protected Text txtTitle;
	protected Label lblThumbnail;
	protected Text txtSummary;
	protected Label lblColorHint;
	protected Text lblOriginator;
	protected Button btnOpenMTD;
	protected Composite buttonsBarComposite;
	protected Button btnDownloads;
	protected Button btnMap;
	protected Button btnLinks;
	
	public GeocatSearchResult(Composite parent, int style) {
		super(parent, SWT.NONE);
		initComponents();
	}
	private void initComponents() {
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setBackgroundMode(SWT.INHERIT_FORCE);
		setLayout(new GridLayout(3, false));
		
		this.setData("org.eclipse.e4.ui.css.id", "SearchResult");
		
				
				GridData gd_mtdPanel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_mtdPanel.heightHint = 250;
		this.setLayoutData(gd_mtdPanel);
		
		headerComposite = new Composite(this, SWT.NONE);
		headerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		headerComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		headerComposite.setLayout(new GridLayout(3, false));
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		btnCheck = new Button(headerComposite, SWT.CHECK);
		btnCheck.setToolTipText("Check to select this entry");
		
		txtTitle = new Text(headerComposite, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtTitle.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		txtTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		txtTitle.setEditable(false);
		txtTitle.setToolTipText("");
		txtTitle.setText("WorldClim : Données climatologiques interpolées a haute résolution: température annuelle moyenne - Bio1");
		
		btnOpenMTD = new Button(headerComposite, SWT.NONE);
		btnOpenMTD.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/plus-square.png"));
		btnOpenMTD.setToolTipText("Open Full Metadata Window in Browser");
		btnOpenMTD.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnOpenMTD.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		
		lblThumbnail = new Label(this, SWT.CENTER);
		GridData gd_lblThumbnail = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblThumbnail.widthHint = 100;
		lblThumbnail.setLayoutData(gd_lblThumbnail);
		lblThumbnail.setText(" ");
		
		txtSummary = new Text(this, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtSummary.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtSummary.setFont(SWTResourceManager.getFont("Sans", 8, SWT.NORMAL));
		txtSummary.setToolTipText("R É S U M É 1ÈRE PARTIE : PRINCIPES DIRECTEURS Introduction Objectifs et structure La rédaction de directives pour des technologies et ap-proches de gestion durable des terres en Afrique subsaha-rienne (ASS) fait partie du programme TerrAfrica de 2009- 2010. L’objectif de ces recommandations et études de cas est de contribuer à créer une cadre pour les investis-sements liés aux pratiques de gestion durable des terres (GDT). Le but est, en particulier, d’identifier, d’analyser, de discuter et de diffuser des pratiques de ");
		GridData gd_txtSummary = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_txtSummary.heightHint = 100;
		txtSummary.setLayoutData(gd_txtSummary);
		txtSummary.setText("R É S U M É 1ÈRE PARTIE : PRINCIPES DIRECTEURS Introduction Objectifs et structure La rédaction de directives pour des technologies et ap-proches de gestion durable des terres en Afrique subsaha-rienne (ASS) fait partie du programme TerrAfrica de 2009- 2010. L’objectif de ces recommandations et études de cas est de contribuer à créer une cadre pour les investis-sements liés aux pratiques de gestion durable des terres (GDT). Le but est, en particulier, d’identifier, d’analyser, de discuter et de diffuser des pratiques de ");
		
		lblColorHint = new Label(this, SWT.NONE);
		lblColorHint.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		GridData gd_lblColorHint = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_lblColorHint.widthHint = 5;
		lblColorHint.setLayoutData(gd_lblColorHint);
		lblColorHint.setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		
		lblOriginator = new Text(this, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		lblOriginator.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblOriginator.setToolTipText("NEPAD Planning and Coordinating Agency,FAO,WOCAT Secretariat");
		lblOriginator.setFont(SWTResourceManager.getFont("Sans", 8, SWT.ITALIC));
		lblOriginator.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lblOriginator.setText("NEPAD Planning and Coordinating Agency,FAO,WOCAT Secretariat");
		
		buttonsBarComposite = new Composite(this, SWT.NONE);
		buttonsBarComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		buttonsBarComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 3, 1));
		
		int btnWidth = 40;
		
		btnDownloads = new Button(buttonsBarComposite, SWT.NONE);
		btnDownloads.setLayoutData(new RowData(btnWidth, SWT.DEFAULT));
		btnDownloads.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnDownloads.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/download.png"));
		btnDownloads.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		
		btnMap = new Button(buttonsBarComposite, SWT.NONE);
		btnMap.setLayoutData(new RowData(btnWidth, SWT.DEFAULT));
		btnMap.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/globe.png"));
		btnMap.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnMap.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		
		btnLinks = new Button(buttonsBarComposite, SWT.NONE);
		btnLinks.setLayoutData(new RowData(btnWidth, SWT.DEFAULT));
		btnLinks.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", "icons/link.png"));
		btnLinks.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnLinks.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
	}
	
}
