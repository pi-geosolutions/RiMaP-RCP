package fr.pigeo.rimap.rimaprcp.core.services.security.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;

import fr.pigeo.rimap.rimaprcp.core.security.ISecureResourceService;
import fr.pigeo.rimap.rimaprcp.core.security.ISessionService;
import fr.pigeo.rimap.rimaprcp.core.security.Session;

public class SecureFileServiceImpl implements ISecureResourceService {
	private static String ENCODED_FILE_SUFFIX = ".enc";
	private static String guestUsername = "guest";

	Charset charset = StandardCharsets.UTF_8;

	@Inject
	Logger logger;

	@Inject
	ISessionService sessionService = null;

	@Inject
	@Optional
	IPreferencesService prefsService;

	@Override
	public byte[] getResourceAsByteArray(String resourcePath, String resourceName) {
		return this.getResourceAsByteArray(resourcePath, "", resourceName);
	}

	// gracefully falls back on anonymous files if the encoded file does
	// not exist.
	// Uses preferences to set this behavior (can be disabled)
	@Override
	public byte[] getResourceAsByteArray(String resourcePath, String category, String resourceName) {
		Path rawPath = getResourcePath(resourcePath, category, resourceName);
		Path encPath = getEncodedResourcePath(resourcePath, category, resourceName);
		String key = getKey();

		if (key != null && Files.isRegularFile(encPath)) {
			return getEncodedResourceAsByteArray(encPath, key);
		} else if (Files.isRegularFile(rawPath)) {
			return getUnencodedResourceAsByteArray(rawPath);
		} else {
			// try to fallback gracefully to anonymous cache data
			if (prefsService != null) {
				boolean gracefully_fallback = prefsService.getBoolean(SecurityConstants.PREFERENCES_NODE,
						SecurityConstants.FALLBACK_ON_ANONYMOUS_PREF_TAG,
						SecurityConstants.FALLBACK_ON_ANONYMOUS_PREF_DEFAULT, null);
				rawPath = getPlainResourcePath(resourcePath, category, resourceName);
				if (gracefully_fallback && Files.isRegularFile(rawPath)) {
					logger.info("Resource unavailable using credentials. Falling back to anonymous cached data");
					return getUnencodedResourceAsByteArray(rawPath);
				}
			} // else
			logger.info("Couldn't get Resource. Returning null");
			return null;
		}
	}

