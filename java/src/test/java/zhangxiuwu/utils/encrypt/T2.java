/**
 * Project Name:utils 
 * Class Name:zhangxiuwu.utils.encrypt.T2  
 * Date:2017年5月7日上午8:02:59 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxiuwu.utils.encrypt;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhangxw
 * TODO 一句话描述	
 * @Version 1.0
 * Date 2017-05-07 08:02:59 
 */
public class T2 {
	private final static String DES = "AES";

	public static void main(String[] args) throws Exception {
		String key = "abcdefghabcdefgh";
//		1A117E0044840092303A626867F0636F
		System.out.println(encryptTest("测试加密", key));

	}

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encryptTest(String encryptString, String encryptKey) throws Exception {

		String iv = "1234567890123456"; // 初始化向量参数，AES 为16bytes. DES 为8bytes.

		SecretKeySpec keySpec = new SecretKeySpec(encryptKey.getBytes(), "AES");

		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // 实例化加密类，参数为加密方式，要写全

		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		// 初始化，此方法可以采用三种方式，按服务器要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom
		// random = new
		// SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec

		// cipher.init(Cipher.ENCRYPT_MODE, keySpec);

		// SecureRandom random = new SecureRandom();

		// cipher.init(Cipher.ENCRYPT_MODE, keySpec, random);

		byte[] orgBytes = encryptString.getBytes(Charset.forName("utf-8"));

		byte[] b = cipher.doFinal(orgBytes);
		// 加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64,
		// HEX, UUE,
		// 7bit等等。此处看服务器需要什么编码方式

		// String ret = new BASE64Encoder().encode(b); // Base64、HEX等编解码

		String ret = bytesToHexString(b).toUpperCase();

		return ret;
	}

	/**
	 * 
	 * @Title: bytesToHexString @Description: byte[]转十六进制字符串 @param @param
	 * src @return String 返回类型 @throws
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}
}
