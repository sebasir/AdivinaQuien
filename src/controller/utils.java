package controller;

import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class utils {
	public static String encriptarClave(String clave) {
		clave = clave + "_4d1v1n4Qu13n";
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(clave.getBytes("ISO-8859-1"));
			byte raw[] = md.digest();
			clave = (new BASE64Encoder()).encode(raw);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return clave;
	}
}
