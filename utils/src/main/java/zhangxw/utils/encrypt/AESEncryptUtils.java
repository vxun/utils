/**
 * Project Name:utils 
 * Class Name:zhangxiuwu.utils.encrypt.CommonEncryptUtils  
 * Date:2017年5月6日下午9:56:06 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxw.utils.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import zhangxw.utils.common.StringUtils;

/**
 * @author zhangxw TODO 加密解密工具类
 * @Version 1.0 Date 2017-05-06 21:56:06
 */
public class AESEncryptUtils {

	/**
	 * @Author zhangxw generatorKey: 不支持AES
	 * @param algorithm
	 *            算法名 AES Key generator for use with the AES algorithm. ARCFOUR
	 *            Key generator for use with the ARCFOUR (RC4) algorithm.
	 *            Blowfish Key generator for use with the Blowfish algorithm.
	 *            DES Key generator for use with the DES algorithm. DESede Key
	 *            generator for use with the DESede (triple-DES) algorithm.
	 *            HmacMD5 Key generator for use with the HmacMD5 algorithm.
	 *            HmacSHA1 HmacSHA224 HmacSHA256 HmacSHA384 HmacSHA512 Keys
	 *            generator for use with the various flavors of the HmacSHA
	 *            algorithms. RC2 Key generator for use with the RC2 algorithm.
	 * 
	 * @param key
	 * @return
	 */
	static SecretKeySpec generateKey(Algorithm algorithm, String passwd) {
		SecretKeySpec skspec = null;
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.val);
			// keyGenerator.init(128, new
			// SecureRandom(passwd.getBytes("UTF-8"))); AES不支持这种方式
			keyGenerator.init(128);
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] encodedKey = secretKey.getEncoded();
			skspec = new SecretKeySpec(encodedKey, algorithm.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return skspec;
	}

	/**
	 * @Author zhangxw generateKey4AES: 生成秘钥，AES的passwword必须是16bit
	 * @param algorithm
	 * @param passwd
	 * @return
	 */
	static SecretKeySpec generateKey4AES(Algorithm algorithm, String passwd) {
		byte[] data = null;
		if (passwd == null) {
			passwd = "";
		}
		StringBuffer sb = new StringBuffer(16);
		sb.append(passwd);
		while (sb.length() < 16) {
			sb.insert(0, "0");
		}
		if (sb.length() > 16) {
			sb.setLength(16);
		}

		try {
			data = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new SecretKeySpec(data, algorithm.val);
	}

	/**
	 * @Author zhangxw generateIv4AES: AES加密的偏移量，需要16位
	 * @param iv
	 * @return
	 */
	static IvParameterSpec generateIv4AES(String iv) {
		IvParameterSpec result = null;
		StringBuilder sb = new StringBuilder(iv);
		try {
			while (sb.length() < 16) {
				sb.insert(0, "0");
			}
			if (sb.length() > 16) {
				sb.setLength(16);
			}
			result = new IvParameterSpec(sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @Author zhangxw AES128Encrypt: 一句话描述
	 * @param content
	 * @param key
	 *            加密的Key，128bit
	 */
	public static byte[] AES128Encrypt(String content, String key, Transformation transformation, String iv) {
		byte[] result = null;
		try {
			SecretKeySpec skspec = generateKey4AES(Algorithm.AES, key);
			Cipher cipher = Cipher.getInstance(transformation.val);
			cipher.init(Cipher.ENCRYPT_MODE, skspec, generateIv4AES(iv));
			result = cipher.doFinal(content.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String AES128Encrypt2Base64(String content, String key, Transformation transformation, String iv) {
		byte[] bs = AES128Encrypt(content, key, transformation, iv);
		return Base64.getEncoder().encodeToString(bs);
	}

	public static String AES128Encrypt2Hex(String content, String key, Transformation transformation, String iv) {
		byte[] bs = AES128Encrypt(content, key, transformation, iv);
		return StringUtils.bytes2Hex(bs);
	}

	/**
	 * 
	 * @Author zhangxw AES128Decrypt: 一句话描述
	 * @param content
	 * @param key
	 *            解密key
	 * @return
	 */
	public static byte[] AES128Decrypt(byte[] content, String key, Transformation transformation, String iv) {
		byte[] result = null;
		try {
			SecretKeySpec skspec = generateKey4AES(Algorithm.AES, key);
			Cipher cipher = Cipher.getInstance(transformation.val);
			cipher.init(Cipher.DECRYPT_MODE, skspec, generateIv4AES(iv));
			result = cipher.doFinal(content);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @Author zhangxw base642AES128Decrypt2Base64: 解密经过base64编码后的AES加密密文。
	 * @param content
	 * @param key
	 * @param transformation
	 * @param iv
	 * @return
	 */
	public static String base642AES128Decrypt(String content, String key, Transformation transformation, String iv) {
		byte[] bytes = Base64.getDecoder().decode(content);
		byte[] bs = AES128Decrypt(bytes, key, transformation, iv);
		try {
			return new String(bs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Author zhangxw hexAES128Decrypt: 解密经过Hex编码的AES加密后的密文
	 * @param content
	 * @param key
	 * @param transformation
	 * @param iv
	 * @return
	 */
	public static String hex2AES128Decrypt(String content, String key, Transformation transformation, String iv) {
		byte[] btes = StringUtils.hex2Bytes(content);
		byte[] bs = AES128Decrypt(btes, key, transformation, iv);
		try {
			return new String(bs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) {
		String bs = AES128Encrypt2Base64("测试加密", "abcdefghabcdefgh", Transformation.AES_CBC_PKCS5PADDING,
				"1234567890123456");
		System.out.println(bs);
		String bs2 = AES128Encrypt2Hex("测试加密", "abcdefghabcdefgh", Transformation.AES_CBC_PKCS5PADDING,
				"1234567890123456");
		System.out.println(bs2);
		System.out.println("=====");

		String s = base642AES128Decrypt("GhF+AESEAJIwOmJoZ/Bjbw==", "abcdefghabcdefgh",
				Transformation.AES_CBC_PKCS5PADDING, "1234567890123456");
		System.out.println(s);
		String sh = hex2AES128Decrypt("1A117E0044840092303A626867F0636F", "abcdefghabcdefgh",
				Transformation.AES_CBC_PKCS5PADDING, "1234567890123456");
		System.out.println(sh);

//		byte[] bytes = { 1, 2, 3, 4, 5, 52, 56, 21, 34 };
//		System.out.println(bytes2Hex(bytes));
//		System.out.println("=================");
//		byte[] bytea = hex2Bytes("1A117E0044840092303A626867F0636F");
//		for (byte b : bytea) {
//			System.out.println(b);
//		}
//		System.out.println("=================");
//		byte[] bytea2 = hex2Bytes2("1A117E0044840092303A626867F0636F");
//		for (byte b : bytea2) {
//			System.out.println(b);
//		}
		// byte[] a = hex2Bytes("10");
		// for (byte b : a) {
		// System.out.println(b);
		// }
		// System.out.println("=====");
		// System.out.println(hexChar2byte('A'));
		// System.out.println("=====");
		// byte[] bytes = null;
		// try {
		// bytes = "我曹".getBytes("UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// for (byte b : bytes) {
		// System.out.println(b);
		// }
	}

}
