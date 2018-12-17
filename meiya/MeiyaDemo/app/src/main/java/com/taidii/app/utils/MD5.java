package com.taidii.app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MD5 {

	private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * 得到参数加密后的MD5值
	 *
	 * @param inStr
	 * @return 32byte MD5 Value
	 */
	public static String getMD5(String inStr) {
		byte[] inStrBytes = inStr.getBytes();
		try {
			MessageDigest MD = MessageDigest.getInstance("MD5");
			MD.update(inStrBytes);
			byte[] mdByte = MD.digest();
			char[] str = new char[mdByte.length * 2];
			int k = 0;
			for (int i = 0; i < mdByte.length; i++) {
				byte temp = mdByte[i];
				str[k++] = hexDigits[temp >>> 4 & 0xf];
				str[k++] = hexDigits[temp & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 字符串转MD5
	 * 与上面的方法实现方式有所不同（采用StringBuffer，对传过来的str直接修改，易于数据的加密，防止监听内存）
	 *
	 * @param str
	 * @return str
	 */
	public static String Md5(String str) {
		if (str != null && !str.equals("")) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Byte.length; i++) {
					sb.append(hexDigits[(md5Byte[i] & 0xff) / 16]);
					sb.append(hexDigits[(md5Byte[i] & 0xff) % 16]);
				}
				str = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 获取随机字符串
	 *
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { //length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

}

