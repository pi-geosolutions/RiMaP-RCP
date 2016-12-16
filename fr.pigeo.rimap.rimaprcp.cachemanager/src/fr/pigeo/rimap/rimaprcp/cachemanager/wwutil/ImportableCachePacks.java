package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ImportableCachePacks {
	protected String CACHEPACK_EXT = ".zip";
	protected Path rootPath;
	protected List<ImportableCachePack> cachePacksList = new ArrayList();

	public ImportableCachePacks(Path path) {
		setPath(path);
	}

	public ImportableCachePacks(String p) {
		setPath(p);
	}

	public ImportableCachePacks() {
	}

	public void setPath(Path p) {
		this.rootPath = p;
		cachePacksList.clear();
	}

	public void setPath(String p) {
		this.rootPath = Paths.get(p);
		cachePacksList.clear();
	}

	public List<ImportableCachePack> getList(boolean recursive) {
		// always refresh list
		this.cachePacksList.clear();
		listPacks(recursive);
		/*
		 * if (this.cachePacksList.isEmpty()) {
		 * listPacks();
		 * }
		 */
		return this.cachePacksList;
	}
	/*
	 * public void clearList() {
	 * this.cachePacksList.clear();
	 * }
	 */

	protected void listPacks(boolean recursive) {
		if (this.rootPath == null) {
			// TODO: log error
			return;
		}
		try {
			Files.walkFileTree(rootPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
					new LookForCachePacksFiles(rootPath, this, recursive));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private void addPack(Path path) {
		// System.out.println("scanning path " + path);
		ImportableCachePack pack = new ImportableCachePack(path);
		boolean isWWJ = pack.isWWJCachePack();
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
}
