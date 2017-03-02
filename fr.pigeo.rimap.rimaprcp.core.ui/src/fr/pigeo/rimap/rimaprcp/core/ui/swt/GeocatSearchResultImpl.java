package fr.pigeo.rimap.rimaprcp.core.ui.swt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataEntity.GeoBox;
import fr.pigeo.rimap.rimaprcp.core.geocatalog.GeocatMetadataToolBox;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.WmsNode;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;

public class GeocatSearchResultImpl extends GeocatSearchResult {
	private GeocatMetadataToolBox searchTools;
	private WwjInstance wwjInst;
	private IEclipseContext context;
	private Image thumbnail;
	private GeocatMetadataEntity entity;
	private SurfacePolygon polygon = null;
	private Color highlightColor = SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

	private enum LinkMode {
		HTTP, WMS, GOOGLE_EARTH
	};

	public GeocatSearchResultImpl(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public GeocatSearchResultImpl(GeocatMetadataEntity entity, GeocatMetadataToolBox searchTools, WwjInstance wwjInst,
			IEclipseContext context, Composite parent, int style) {
		super(parent, style);
		this.wwjInst = wwjInst;
		this.context = context;
		load(entity, searchTools);
	}

	/*
	 * Loads values from the GeocatMetadataEntity into the form fields
	 */
	public void load(GeocatMetadataEntity entity, GeocatMetadataToolBox searchTools) {
		this.searchTools = searchTools;
		this.entity = entity;
		this.setTitle(entity.getDefaultTitle());
		/*
		 * this.txtTitle.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override
		 * public void mouseUp(MouseEvent e) {
		 * String mtdLink =
		 * "http://ne-risk.pigeo.fr/geonetwork/srv/fre/md.viewer#/pigeo_simple_view/183";
		 * Program.launch(mtdLink);
		 * }
		 * 
		 * });
		 */
		this.btnOpenMTD.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String mtdLink = searchTools.getFullMetadataViewPath(entity.get_geonet_info()
						.getUuid());
				Program.launch(mtdLink);
			}
		});
		// this.btnOpenMTD.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// String mtdLink =
		// searchTools.getFullMetadataViewPath(entity.get_geonet_info().getUuid());
		// Program.launch(mtdLink);
		// }
		// });
		this.setSummary(entity.get_abstract());
		this.setOriginator(entity.getFirstResponsibleParty());
		// set thumbnail
		String tn = entity.getImageAsThumbnail();
		String mtdid = entity.get_geonet_info()
				.getId();
		if (tn != null && mtdid != null && searchTools != null) {
			String url = searchTools.getFullResourcesServicePath(tn, mtdid);
			this.setThumbnail(url);
		}

		// configure buttons
		// Links button
		// Java 8 way of copying with filter
		List<String> onlineResources = entity.getLink();
		if (onlineResources != null) {
			List<String> links = onlineResources.stream()
					.filter(s -> s.contains("WWW:LINK"))
					.collect(Collectors.toList());
			configureCButton(this.btnLinks, links, LinkMode.HTTP, "icons/link-dropdown.png");

			// download button
			List<String> downloads = onlineResources.stream()
					.filter(s -> s.contains("WWW:DOWNLOAD"))
					.collect(Collectors.toList());
			configureCButton(this.btnDownloads, downloads, LinkMode.HTTP, "icons/download-dropdown.png");

			// interactive map button
			List<String> maps = onlineResources.stream()
					.filter(s -> s.contains("OGC:WMS"))
					.collect(Collectors.toList());
			configureCButton(this.btnMap, maps, LinkMode.WMS, "icons/globe-dropdown.png");
		}

	}

	private void configureButton(Button btn, List<String> list, LinkMode mode, String multipleValuesIconPath) {
		if (list == null || list.isEmpty()) {
			// btn.setVisible(false); //hide but keep occupied space
			btn.dispose();
			return;
		}
		if (list.size() == 1) {
			String elt = list.get(0);
			String[] chunks = elt.split("\\|");
			String desc = chunks[1].length() != 0 ? chunks[1] : chunks[0];
			btn.setToolTipText(desc);
			btn.addSelectionListener(new ResourceButtonSelectionListener(chunks, mode, entity, context));
		} else {
			btn.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", multipleValuesIconPath));
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					Shell shell = btn.getShell();
					Menu menu = new Menu(shell, SWT.POP_UP);
					list.forEach(s -> {
						String[] chunks = s.split("\\|");
						String desc = chunks[1].length() != 0 ? chunks[1] : chunks[0];
						MenuItem item = new MenuItem(menu, SWT.PUSH);
						item.setText(desc);
						item.addSelectionListener(new ResourceButtonSelectionListener(chunks, mode, entity, context));
					});
					Point loc = btn.getLocation();
					Rectangle rect = btn.getBounds();

					Point mLoc = new Point(loc.x - 1, loc.y + rect.height);

					menu.setLocation(shell.getDisplay()
							.map(btn.getParent(), null, mLoc));

					menu.setVisible(true);
				}
			});
		}

	}

	private void configureCButton(CButton btn, List<String> list, LinkMode mode, String multipleValuesIconPath) {
		btn.setBackgrounds(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY), SWTResourceManager.getColor(SWT.COLOR_GRAY));
		
		if (list == null || list.isEmpty()) {
			// btn.setVisible(false); //hide but keep occupied space
			btn.dispose();
			return;
		}
		if (list.size() == 1) {
			String elt = list.get(0);
			String[] chunks = elt.split("\\|");
			String desc = chunks[1].length() != 0 ? chunks[1] : chunks[0];
			btn.setToolTipText(desc);
			btn.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					switch (mode) {
					case GOOGLE_EARTH:
						// TODO : implement ?
						break;
					case WMS:
						String description = chunks[1].length() != 0 ? chunks[1] : chunks[0];
						String layername = chunks[1].length() != 0 ? chunks[0] : extractNameFromURL(chunks);
						String url = chunks[2].split("\\?")[0];

						if (context != null) {
							WmsNode layer = ContextInjectionFactory.make(WmsNode.class, context);
							layer.setName(description);
							layer.setLayers(layername);
							layer.setUrl(url);
							layer.setMetadata_uuid(entity.get_geonet_info()
									.getUuid());
							layer.setChecked(true);
							Layer wwjLayer = layer.getLayer();
							wwjInst.addLayer(wwjLayer);
						}
						break;
					case HTTP:
					default:
						Program.launch(chunks[2]);
					}
				}
				

				private String extractNameFromURL(String[] chunkss) {
					String name = chunkss[0];
					String[] url_split = chunkss[2].split("\\?");
					if (url_split.length > 1 && url_split[1].length() > 0) {
						for (String s : url_split[1].split("\\&")) {
							if (s.startsWith("layers=")) {
								name = s.split("=")[1];
								break;
							}
						}
					}
					return name;
				}

			});
			// btn.addSelectionListener(new
			// ResourceButtonSelectionListener(chunks, mode, entity, context));
		} else {
			btn.setImage(ResourceManager.getPluginImage("fr.pigeo.rimap.rimaprcp.core.ui", multipleValuesIconPath));
			btn.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event e) {
					Shell shell = btn.getShell();
					Menu menu = new Menu(shell, SWT.POP_UP);
					list.forEach(s -> {
						String[] chunks = s.split("\\|");
						String desc = chunks[1].length() != 0 ? chunks[1] : chunks[0];
						MenuItem item = new MenuItem(menu, SWT.PUSH);
						item.setText(desc);
						item.addSelectionListener(new ResourceButtonSelectionListener(chunks, mode, entity, context));
					});
					Point loc = btn.getLocation();
					Rectangle rect = btn.getBounds();

					Point mLoc = new Point(loc.x - 1, loc.y + rect.height);

					menu.setLocation(shell.getDisplay()
							.map(btn.getParent(), null, mLoc));

					menu.setVisible(true);
				}
			});
		}

	}

	private class ResourceButtonSelectionListener extends SelectionAdapter {
		private LinkMode mode;
		private String[] chunks;
		private IEclipseContext context;
		private GeocatMetadataEntity mtdEntity;

		public ResourceButtonSelectionListener(String[] chunks, LinkMode mode, GeocatMetadataEntity mtdEntity,
				IEclipseContext context) {
			this.mode = mode;
			this.chunks = chunks;
			this.context = context;
			this.mtdEntity = mtdEntity;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			switch (mode) {
			case GOOGLE_EARTH:
				// TODO : implement ?
				break;
			case WMS:
				String description = chunks[1].length() != 0 ? chunks[1] : chunks[0];
				String layername = chunks[1].length() != 0 ? chunks[0] : extractNameFromURL(chunks);
				String url = chunks[2].split("\\?")[0];

				if (context != null) {
					WmsNode layer = ContextInjectionFactory.make(WmsNode.class, context);
					layer.setName(description);
					layer.setLayers(layername);
					layer.setUrl(url);
					layer.setMetadata_uuid(mtdEntity.get_geonet_info()
							.getUuid());
					layer.setChecked(true);
					Layer wwjLayer = layer.getLayer();
					wwjInst.addLayer(wwjLayer);
				}
				break;
			case HTTP:
			default:
				Program.launch(chunks[2]);
			}
		}

		private String extractNameFromURL(String[] chunkss) {
			String name = chunkss[0];
			String[] url_split = chunkss[2].split("\\?");
			if (url_split.length > 1 && url_split[1].length() > 0) {
				for (String s : url_split[1].split("\\&")) {
					if (s.startsWith("layers=")) {
						name = s.split("=")[1];
						break;
					}
				}
			}
			return name;
		}
	}

	private class ResourceCButtonSelectionListener implements Listener {
		private LinkMode mode;
		private String[] chunks;
		private IEclipseContext context;
		private GeocatMetadataEntity mtdEntity;

		public ResourceCButtonSelectionListener(String[] chunks, LinkMode mode, GeocatMetadataEntity mtdEntity,
				IEclipseContext context) {
			this.mode = mode;
			this.chunks = chunks;
			this.context = context;
			this.mtdEntity = mtdEntity;
		}

		@Override
		public void handleEvent(Event e) {
			switch (mode) {
			case GOOGLE_EARTH:
				// TODO : implement ?
				break;
			case WMS:
				String description = chunks[1].length() != 0 ? chunks[1] : chunks[0];
				String layername = chunks[1].length() != 0 ? chunks[0] : extractNameFromURL(chunks);
				String url = chunks[2].split("\\?")[0];

				if (context != null) {
					WmsNode layer = ContextInjectionFactory.make(WmsNode.class, context);
					layer.setName(description);
					layer.setLayers(layername);
					layer.setUrl(url);
					layer.setMetadata_uuid(mtdEntity.get_geonet_info()
							.getUuid());
					layer.setChecked(true);
					Layer wwjLayer = layer.getLayer();
					wwjInst.addLayer(wwjLayer);
				}
				break;
			case HTTP:
			default:
				Program.launch(chunks[2]);
			}
		}

		private String extractNameFromURL(String[] chunkss) {
			String name = chunkss[0];
			String[] url_split = chunkss[2].split("\\?");
			if (url_split.length > 1 && url_split[1].length() > 0) {
				for (String s : url_split[1].split("\\&")) {
					if (s.startsWith("layers=")) {
						name = s.split("=")[1];
						break;
					}
				}
			}
			return name;
		}

	}

	public void setTitle(String title) {
		this.txtTitle.setText(title);
		// this.txtTitle.setToolTipText(title);
	}

	public void setSummary(String sum) {
		this.txtSummary.setText(sum);
		this.txtSummary.setToolTipText(sum);
	}

	public void setOriginator(String ref) {
		this.lblOriginator.setText(ref);
	}

	public void setThumbnail(String path) {
		// TODO: manage resources (Garbage collecting).
		// Maybe using SWTResourceManager
		URL url;
		try {
			/*
			 * url = new URL(path);
			 * ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			 * this.lblThumbnail.setImage(desc.createImage());
			 */
			this.thumbnail = getResizedImage(path, 100, 100);
			if (thumbnail != null) {
				this.lblThumbnail.setImage(thumbnail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Image getResizedImage(String path, int width, int height) throws MalformedURLException {
		Image scaled = null;
		URL url;
		url = new URL(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		Image image = desc.createImage(false);
		if (image != null) {
			// compute scale factor to fit in the bounds defined by width,
			// height
			int w = image.getBounds().width;
			int h = image.getBounds().height;
			double wScaleFactor = (double) width / w;
			double hScaleFactor = (double) height / h;
			double scaleFactor = Math.min(wScaleFactor, hScaleFactor);

			scaled = new Image(Display.getDefault(), image.getImageData()
					.scaledTo((int) (w * scaleFactor), (int) (h * scaleFactor)));
			image.dispose();
		}
		return scaled;
	}

	public SurfacePolygon getPolygon() {
		return this.getPolygon(java.awt.Color.YELLOW);
	}

	public SurfacePolygon getPolygon(java.awt.Color color) {
		if (this.polygon != null) {
			return polygon;
		}
		if (this.entity.getGeoBox() != null && !this.entity.getGeoBox()
				.isEmpty()) {
			// update color hint int the panel
			this.lblColorHint
					.setBackground(SWTResourceManager.getColor(color.getRed(), color.getGreen(), color.getBlue()));

			GeoBox box = this.entity.getFirstGeoBox();

			ArrayList<LatLon> surfaceLinePositions = new ArrayList<LatLon>();
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getEast()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getWest()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getSouth(), box.getWest()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getSouth(), box.getEast()));
			surfaceLinePositions.add(LatLon.fromDegrees(box.getNorth(), box.getEast()));

			// define apparence
			ShapeAttributes attr = new BasicShapeAttributes();
			attr.setOutlineWidth(2.0);
			attr.setOutlineOpacity(1);
			attr.setOutlineMaterial(new Material(color));
			attr.setEnableAntialiasing(true);
			attr.setDrawInterior(false);

			ShapeAttributes highlightAttributes = new BasicShapeAttributes(attr);
			highlightAttributes.setInteriorOpacity(0.3);
			highlightAttributes.setInteriorMaterial(attr.getOutlineMaterial());
			highlightAttributes.setDrawInterior(true);

			// create poly & add to WWJ
			this.polygon = new SurfacePolygon(attr, surfaceLinePositions);
			polygon.setHighlightAttributes(highlightAttributes);

			return polygon;
		}
		return null;
	}

	public void setHighlighted(boolean highlight) {
		if (this.isDisposed()) {
			return;
		}
		Color color;
		if (highlight) {
			color = this.highlightColor;
		} else {
			color = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		}
		this.setBackground(color);
		this.txtSummary.setBackground(color);
		this.lblOriginator.setBackground(color);
	}

	@Override
	public void dispose() {
		if (this.thumbnail != null)
			this.thumbnail.dispose();
		highlightColor.dispose();
		super.dispose();
	}

}
