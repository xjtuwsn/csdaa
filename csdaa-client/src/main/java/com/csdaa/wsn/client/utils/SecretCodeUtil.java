package com.csdaa.wsn.client.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 
 * @author Zhou Jimmy
 * @email zhoujimmy@yeah.net
 * @version 2019年3月30日上午10:09:09
 * MD5加密工具类
 */
public class SecretCodeUtil {

	// 将字节流转换为BigInteger类型
	public static BigInteger md5Int(byte[] win, MessageDigest md5) {
		md5.update(win);
		byte[] result = md5.digest();
		return new BigInteger(result);
	}

	// 将字符串转换为16进制表示的字符串
	public static String md5(String string, MessageDigest md5) {
		md5.update(string.getBytes());
		byte[] result = md5.digest();
		return new BigInteger(result).toString(16).toString();
	}

	// 将字节流转换为16进制表示的字符串
	public static String md5(byte[] buf, MessageDigest md5) {
		md5.update(buf);
		byte[] result = md5.digest();
		return new BigInteger(result).toString(16).toString();
	}

}
