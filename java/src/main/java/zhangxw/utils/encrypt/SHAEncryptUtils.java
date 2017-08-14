/**
 * Project Name:utils 
 * Class Name:zhangxw.utils.encrypt.SHAEncryptUtils  
 * Date:2017年5月7日下午12:09:48 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxw.utils.encrypt;

import java.security.MessageDigest;

import zhangxw.utils.common.StringUtils;

/**
 * @author zhangxw
 * TODO 一句话描述	
 * @Version 1.0
 * Date 2017-05-07 12:09:48 
 */
public class SHAEncryptUtils {

	public static byte[] sha1(String message) {
		byte[] result = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(Algorithm.SHA1.val);
//			digest.update(message.getBytes("UTF-8"));
			System.out.println(message);
			result = digest.digest(message.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * @Author zhangxw
	 * sha12Hex: sha1加密后，转为16进制字符串
	 * @param message
	 * @return
	 */
	public static String sha12Hex(String message) {
		byte[] bytes = sha1(message);
//		String result = hex(bytes);
		String result = StringUtils.bytes2Hex(bytes);
		System.out.println(result);
		return result;
	}
	
}
