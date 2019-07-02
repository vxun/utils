/**
 * Project Name:utils 
 * Class Name:Algorithm
 * Date:2017年5月7日下午12:08:36 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package fiftyone.utils.encrypt;

/**
 * @author zhangxw
 * TODO 一句话描述	
 * @Version 1.0
 * Date 2017-05-07 12:08:36 
 */
public enum Algorithm {
	AES("AES"), DES("DES"), SHA1("SHA-1");

	public final String val;

	Algorithm(String val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return this.val;
	}
}
