package aeac.sys.aeac_basetemple.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;


import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RsaAndAesUtils {

	public static final String KEY_ALGORITHM = "RSA";
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

	public static Map<String, Object> initKey() throws Exception {
		// 获取算法RSA
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	// 获得公钥字符串
	public static String getPublicKeyStr(Map<String, Object> keyMap) throws Exception {
		// 获得map中的公钥对象 转为key对象
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		// 编码返回字符串
		return encryptBASE64(key.getEncoded());
	}

	// 获得私钥字符串
	public static String getPrivateKeyStr(Map<String, Object> keyMap) throws Exception {
		// 获得map中的私钥对象 转为key对象
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		// 编码返回字符串
		return encryptBASE64(key.getEncoded());
	}

	// 获取公钥
	private static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decodeBase64(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	// 获取私钥
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decodeBase64(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	// 编码返回字符串
	public static String encryptBASE64(byte[] key) {
		return Base64.encodeBase64String(key);
	}

	// ************************加密文件**************************
	public static void encryptFile(InputStream inputStream, String outputPath, String publicKeyStr) throws Exception {
		DataOutputStream dataOut = null;
		CipherInputStream cipherInputStream = null;
		try {
			// 生成 AES 密钥 key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom random = new SecureRandom();
			keygen.init(random);
			SecretKey key = keygen.generateKey();

			PublicKey publicKey = getPublicKey(publicKeyStr);
			// RSA
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.WRAP_MODE, publicKey);

			// RSA 加密 key
			byte[] wrappedKey = rsa.wrap(key);
			dataOut = new DataOutputStream(new FileOutputStream(outputPath));
			dataOut.writeInt(wrappedKey.length); // 将加密后的密钥写入到输出流 dataOut
			dataOut.write(wrappedKey);
			Cipher aes = Cipher.getInstance("AES");
			aes.init(Cipher.ENCRYPT_MODE, key);

			cipherInputStream = new CipherInputStream(inputStream, aes);
			IOUtils.copyLarge(cipherInputStream, dataOut);
		} finally {
			if (dataOut != null) {
				dataOut.close();
			}
			if (cipherInputStream != null) {
				cipherInputStream.close();
			}
		}
	}

	// ************************解密文件**************************
	public static void decryptFile(InputStream inputStream, String outputPath, String privateKeyStr) throws Exception {
		DataInputStream dataIn = null;
		CipherOutputStream cipherOutputStream = null;
		try {
			dataIn = new DataInputStream(inputStream);
			int length = dataIn.readInt(); // 读取 key 长度
			byte[] wrappedKey = new byte[length];
			dataIn.read(wrappedKey, 0, length);
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.UNWRAP_MODE, getPrivateKey(privateKeyStr));
			// 获取 AES 密钥 key
			Key key = rsa.unwrap(checkByteLength(wrappedKey), "AES", Cipher.SECRET_KEY);
			Cipher aes = Cipher.getInstance(ALGORITHMSTR);
			aes.init(Cipher.DECRYPT_MODE, key);
			FileOutputStream outputStream=new FileOutputStream(outputPath);
			cipherOutputStream = new CipherOutputStream(outputStream, aes);
			IOUtils.copyLarge(dataIn, cipherOutputStream);
			cipherOutputStream.flush();
			outputStream.close();
		} finally {
			if (dataIn != null) {
				dataIn.close();
			}
			if (cipherOutputStream != null) {
				cipherOutputStream.close();
			}
		}
	}

	private static byte[] checkByteLength(byte[] byteContent) {
		int length = byteContent.length;
		int remainder = length % 16;
		if (remainder == 0) {
			return byteContent;
		} else {
			return Arrays.copyOf(byteContent, length + (16 - remainder));
		}
	}

	public static void main(String[] args) throws Exception {
//		String orignalPath = "C:\\Users\\rd_y_hu\\Desktop\\金蝶云苍穹平台开发指南.docx";
		String orignalPath = "C:\\Users\\rd_y_hu\\Desktop\\111.pdf";
		String fileName = "111.pdf";
		String encryptFilePath = "C:\\Users\\rd_y_hu\\Desktop\\加密后文件\\" + fileName;
		String decryptFilePath = "C:\\Users\\rd_y_hu\\Desktop\\解密后文件\\" + fileName;
		File orignalFIle = new File(orignalPath);
		InputStream orignalIn = null;
		FileInputStream encryptFileIn = null;
		try {
			Map<String, Object> initKey = initKey();
			String publicKeyStr = getPublicKeyStr(initKey);
			System.out.println("公钥:" + publicKeyStr);
			String privateKeyStr = getPrivateKeyStr(initKey);
			System.out.println("私钥:" + privateKeyStr);

			orignalIn = new FileInputStream(orignalFIle); // 初始文件输入流
			// 加密
			long startTime = System.currentTimeMillis();
			File encryptFile = new File(encryptFilePath);
			encryptFile(orignalIn, encryptFilePath, publicKeyStr); // 输出到文件 outFilePath
			System.out.println("加密耗时 " + (System.currentTimeMillis() - startTime));

			// 解密
			startTime = System.currentTimeMillis();
			encryptFileIn = new FileInputStream(encryptFile); // 加密文件输入流
			decryptFile(encryptFileIn, decryptFilePath, privateKeyStr);
			System.out.println("解密耗时 " + (System.currentTimeMillis() - startTime));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (orignalIn != null) {
				orignalIn.close();
			}
			if (encryptFileIn != null) {
				encryptFileIn.close();
			}
		}
	}
}