	private byte[] getUnencodedResourceAsByteArray(Path path) {
		byte[] data;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			logger.error("Could not read file %s", path.getFileName());
			e.printStackTrace();
			return null;
		}
		return data;
	}

	private byte[] getEncodedResourceAsByteArray(Path encPath, String key) {
		if (key == null) {
			return null;
		}

		try {
			Cipher cipher = EncryptionUtil.makeCipher(Cipher.DECRYPT_MODE, key);
			byte[] inputBytes = Files.readAllBytes(encPath);
			return cipher.doFinal(inputBytes);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException e) {
			logger.error("Error initializing decryption.");
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Could not decrypt %s. ", encPath.getFileName());
			// e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.error("Could not read file %s", encPath.getFileName());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getResourceAsString(String resourcePath, String resourceName) {
		byte[] output = getResourceAsByteArray(resourcePath, resourceName);

		return new String(output, StandardCharsets.UTF_8);
	}

	@Override
	public InputStream getResourceAsStream(String resourcePath, String resourceName) {
		byte[] input = getResourceAsByteArray(resourcePath, resourceName);

		return new ByteArrayInputStream(input);
	}

	@Override
	public boolean isResourceEncrypted(String resourcePath, String resourceName) {
		return this.isResourceEncrypted(resourcePath, "", resourceName);
	}

	@Override
	public boolean isResourceEncrypted(String resourcePath, String category, String resourceName) {
		Path encodedPath = getEncodedResourcePath(resourcePath, category, resourceName);
		return Files.isRegularFile(encodedPath);
	}

	@Override
	public boolean currentSessionCanDecrypt(String resourcePath, String resourceName) {
		return this.currentSessionCanDecrypt(resourcePath, "", resourceName);
	}

	@Override
	public boolean currentSessionCanDecrypt(String resourcePath, String category, String resourceName) {
		Path encPath = getEncodedResourcePath(resourcePath, category, resourceName);
		String key = getKey();
		if (key == null) {
			return false;
		}
		return (getEncodedResourceAsByteArray(encPath, key) != null);
	}

	@Override
	public boolean setResource(byte[] input, String resourcePath, String resourceName) {
		return this.setResource(input, resourcePath, "", resourceName);
	}

	@Override
	public boolean setResource(byte[] input, String resourcePath, String category, String resourceName) {
		Path destPath = null;
		String key = getKey();

		byte[] outputBytes;
		Cipher cipher = null;
		try {
			if (key != null) {
				try {
					cipher = EncryptionUtil.makeCipher(Cipher.ENCRYPT_MODE, key);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException e) {
					logger.error("Error initializing encryption. The resource %s will be stored unencrypted",
							resourceName);
					e.printStackTrace();
				}
			}
			if (cipher != null) {
				outputBytes = cipher.doFinal(input);
				destPath = getEncodedResourcePath(resourcePath, category, resourceName);
			} else {
				destPath = getResourcePath(resourcePath, category, resourceName);
				outputBytes = input;
			}
			// Create parent folder if necessary
			Files.createDirectories(destPath.getParent());

			Files.write(destPath, outputBytes, StandardOpenOption.CREATE);
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Could not write the file on disk");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean setResource(String input, String resourcePath, String resourceName) {
		byte[] inputBytes = input.getBytes(charset);
		return this.setResource(inputBytes, resourcePath, resourceName);
	}

	@Override
	public boolean setResource(InputStream input, String resourcePath, String resourceName) {
		try {
			byte[] inputBytes = IOUtils.toByteArray(input);
			return this.setResource(inputBytes, resourcePath, resourceName);
		} catch (IOException e) {
			logger.error("Error: could not read the inputStream");
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean isResourceAvailable(String resourcePath, String category, String resourceName) {
		Path path = getResourcePath(resourcePath, category, resourceName);
		boolean isAvailableUnencoded = (Files.exists(path) && Files.isRegularFile(path));
		Path encpath = getEncodedResourcePath(resourcePath, category, resourceName);
		boolean isAvailableEncoded = (Files.exists(encpath) && Files.isRegularFile(encpath));
		// logger.info("Checking availability for file: \n" + path + "=" +
		// isAvailableUnencoded + " \n" + encpath + "=" + isAvailableEncoded);
		return (isAvailableUnencoded || isAvailableEncoded);
	}

	@Override
	public boolean isResourceAvailable(String resourcePath, String resourceName) {
		return this.isResourceAvailable(resourcePath, "", resourceName);
	}

	/**
	 * If a Session is available, insert the Session's username in the file
	 * path:
	 * [resourcePath]/[username]/[resourceName]
	 * If not or if the session is anonymous, returns
	 * [resourcePath]/guest/[resourceName]
	 * 
	 * @param resourcePath
	 * @param category
	 *            structured like a folder structure. Defines the storage place
	 *            in the cache folder. Avoids putting it all at the root of
	 *            cache folder
	 * @param resourceName
	 * @return
	 */
	private Path getResourcePath(String resourcePath, String category, String resourceName) {
		Session session = sessionService.getSession();
		String username = session.getUsername();
		Path path;
		if (username == null) {
			username = guestUsername;
		}
		path = Paths.get(resourcePath, username, category, resourceName);
		return path;
	}

	private Path getResourcePath(String resourcePath, String resourceName) {
		return getResourcePath(resourcePath, "", resourceName);
	}

	private Path getPlainResourcePath(String resourcePath, String category, String resourceName) {
		return Paths.get(resourcePath, guestUsername, category, resourceName);
	}

	private Path getPlainResourcePath(String resourcePath, String resourceName) {
		return getPlainResourcePath(resourcePath, "", resourceName);
	}

	private Path getEncodedResourcePath(String resourcePath, String category, String resourceName) {
		return getResourcePath(resourcePath, category, resourceName + SecureFileServiceImpl.ENCODED_FILE_SUFFIX);
	}

	private Path getEncodedResourcePath(String resourcePath, String resourceName) {
		return getEncodedResourcePath(resourcePath, "", resourceName);
	}

	/**
	 * @return the current Session's password, used as key for the encryption
	 */
	private String getKey() {
		Session session = sessionService.getSession();
		String key = session.getPassword();
		return key;
	}

	@Override
	public boolean deleteResource(String resourcePath, String resourceName) {
		Path deletePath = getResourcePath(resourcePath, resourceName);
		try {
			Files.delete(deletePath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteResource(String resourcePath, String category, String resourceName) {
		Path deletePath = getResourcePath(resourcePath, category, resourceName);
		try {
			Files.delete(deletePath);
			return true;
		} catch (NoSuchFileException e) {
			//in case the file doesn't exist (already deleted, for instance)
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void deleteResources(String resourcePath, String category, String regex, boolean recursive) {
		Path fullPath = getResourcePath(resourcePath, category);
		if (recursive) {
			try {
				Files.walkFileTree(fullPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (Files.isRegularFile(file)) {
							deleteIfRegexMatch(file, regex);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			DirectoryStream<Path> stream;
			try {
				stream = Files.newDirectoryStream(fullPath);
				Iterator<Path> iter = stream.iterator();
				while (iter.hasNext()) {
					Path path = iter.next();
					if (Files.isRegularFile(path)) {
						deleteIfRegexMatch(path, regex);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected void deleteIfRegexMatch(Path file, String regex) throws IOException {
		if (Pattern.matches(regex, file.getFileName()
				.toString())) {
			Files.delete(file);
		}
	}

	@Override
	public void deleteResources(String resourcePath, String regex, boolean recursive) {
		this.deleteResources(resourcePath, "", regex, recursive);
	}

	@Override
	public void deleteResourcesListed(String resourcePath, String category, List<String> resourcesToDelete) {
		Path fullPath = getResourcePath(resourcePath, category);
		DirectoryStream<Path> stream;
		try {
			stream = Files.newDirectoryStream(fullPath);
			Iterator<Path> iter = stream.iterator();
			while (iter.hasNext()) {
				Path path = iter.next();
				if (Files.isRegularFile(path)) {
					if (resourcesToDelete.contains(stripEnc(path.getFileName()
							.toString()))) {
						logger.info("Deleting expired file " + path.getFileName().toString() + " (" + path.toString() + ")");
						Files.delete(path);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteResourcesNotListed(String resourcePath, String category, List<String> resourcesToKeep) {
		Path fullPath = getResourcePath(resourcePath, category);
		DirectoryStream<Path> stream;
		try {
			stream = Files.newDirectoryStream(fullPath);
			Iterator<Path> iter = stream.iterator();
			while (iter.hasNext()) {
				Path path = iter.next();
				if (Files.isRegularFile(path)) {
					if (!resourcesToKeep.contains(stripEnc(path.getFileName()
							.toString()))) {
						logger.info("Deleting expired file " + path.getFileName().toString() + " (" + path.toString() + ")");
						Files.delete(path);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * If a file is encoded, it will be suffixed by .enc extension. We need to remove this 
	 * for files comparison (e.g. when using deleteResourcesNotListed)
	 * @param filename
	 * @return
	 */
	private String stripEnc(String filename) {
		//You can test the regex on http://www.regexplanet.com/advanced/java/index.html
		String regex = "(.*)(\\"+ENCODED_FILE_SUFFIX+")$";
		String replace = "$1";
		return filename.replaceAll(regex, replace);
	}
}
