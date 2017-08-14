/**
 * Project Name:utils 
 * Class Name:zhangxw.utils.encrypt.Transformation  
 * Date:2017年5月7日下午12:08:52 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxw.utils.encrypt;

/**
 * @author zhangxw
 * TODO 一句话描述	
 * @Version 1.0
 * Date 2017-05-07 12:08:52 
 */
public enum Transformation {
	AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding");
	public final String val;

	Transformation(String val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return this.val;
	}
}
