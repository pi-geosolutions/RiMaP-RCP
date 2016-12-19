package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import fr.pigeo.rimap.rimaprcp.cachemanager.events.CacheManagerEventConstants;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;

public class ImportableCachePacks {
	protected IEventBroker eventBroker;
	protected String CACHEPACK_EXT = ".zip";
	protected Path rootPath;
	protected List<ImportableCachePack> cachePacksList = new ArrayList();
	protected List<ImportableCachePack> checkedCachePacksList = new ArrayList();
	private boolean cancelInstallPacksJob = false;
	protected Job installPacksJob;

	// used for extract progress monitoring
	private long totalNbFiles = 0, processedFiles = 0;
	private int progress = 0;

	public ImportableCachePacks(Path path, IEventBroker eventBroker) {
		setPath(path);
		this.eventBroker = eventBroker;
	}

	public ImportableCachePacks(String p, IEventBroker eventBroker) {
		setPath(Paths.get(p));
		this.eventBroker = eventBroker;
	}

	public ImportableCachePacks(IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	public void setPath(Path p) {
		this.rootPath = p;
		cachePacksList.clear();
	}

	public void setPath(String p) {
		this.rootPath = Paths.get(p);
		cachePacksList.clear();
	}

	public List<ImportableCachePack> getCheckedCachePacksList() {
		return checkedCachePacksList;
	}

	public void setCheckedCachePacksList(List<ImportableCachePack> checkedCachePacksList) {
		this.checkedCachePacksList = checkedCachePacksList;
	}

	public List<ImportableCachePack> getList(boolean recursive) {
		// always refresh list
		this.cachePacksList.clear();
		listPacks(recursive);
		return this.cachePacksList;
	}

	protected void listPacks(boolean recursive) {
		if (this.rootPath == null) {
			// TODO: log error
			return;
		}
		try {
			Files.walkFileTree(rootPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
					new LookForCachePacksFiles(rootPath, this, recursive));
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	private void addPack(Path path) {
		// System.out.println("scanning path " + path);
		ImportableCachePack pack = new ImportableCachePack(path);
		if (pack.isWWJCachePack()) {
			this.cachePacksList.add(pack);
		}
	}

	private class LookForCachePacksFiles extends SimpleFileVisitor<Path> {
		private ImportableCachePacks boss;
		private Path root;
		private boolean recursive;

		public LookForCachePacksFiles(Path root, ImportableCachePacks boss, boolean recursive) {
			this.root = root;
			this.boss = boss;
			this.recursive = recursive;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			if (file.toString()
					.endsWith(boss.CACHEPACK_EXT)) {
				boss.addPack(file);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
			if (directory.compareTo(root) == 0 || recursive) {
				return FileVisitResult.CONTINUE;
			} else {
				return FileVisitResult.SKIP_SUBTREE;
			}
		}
	}

	private long getTotalNbFiles() {
		long total = 0;
		for (ImportableCachePack pack : getCheckedCachePacksList()) {
			total += pack.countFilesInPack();
		}
		return total;
	}

	public void installPacks() {
		System.out.println("install packs");
		ImportableCachePacks p = this;
		this.cancelInstallPacksJob = false;
		this.totalNbFiles = getTotalNbFiles();
		this.processedFiles = 0;
		this.progress = 0;
		installPacksJob = new Job("Installing selected packs") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String b = CacheUtil.linebreak;
				for (ImportableCachePack pack : getCheckedCachePacksList()) {
					eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_CONSOLE_MESSAGE,
							"Importing pack " + pack.getName() + b);
					try {
						URI zipFile = CacheUtil.makeURI(pack.getPath());
						try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, new HashMap<>());) {
							Path src = zipFileSys.getPath("/");
							Files.walkFileTree(src, new InstallPackFileVisitor(pack, zipFileSys));
						}
					} catch (IOException e) {
						e.printStackTrace();
						eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_CONSOLE_MESSAGE,
								"Error encourtered while importing pack " + pack.getName() + b + e.getLocalizedMessage()
										+ b);
						return Status.CANCEL_STATUS;
					}
					eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_CONSOLE_MESSAGE,
							"Successfully imported pack " + pack.getName() + b);
				}
				eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_PROGRESS_UPDATE, 100);
				return Status.OK_STATUS;
			}
		};

		// Start the Job
		installPacksJob.schedule();

	}

	public long computeAvailableSpaceOnCacheFileStore() {
		FileStore store = new BasicDataFileStore();
		File cacheRoot = store.getWriteLocation();
		long freeSpace = cacheRoot.getFreeSpace(); // unallocated / free disk
													// space
													// in bytes.
		return freeSpace;
	}

	private Path getCacheFileStoreWriteLocation() {
		FileStore store = new BasicDataFileStore();
		File cacheRoot = store.getWriteLocation();
		return Paths.get(cacheRoot.getAbsolutePath());
	}

	protected void reportProcessedFile(Path file) {
		this.processedFiles++;
		double currentProgress = Math.floor(100 * processedFiles / totalNbFiles);
		if (currentProgress > progress) {
			setProgress((int) currentProgress);
		}
	}

	private void setProgress(int val) {
		this.progress = val;

		System.out.println("Progress " + val);
		eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_PROGRESS_UPDATE, this.progress);
	}

	private class InstallPackFileVisitor extends SimpleFileVisitor<Path> {
		private ImportableCachePack boss;
		private FileSystem zipFileSys;
		private Path rootCachePath;
		private Path source;
		private Path target;
		private String b = CacheUtil.linebreak;

		public InstallPackFileVisitor(ImportableCachePack boss, FileSystem zipFileSys) {
			this.boss = boss;
			this.zipFileSys = zipFileSys;
			this.rootCachePath = getCacheFileStoreWriteLocation().toAbsolutePath();
			this.source = zipFileSys.getPath("/");
			this.target = this.rootCachePath;

		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			return copy(file);
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
			return copy(directory);
		}
		
		public FileVisitResult copy(Path file) throws IOException {
			Path targetFile = resolveZipPath(file);
			System.out.println("Copying " + targetFile.toAbsolutePath() +" from " +file.toAbsolutePath());
			if (!(Files.exists(targetFile) && 
					Files.getLastModifiedTime(file).compareTo(Files.getLastModifiedTime(targetFile))<=0)) {
				Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
				eventBroker.send(CacheManagerEventConstants.IMPORT_PACKAGE_CONSOLE_MESSAGE,
						"[Copy] " + targetFile
								.toString() + b);
			}
			reportProcessedFile(file);
			return FileVisitResult.CONTINUE;
		}

		public Path resolveZipPath(Path p) {
			Path localPath = pathTransform(this.target.getFileSystem(), p);
			localPath = Paths.get("/").relativize(localPath);
			return Paths.get(this.target.resolve(localPath)
					.toString());
		}

		public Path pathTransform(final FileSystem fs, final Path path) {
			Path ret = fs.getPath(path.isAbsolute() ? fs.getSeparator() : "");
			for (final Path component : path)
				ret = ret.resolve(component.getFileName()
						.toString());
			return ret;
		}

	}

}
