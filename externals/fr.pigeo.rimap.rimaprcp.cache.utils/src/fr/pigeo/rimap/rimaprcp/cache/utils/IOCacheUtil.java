package fr.pigeo.rimap.rimaprcp.cache.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;

public class IOCacheUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final byte[] salt = "[B@2903c6ff".getBytes();

	private static Cipher makeCipher(int cryptmode, String key) {
		SecretKeyFactory kf;
		Cipher cipher = null;
		try {
			kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 8192, 128);
			SecretKey tmp = kf.generateSecret(spec);
			SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
			cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cryptmode, secretKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException e) {
			// TODO Auto-generated catch block

			System.out.println("Error: could not initiate encryption. Storage will be unencrypted");
			e.printStackTrace();
		}
		return cipher;
	}

	/**
	 * Stores the file to cache. With encryption if key is not null, plain if
	 * key is null
	 * 
	 * @param input
	 * @param destfile
	 * @param key
	 * @return
	 */
	public static boolean store(String input, File destfile, String key) {
		byte[] inputBytes;
		byte[] outputBytes;
		try {
			if (key != null) {
				Cipher cipher = IOCacheUtil.makeCipher(Cipher.ENCRYPT_MODE, key);

				inputBytes = input.getBytes();
				outputBytes = cipher.doFinal(inputBytes);
			} else {
				outputBytes = input.getBytes();
			}
			IOCacheUtil.createParentFolderIfNecessary(destfile);
			FileOutputStream os = new FileOutputStream(destfile);
			os.write(outputBytes);
			os.close();
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: could not write the file on disk");
			e.printStackTrace();
		}
		return true;
	};

	/**
	 * Gets the file's content, decrypting it if necessary.
	 * 
	 * @param fromfile
	 * @param key
	 * @return
	 */
	public static String retrieve(File fromfile, String key) {
		byte[] inputBytes;
		byte[] outputBytes;
		String str = null;
		try {
			FileInputStream is = new FileInputStream(fromfile);
			inputBytes = new byte[(int) fromfile.length()];
			is.read(inputBytes);
			
			if (key != null) {
				Cipher cipher = IOCacheUtil.makeCipher(Cipher.DECRYPT_MODE, key);
				outputBytes = cipher.doFinal(inputBytes);
			} else {
				outputBytes = inputBytes;
			}
			str = new String(outputBytes, StandardCharsets.UTF_8);
			is.close();
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/** Record the key to a text file for testing: **/
	private static void keyToFile(SecretKey key) {
		try {
			File keyFile = new File(System.getProperty("user.dir"), "keyfile.txt");
			FileWriter keyStream = new FileWriter(keyFile);
			String encodedKey = "\n" + "Encoded version of key:  " + key.getEncoded().toString();
			keyStream.write(key.toString());
			keyStream.write(encodedKey);
			keyStream.close();
		} catch (IOException e) {
			System.err.println("Failure writing key to file");
			e.printStackTrace();
		}
	}

	public static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		System.out.println("Salt: " + bytes.toString());
		return bytes;
	}

	private static void createParentFolderIfNecessary(File destfile) {
		File parentFolder = destfile.getParentFile();
		parentFolder.mkdirs();
	}
}
