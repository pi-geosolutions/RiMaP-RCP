package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import fr.pigeo.rimap.rimaprcp.worldwind.WwjInstance;
import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.event.BulkRetrievalEvent;
import gov.nasa.worldwind.event.BulkRetrievalListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.TiledImageLayer;
import gov.nasa.worldwind.retrieve.BulkRetrievable;
import gov.nasa.worldwind.retrieve.BulkRetrievalThread;
import gov.nasa.worldwind.retrieve.Progress;
import gov.nasa.worldwind.terrain.BasicElevationModel;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Tile;

public class Downloadable {
	private WWObject layer;
	private WwjInstance wwj;
	private IEventBroker evtBroker;
	private Sector currentSector;
	private boolean download = false;
	private long estimatedSize = -1;
	private double maxResolution = 0;
	private int maxLevel = 19;
	private double[] resolutions = null;
	protected BulkRetrievalThread thread = null;
	protected String packageDestination = "";
	protected Job zipJob;
	protected boolean cancelZipJob = false;
	private LevelSet levels = null;
	private FileStore datafilestore = null;
	private String layername = "unnamed";
	private boolean isSupported = false;

	public Downloadable(WWObject layer, WwjInstance wwj, IEventBroker evtBroker) {
		super();
		this.layer = layer;
		this.wwj = wwj;
		this.evtBroker = evtBroker;
		if (layer instanceof TiledImageLayer) {
			TiledImageLayer til = (TiledImageLayer) layer;
			this.levels = til.getLevels();
			this.datafilestore = til.getDataFileStore();
			this.layername = til.getName();
			this.isSupported = true;
		} else if (layer instanceof ElevationModel) {
			BasicElevationModel bem = (BasicElevationModel) layer;
			this.levels = bem.getLevels();
			this.datafilestore = bem.getDataFileStore();
			this.layername = bem.getName();
			this.isSupported = true;
		}
	}

	public WWObject getLayer() {
		return layer;
	}

	public void setLayer(WWObject layer) {
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
	}

	public int getMaxLevel() {
		return this.maxLevel;
	}

	public void setMaxLevel(int lev) {
		this.maxLevel = lev;
		this.setMaxResolution(this.getResolutions()[lev]);
	}

	public double getMaxResolution() {
		if (maxResolution == 0) {
			if (this.levels != null) {
				maxResolution = this.levels.getLastLevel()
						.getTexelSize()
						* wwj.getModel()
								.getGlobe()
								.getRadius();
			}
		}
		return maxResolution;
	}

	public double getMaxResolutionInRadians() {
		return maxResolution / wwj.getModel()
				.getGlobe()
				.getRadius();
	}

	public void setMaxResolution(double res) {
		this.maxResolution = res;
		evtBroker.post(CacheManagerEventConstants.MAXRESOLUTION_CHANGED, this);
	}

