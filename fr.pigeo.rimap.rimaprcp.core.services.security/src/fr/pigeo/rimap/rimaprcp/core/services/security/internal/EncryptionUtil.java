package fr.pigeo.rimap.rimaprcp.core.services.security.internal;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	// Hand-made salt
	private static final byte[] salt = "[B@2903c6ff".getBytes();

	public static Cipher makeCipher(int cryptmode, String key)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
		SecretKeyFactory kf;
		Cipher cipher = null;
		kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 8192, 128);
		SecretKey tmp = kf.generateSecret(spec);
		SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
		cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(cryptmode, secretKey);

		return cipher;
	}
}
