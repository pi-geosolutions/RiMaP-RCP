package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.event.BulkRetrievalEvent;
import gov.nasa.worldwind.event.BulkRetrievalListener;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.TiledImageLayer;
import gov.nasa.worldwind.retrieve.BulkRetrievable;
import gov.nasa.worldwind.retrieve.BulkRetrievalThread;
import gov.nasa.worldwind.retrieve.Progress;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.LevelSet;

public class Downloadable {
	private Layer layer;
	private WwjInstance wwj;
	private IEventBroker evtBroker;
	private Sector currentSector;
	private boolean download = false;
	private int minLevel = 0, maxLevel = 19;
	private long estimatedSize = 0;
	private double downloadprogress = -1;
	private double maxResolution = 0;
	private double[] resolutions = null;
	protected BulkRetrievalThread thread = null;

	public Downloadable(Layer layer, WwjInstance wwj, IEventBroker evtBroker) {
		super();
		this.layer = layer;
		this.wwj = wwj;
		this.evtBroker = evtBroker;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public WwjInstance getWwj() {
		return wwj;
	}

	public void setWwj(WwjInstance wwj) {
		this.wwj = wwj;
	}

	public boolean doDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
		updateSize();
		downloadprogress = download ? 0.2 : -1;
	}

	public int getLevel(String leveltype) {
		if (leveltype.equalsIgnoreCase("min")) {
			return getMinLevel();
		} else if (leveltype.equalsIgnoreCase("max")) {
			return getMaxLevel();
		}
		return 0;
	}

	public void setLevel(String leveltype, int level) {
		if (leveltype.equalsIgnoreCase("min")) {
			setMinLevel(level);
		} else if (leveltype.equalsIgnoreCase("max")) {
			setMaxLevel(level);
		}
	}

	public String getLevelAsString(String levelType) {
		return String.format("%d", getLevel(levelType));
	}

	public void setLevelFromString(String levelType, String level) {
		setLevel(levelType, Integer.parseInt(level));
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	public double getMaxResolution() {
		if (maxResolution == 0) {
			if (layer instanceof TiledImageLayer) {
				TiledImageLayer l = (TiledImageLayer) layer;
				maxResolution = l.getLevels().getLastLevel().getTexelSize() * wwj.getModel()
						.getGlobe()
						.getRadius();
			}
		}
		return maxResolution;
	}
	
	public double getMaxResolutionInRadians() {
		return maxResolution / wwj.getModel().getGlobe().getRadius();
	}

	public void setMaxResolution(double res) {
		this.maxResolution = res;

		evtBroker.post(CacheManagerEventConstants.MAXRESOLUTION_CHANGED, this);
	}

	public double[] getResolutions() {
		if (resolutions == null) {
			if (layer instanceof TiledImageLayer) {
				TiledImageLayer l = (TiledImageLayer) layer;
				LevelSet levels = l.getLevels();
				resolutions = new double[levels.getNumLevels()];
				for (int i = 0; i < levels.getNumLevels(); i++) {
					Level lev = levels.getLevel(i);
					double res = lev.getTexelSize() * wwj.getModel()
							.getGlobe()
							.getRadius();
					resolutions[i] = res;
				}
			}
			else {
				resolutions = new double[]{100,50,20,5,1,0.5,0.1};
			}
		}
		return resolutions;
	}

	public long getEstimatedSize() {
		return estimatedSize;
	}

	public void setEstimatedSize(long estimatedSize) {
		this.estimatedSize = estimatedSize;
	}

	public void updateSize() {
		if (download && currentSector != null) {
			this.estimatedSize = ((BulkRetrievable) layer).getEstimatedMissingDataSize(currentSector, getMaxResolutionInRadians());
		} else {
			estimatedSize = 0;
		}
		evtBroker.post(CacheManagerEventConstants.DOWNLOADABLE_SIZE_UPDATED, this.estimatedSize);
	}

	public void updateSector(Sector s) {
		this.currentSector = s;
		if (download) {
			updateSize();
		}
	}

	public BulkRetrievalThread startDownloadThread() {
		this.thread = ((BulkRetrievable) layer).makeLocal(this.currentSector, getMaxResolutionInRadians(),
				new BulkRetrievalListener() {
					public void eventOccurred(BulkRetrievalEvent event) {
						// This is how you'd include a retrieval listener.
						// Uncomment below to monitor downloads.
						// Be aware that the method is not invoked on the event
						// dispatch thread, so any interaction
						// with AWT or Swing must be within a
						// SwingUtilities.invokeLater() runnable.

						// System.out.printf("%s: item %s\n",
						// event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_SUCCEEDED)
						// ? "Succeeded"
						// :
						// event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_FAILED)
						// ? "Failed"
						// : "Unknown event type", event.getItem());
					}
				});
		thread.setName("Bulk retrieval thread (" + layer.getName() + ")");
		return thread;
	}
	
	public boolean isDownloadThreadActive() {
		if (this.thread == null) {
			return false;
		}
		return thread.isAlive();
	}

	public String getDownloadProgress() {
		if (this.thread == null) {
			return "-";
		}
		if (!this.thread.isAlive()) {
			return "completed";
		}
        Progress progress = thread.getProgress();
        int percent = 0;
        if (progress.getTotalCount() > 0) {
            percent = (int) ((float) progress.getCurrentCount() / progress.getTotalCount() * 100f);
        }
        return Math.min(percent, 100)+" %";
	}
}
