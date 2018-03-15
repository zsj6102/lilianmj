package org.soshow.beautyedu.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class RSAEncrypt {
	private static final Provider RsaProvider  = new org.bouncycastle.jce.provider.BouncyCastleProvider();//TODO + bcprov-jdk .jar

//	static String RES_INST = "RSA"; // == "RSA/None/NoPadding"; //固定
	static String RES_INST = "RSA/None/PKCS1Padding";//加随机  <==> jsencrypt.min.js
	
	public static class Gen {
		public static Map<String, Object> initKey() throws Exception {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			Map<String, Object> keyMap = new HashMap<String, Object>(2);
			keyMap.put("publicKey", publicKey);
//			System.out.println(Base64.encodeBase64String(publicKey.getEncoded()));
			keyMap.put("privateKey", privateKey);
//			System.out.println(Base64.encodeBase64String(privateKey.getEncoded()));
			return keyMap;
		}

		public static void main(String[] args) throws Exception {
			initKey();
		}
	}
	
	/**180211+*/
	private static final String DEFAULT_PUBLIC_KEY =  "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbLZfOpyPptEiTiBFqhHaCUq73DGZR1KDTVfBKOwyyhlrppdWfyknhW44Bl63K3WG4G9pnAFj9djtDbAuUtJMbF0sfiP0TyAM6mW+1Px6i2gQcuH+ItBRGJ9asiZ+cdtuix0Ulr7NXy4fAbSSno0gKaO29EmWMScYDTg16o+cYfwIDAQAB";
	
	public static RSAEncrypt instance = new RSAEncrypt();
	public static RSAEncrypt get(){
		return instance;
	}
	static{
		try {
//			instance.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;
	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;
	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 获取私钥
	 * 
	 * @return 当前的私钥对象
	 */
	 RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * 获取公钥
	 * 
	 * @return 当前的公钥对象
	 */
	 RSAPublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * 随机生成密钥对
	 */
	 void genKeyPair() {
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
		this.publicKey = (RSAPublicKey) keyPair.getPublic();
	}

	/**
	 * 从文件中输入流中加载公钥
	 * 
	 * @param in
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public void loadPublicKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
//			loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
//	public void loadPublicKey(String publicKeyStr) throws Exception {
//		try {
////			Base64 base64Decoder = new Base64();
////			byte[] buffer = base64Decoder.decode(publicKeyStr);
//			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
////			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
////			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
//		} catch (NoSuchAlgorithmException e) {
//			throw new Exception("无此算法");
//		} catch (InvalidKeySpecException e) {
//			throw new Exception("公钥非法");
//		} catch (NullPointerException e) {
//			throw new Exception("公钥数据为空");
//		}
//	}

	/**
	 * 从文件中加载私钥
	 * 
	 * @param
	 *
	 * @return 是否成功
	 * @throws Exception
	 */
//	public void loadPrivateKey(InputStream in) throws Exception {
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			String readLine = null;
//			StringBuilder sb = new StringBuilder();
//			while ((readLine = br.readLine()) != null) {
//				if (readLine.charAt(0) == '-') {
//					continue;
//				} else {
//					sb.append(readLine);
//					sb.append('\r');
//				}
//			}
//			loadPrivateKey(sb.toString());
//		} catch (IOException e) {
//			throw new Exception("私钥数据读取错误");
//		} catch (NullPointerException e) {
//			throw new Exception("私钥输入流为空");
//		}
//	}

//	public void loadPrivateKey(String privateKeyStr) throws Exception {
//		try {
//			Base64 base64Decoder = new Base64();
//			byte[] buffer = base64Decoder.decode(privateKeyStr);
//			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
//			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//			this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
//		} catch (NoSuchAlgorithmException e) {
//			throw new Exception("无此算法");
//		} catch (InvalidKeySpecException e) {
//			throw new Exception("私钥非法");
//		} catch (NullPointerException e) {
//			throw new Exception("私钥数据为空");
//		}
//	}

	public byte[] encrypt(byte[] plainTextData) throws Exception {
		return encrypt(getPublicKey(), plainTextData);
	}
	public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(RES_INST, RsaProvider);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}




	/**
	 * 字节数据转十六进制字符串
	 * 
	 * @param data
	 *            输入数据
	 * @return 十六进制内容
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			// 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			// 取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}
	/*
	 * js
	 * var encrypt = new JSEncrypt();
			//TODO conf
            encrypt.setPublicKey(  )
            var pswstr = encodeURI( encrypt.encrypt(psw+"_"+ts) ).replace(/\+/g, '%2B');
            
            
       java
		password_enc = password_enc.replaceAll("%2B", "+");
		String psw = new String(RSAEncrypt.get().decrypt(Base64.decodeBase64(password_enc)));
		
		String ts = request.getParameter("ts");
		psw = psw.substring(0, psw.length() - ("_" + ts).length());
	 * 
	 * 
	 */

	public static void main(String[] args) {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		// rsaEncrypt.genKeyPair();
		// 加载公钥
		try {
//			rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);
			System.out.println("加载公钥成功");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载公钥失败");
		}
		// 加载私钥
		try {

			System.out.println("加载私钥成功");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载私钥失败");
		}
		// 测试字符串
		String encryptStr = "123456";
		try {
			// 加密
			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());
			String a = Base58.encode(cipher);
			System.out.println(a);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}