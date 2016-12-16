package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ImportableCachePack {
	private Path path;
	private boolean isWWJCachePack = false, informationsLoaded = false;
	private String name = "";

	public ImportableCachePack(Path path) {
		this.path = path.toAbsolutePath();
	}

	public ImportableCachePack(String p) {
		this(Paths.get(p));
	}

	public String getName() {
		this.loadInformations();
		return this.name;
	}

	public boolean isWWJCachePack() {
		this.loadInformations();
		return this.isWWJCachePack;
	}

	public Path getPath() {
		return path;
	}

	public void loadInformations() {
		this.loadInformations(false);
	}

	public void loadInformations(boolean reload) {
		if ((!informationsLoaded) || reload) {
			URI zipFile;
			try {
				zipFile = URI.create("jar:file:" + path.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
				this.isWWJCachePack = false;
				this.informationsLoaded = true;
				return;
			}
			try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, new HashMap<>());){
				Path src = zipFileSys.getPath("/");
				Files.walkFileTree(src, new GetAndReadXmlFile(path, this));
				this.isWWJCachePack = this.name.length() > 0;
			} catch (Exception e) {
				e.printStackTrace();
				this.isWWJCachePack = false;
			}
			this.informationsLoaded = true;
		}
	}

	private class GetAndReadXmlFile extends SimpleFileVisitor<Path> {
		private ImportableCachePack boss;
		private Path root;

		public GetAndReadXmlFile(Path root, ImportableCachePack boss) {
			this.root = root;
			this.boss = boss;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			boolean gotIt = false;
			if (file.toString()
					.endsWith(".xml")) {
				gotIt = tryGetInformation(file);
			}
			if (gotIt) {
				return FileVisitResult.TERMINATE;
			} else {
				return FileVisitResult.CONTINUE;
			}
		}

		private boolean tryGetInformation(Path file) {
			boolean ok = false;
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = domFactory.newDocumentBuilder();
				InputStream fis = Files.newInputStream(file);
				try {
					Document dDoc = builder.parse(fis);
					XPath xPath = XPathFactory.newInstance()
							.newXPath();
					Node node = (Node) xPath.evaluate("/Layer/DisplayName", dDoc, XPathConstants.NODE);
					if (node != null) {
						boss.name = node.getTextContent();
						ok = true;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return ok;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			// stop when we are going beyond dataset level (ie in tiles folders)
			if (CacheUtil.isNumeric(dir.getFileSystem()
					.toString())) {
				return FileVisitResult.SKIP_SUBTREE;
			}
			return FileVisitResult.CONTINUE;
		}

	}
}
