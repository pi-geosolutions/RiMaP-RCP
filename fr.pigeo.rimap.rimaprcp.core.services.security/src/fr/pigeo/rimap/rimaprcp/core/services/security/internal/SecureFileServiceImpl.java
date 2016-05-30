package fr.pigeo.rimap.rimaprcp.core.services.security.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

	Charset charset = StandardCharsets.UTF_8;

	@Inject
	Logger logger;

	@Inject
	ISessionService sessionService = null;

	@Inject
	@Optional
	IPreferencesService prefsService;

	// gracefully falls back on anonymous files if the encoded file does
	// not exist.
	// Uses preferences to set this behavior (can be disabled)
	@Override
	public byte[] getResourceAsByteArray(String resourcePath, String resourceName) {
		Path rawPath = getResourcePath(resourcePath, resourceName);
		Path encPath = getEncodedResourcePath(resourcePath, resourceName);
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
				rawPath = getPlainResourcePath(resourcePath, resourceName);
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
		// Path unencodedPath = getResourcePath(resourcePath, resourceName);
		Path encodedPath = getEncodedResourcePath(resourcePath, resourceName);
		return Files.isRegularFile(encodedPath);
	}

	@Override
	public boolean currentSessionCanDecrypt(String resourcePath, String resourceName) {
		Path encPath = getEncodedResourcePath(resourcePath, resourceName);
		String key = getKey();
		if (key == null) {
			return false;
		}
		return (getEncodedResourceAsByteArray(encPath, key) != null);
	}

	@Override
	public boolean setResource(byte[] input, String resourcePath, String resourceName) {
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
				destPath = getEncodedResourcePath(resourcePath, resourceName);
			} else {
				destPath = getResourcePath(resourcePath, resourceName);
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
	public boolean isResourceAvailable(String resourcePath, String resourceName) {
		Path path = getResourcePath(resourcePath, resourceName);
		boolean isAvailableUnencoded = (Files.exists(path) && Files.isRegularFile(path));
		Path encpath = getEncodedResourcePath(resourcePath, resourceName);
		boolean isAvailableEncoded = (Files.exists(encpath) && Files.isRegularFile(encpath));
		logger.info("Checking availability for file: \n" + path + "=" + isAvailableUnencoded + " \n" + encpath + "="
				+ isAvailableEncoded);
		return (isAvailableUnencoded || isAvailableEncoded);
	}

	/**
	 * If a Session is available, insert the Session's username in the file
	 * path:
	 * [resourcePath]/[username]/[resourceName]
	 * If not or if the session is anonymous, returns
	 * [resourcePath]/[resourceName]
	 * 
	 * @param resourcePath
	 * @param resourceName
	 * @return
	 */
	private Path getResourcePath(String resourcePath, String resourceName) {
		Session session = sessionService.getSession();
		String username = session.getUsername();
		Path path;
		if (username != null) {
			path = Paths.get(resourcePath, username, resourceName);
		} else {
			path = Paths.get(resourcePath, resourceName);
		}
		return path;
	}

	private Path getPlainResourcePath(String resourcePath, String resourceName) {
		return Paths.get(resourcePath, resourceName);
	}

	private Path getEncodedResourcePath(String resourcePath, String resourceName) {
		return getResourcePath(resourcePath, resourceName + SecureFileServiceImpl.ENCODED_FILE_SUFFIX);
	}

	/**
	 * @return the current Session's password, used as key for the encryption
	 */
	private String getKey() {
		Session session = sessionService.getSession();
		String key = session.getPassword();
		return key;
	}

}
