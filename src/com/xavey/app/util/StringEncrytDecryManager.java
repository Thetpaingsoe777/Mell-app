package com.xavey.app.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.util.Base64;

;
// need to download following two
/*import com.sun.mail.util.BASE64DecoderStream;
 import com.sun.mail.util.BASE64EncoderStream;*/

public class StringEncrytDecryManager {

	private String string;
	Cipher ecipher;
	Cipher dcipher;

	SecretKey key;

	public StringEncrytDecryManager() {

		try {
			key = KeyGenerator.getInstance("DES").generateKey();
			ecipher = Cipher.getInstance("DES");
			dcipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getEncryptedString() {
		return encrypt(string);
	}

	public String getDecryptedString() {
		return decrypt(string);
	}

	private String encrypt(String str) {
		try {
			// encode the string into a sequence of bytes using the named
			// charset
			// storing the result into a new byte array.
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			// encode to base64
			enc = Base64.encode(enc, 0);

			return new String(enc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String decrypt(String str) {
		try {
			byte[] dec = Base64.decode(str.getBytes(), 0);
			byte[] utf8 = dcipher.doFinal(dec);
			return new String(utf8, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMD5(String input) {
		String key = "ADFAFD:KFJWEKWEWEF:FS";
		input = key + input;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(input.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