	public double[] getResolutions() {
		if (resolutions == null) {
			if (this.levels != null) {
				resolutions = new double[levels.getNumLevels()];
				for (int i = 0; i < levels.getNumLevels(); i++) {
					Level lev = levels.getLevel(i);
					double res = lev.getTexelSize() * wwj.getModel()
							.getGlobe()
							.getRadius();
					resolutions[i] = res;
				}
			} else {
				resolutions = new double[] { 100, 50, 20, 5, 1, 0.5, 0.1 };
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

	public String getPackageDestination() {
		return packageDestination;
	}

	public void setPackageDestination(String packageDestination) {
		this.packageDestination = packageDestination;
	}

	public void updateSize() {
		if (download && currentSector != null) {
			this.estimatedSize = ((BulkRetrievable) layer).getEstimatedMissingDataSize(currentSector,
					getMaxResolutionInRadians());
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

	public boolean intersectsCurrentSector() {
		boolean intersects = true;
		// deal with the case where the layer doesn't intersect the currently
		// selected sector
		try {
			Sector sector = (Sector) layer.getValue(AVKey.SECTOR);
			if (!currentSector.intersects(sector)) {
				intersects = false;
			}
		} catch (Exception ex) {
			// do nothing
		}
		return intersects;
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

//						System.out.printf("%s: item %s\n",
//								event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_SUCCEEDED) ? "Succeeded"
//										: event.getEventType().equals(BulkRetrievalEvent.RETRIEVAL_FAILED) ? "Failed"
//												: "Unknown event type",
//								event.getItem());
					}
				});
		thread.setName("Bulk retrieval thread (" + this.layername + ")");
		return thread;
	}

	public boolean isDownloadThreadActive() {
		if (this.thread == null) {
			return false;
		}
		return thread.isAlive();
	}

	public String getDownloadProgress() {
		if (download && !intersectsCurrentSector()) {
			return "off sector";
		}
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
		return Math.min(percent, 100) + " %";
	}

	public Sector getCurrentSector() {
		return currentSector;
	}

	public void setCurrentSector(Sector currentSector) {
		this.currentSector = currentSector;
	}

	public void stopThread() {
		if (this.thread != null && this.thread.isAlive()) {
			this.thread.interrupt();
		}
	}

	public boolean canExportPackage() {
		return getDownloadProgress().equalsIgnoreCase("completed");
	}

	public void exportPackage() {
		if (canExportPackage()) {
			evtBroker.post(CacheManagerEventConstants.EXPORT_PACKAGE, this);
		}
	}

	public Path getCacheLocation(boolean fullpath) {
		Path full = Paths.get(this.datafilestore.getWriteLocation()
				.getAbsolutePath(),
				this.levels.getFirstLevel()
						.getPath())
				.getParent();
		Path relative = Paths.get(this.levels.getFirstLevel()
				.getPath())
				.getParent();
		// System.out.println("full cache location path:" + full.toString());
		// System.out.println("relative cache location path: " +
		// relative.toString());
		return fullpath ? full : relative;
	}

	/*
	 * Too memory consuming, don't use this on large sectors
	 */
	// public List<Path> getFilesInCurrentSector() {
	// if (this.currentSector == null) {
	// return null;
	// }
	// List<Path> paths = new ArrayList<Path>();
	// for (Level lev : this.levels
	// .getLevels()) {
	// if (lev.getLevelNumber() > this.getMaxLevel()) {
	// continue;
	// }
	// TextureTile[][] tiles = l.getTilesInSector(this.currentSector,
	// lev.getLevelNumber());
	// for (TextureTile[] row : tiles) {
	// for (TextureTile tile : row) {
	// Path tilepath = Paths.get(l.getDataFileStore()
	// .getWriteLocation()
	// .getAbsolutePath(), tile.getPath());
	// paths.add(tilepath);
	// }
	// }
	// }
	// return paths;
	// }

	public boolean putThisTileInThePacket(Path tileRelativePath) {
		if (tileRelativePath.toString()
				.endsWith(".xml")) {
			// we keep all xml files. Normally there should only be the one
			// defining the layer
			return true;
		}
		if (currentSector == null) {
			return true;
		}
		boolean pttitp = false;
		try {
			String tilename = tileRelativePath.getFileName()
					.toString();
			int row = Integer.parseInt(tileRelativePath.getParent()
					.getFileName()
					.toString());
			int level = Integer.parseInt(tileRelativePath.getParent()
					.getParent()
					.getFileName()
					.toString());
			String srow = tilename.split("_")[1].split("\\.")[0];
			int col = Integer.parseInt(srow);

			// taken from BasicElevationModel.createTile combined with
			// Level.computeSectorForPosition
			// Compute the tile's SW lat/lon based on its row/col in the level's
			// data set.
			Angle dLat = this.levels.getLevel(level)
					.getTileDelta()
					.getLatitude();
			Angle dLon = this.levels.getLevel(level)
					.getTileDelta()
					.getLongitude();
			Angle latOrigin = this.levels.getTileOrigin()
					.getLatitude();
			Angle lonOrigin = this.levels.getTileOrigin()
					.getLongitude();
			Angle minLatitude = Tile.computeRowLatitude(row, dLat, latOrigin);
			Angle minLongitude = Tile.computeColumnLongitude(col, dLon, lonOrigin);
			Sector tileSector = new Sector(minLatitude, minLatitude.add(dLat), minLongitude, minLongitude.add(dLon));
			pttitp = currentSector.intersects(tileSector);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return pttitp;
	}

	public void zip() {
		Downloadable d = this;
		this.cancelZipJob = false;
		zipJob = new Job("Zip " + d.layername) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Map<String, String> attributes = new HashMap<>();
				attributes.put("create", "true");
				try {
					String pd = d.getPackageDestination();

					// URI zipFile = URI.create("jar:file:" +
					// d.getPackageDestination());
					// hack to make Windows accept the path
					URI zipFile = CacheUtil.makeURI(Paths.get(d.getPackageDestination()));
					try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes);) {
						Path src = d.getCacheLocation(true);
						Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
								new ExtractAndCopyTiles(d, zipFileSys));

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};

		// Start the Job
		zipJob.schedule();

	}

	public String getConsoleHeader() {
		String b = CacheUtil.linebreak;
		String header = "Exporting files " + b + "Source directory = " + this.getCacheLocation(true) + b
				+ "Target directory = " + this.getCacheLocation(false) + b + " in package file "
				+ this.getPackageDestination() + b + " Press Proceed to start the export" + b;
		return header;
	}

	private class ExtractAndCopyTiles extends SimpleFileVisitor<Path> {
		private Path source;
		private Path target;
		private FileSystem zipFileSys;
		private Downloadable downloadable;
		private long totalNbFiles = 0, treatedNbFiles = 0;
		private int progress = 0;

		public ExtractAndCopyTiles(Downloadable d, FileSystem zipFileSys) {
			this.source = d.getCacheLocation(true);
			this.target = d.getCacheLocation(false);
			this.zipFileSys = zipFileSys;
			this.downloadable = d;
			try {
				Files.walk(source)
						.forEach(path -> totalNbFiles++);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			return copyToZip(file);
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
			if (downloadable.isCancelZipJob()) {
				try {
					zipFileSys.close();
					Files.deleteIfExists(Paths.get(downloadable.getPackageDestination()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return FileVisitResult.TERMINATE;
			}
			return copyToZip(directory);
		}

		private FileVisitResult copyToZip(Path p) {
			treatedNbFiles++;
			double currentProgress = Math.floor(100 * treatedNbFiles / totalNbFiles);
			if (currentProgress > progress) {
				setProgress((int) currentProgress);
			}

			String b = CacheUtil.linebreak;
			if (!Files.isDirectory(p)) {
				if (downloadable.putThisTileInThePacket(p)) {
					Path targetInZip = zipFileSys.getPath(target.resolve(source.relativize(p))
							.toString());
					try {
						if (Files.isDirectory(p)) {
							// create non-already created intermediary
							// directories
							Files.createDirectories(targetInZip);
							evtBroker.send(CacheManagerEventConstants.EXPORT_PACKAGE_CONSOLE_MESSAGE,
									"[Create Directory] " + source.relativize(p) + b);
						} else {
							// create non-already created intermediary
							// directories :
							// due to the filter in the beginning of the
							// function,
							// directories will be filtered out
							Files.createDirectories(targetInZip.getParent());
							evtBroker.send(CacheManagerEventConstants.EXPORT_PACKAGE_CONSOLE_MESSAGE,
									"[Copy] " + source.relativize(p)
											.toString() + b);
							Files.copy(p, targetInZip, StandardCopyOption.REPLACE_EXISTING);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// evtBroker.send(CacheManagerEventConstants.EXPORT_PACKAGE_CONSOLE_MESSAGE,
					// "[Exclude] " + source.relativize(p) + " (off limits)" +
					// b);
				}
			}
			return FileVisitResult.CONTINUE;
		}

		private void setProgress(int val) {
			this.progress = val;
			evtBroker.send(CacheManagerEventConstants.DOWNLOAD_PROGRESS_UPDATE, this.progress);
		}
	}

	public boolean isCancelZipJob() {
		return cancelZipJob;
	}

	public void cancelZipping() {
		this.cancelZipJob = true;
	}

	public String getLayername() {
		return layername;
	}

	public void layer_SetEnabled(boolean enabled) {
		if (this.layer instanceof Layer) {
			((Layer) layer).setEnabled(enabled);
		}
		if (this.layer instanceof BasicElevationModel) {
			((BasicElevationModel) layer).setEnabled(enabled);
		}
	}

	public boolean layer_isEnabled() {
		if (this.layer instanceof Layer) {
			return ((Layer) layer).isEnabled();
		}
		if (this.layer instanceof BasicElevationModel) {
			return ((BasicElevationModel) layer).isEnabled();
		}
		return false;
	}

	public boolean isSupported() {
		return isSupported;
	}
}
