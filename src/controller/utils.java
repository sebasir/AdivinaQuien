package controller;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.commons.codec.binary.Base64;

public class utils {
	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };
	
	public static String encriptarClave(String clave) {
		clave = clave + Urls.publicKey.getUrl();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(clave.getBytes(Urls.charset.getUrl()));
			clave = Base64.encodeBase64String(md.digest());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return clave;
	}
	
	public static String encryptURL(String property) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Urls.algorithm.getUrl());
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(Urls.publicKey.getUrl().toCharArray()));
			Cipher pbeCipher = Cipher.getInstance(Urls.algorithm.getUrl());
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			return base64Encode(pbeCipher.doFinal(property.getBytes(Urls.charset.getUrl())));
		} catch (Exception e) {
			return null;
		}
	}

	public static String decryptURL(String property) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Urls.algorithm.getUrl());
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(Urls.publicKey.getUrl().toCharArray()));
			Cipher pbeCipher = Cipher.getInstance(Urls.algorithm.getUrl());
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			return new String(pbeCipher.doFinal(base64Decode(property)), Urls.charset.getUrl());
		} catch (Exception e) {
			return null;
		}
	}

	private static String base64Encode(byte[] bytes) {
		return new Base64().encodeAsString(bytes);
	}

	private static byte[] base64Decode(String property) {
		return new Base64().decode(property);
	}
}
