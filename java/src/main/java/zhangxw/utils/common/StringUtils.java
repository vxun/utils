/**
 * Project Name:utils 
 * Class Name:zhangxiuwu.utils.StringUtil  
 * Date:2017年5月7日上午9:57:38 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxw.utils.common;

/**
 * @author zhangxw
 * TODO 一句话描述	
 * @Version 1.0
 * Date 2017-05-07 09:57:38 
 */
public class StringUtils {
	
	/**
	 * @Author zhangxw
	 * bytes2Hex: byte转16进制字符串
	 * @param bytes
	 * @return
	 */
	public static String bytes2Hex(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	/**
	 * @Author zhangxw
	 * hex2Bytes2: 16进制转byte数组
	 * @param hex 
	 * @return
	 */
	public static byte[] hex2Bytes2(String hex) {
		String idx = "0123456789ABCDEF";
		if (hex == null || hex.trim().equals("")) {
			return null;
		}
		hex = hex.toUpperCase();
		int length = hex.length() / 2;
		char[] hexChars = hex.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (idx.indexOf(hexChars[pos]) << 4 | idx.indexOf(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * @Author zhangxw
	 * hex2Bytes: 16进制转byte数组
	 * @param hex
	 * @return
	 */
	public static byte[] hex2Bytes(String hex) {
		if (hex.length() < 1)
			return null;
//		hex = hex.toUpperCase();
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; i++) {
			int high = Integer.parseInt(hex.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hex.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
	
	/**
	 * @Author zhangxw
	 * isNotBlank: 判断是否为空白字符
	 * @return
	 */
	public static Boolean isNotBlank(String str) {
		return str != null && !str.trim().equals(""); 
	}
	
	/**
	 * @Author zhangxw
	 * isBlank: 判断是否为空白字符
	 * @param str
	 * @return
	 */
	public static Boolean isBlank(String str) {
		return str == null || str.trim().equals(""); 
	}
	
}
