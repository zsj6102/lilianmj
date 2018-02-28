package org.soshow.beautyedu.utils;

import javax.crypto.Cipher;


import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.util.Log;

public class Encryption {
	// public static void main(String args[]) throws Exception {
	// System.out.println(encrypt());
	// System.out.println(desEncrypt());
	// }

	public static String encryptd(String key, String iv, String data) {
		try {
			return encrypt(key, iv, data);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static String desEncryptd(String key, String iv, String data) {
		try {
			return desEncrypt(key, iv, data);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static String encrypt(String key, String iv, String data) throws Exception {
		try {

			// String iv = "753951QKZP123456";

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes("ISO8859_1");
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			//Log.d("234abc", "encrypted=" + encrypted);
			return StringUtil.str2HexStr(new BASE64Encoder().encode(encrypted));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String desEncrypt(String key, String iv, String data) throws Exception {
		try {
			// String iv = "753951QKZP123456";
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(StringUtil.hexStr2Str(data));
			// byte[] encrypted1 = data.getBytes();
			// return new String(encrypted1);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString.trim();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}