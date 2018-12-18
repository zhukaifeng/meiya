package com.taidii.app.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Base64;

/**
 * Created by zhukaifeng on 2018/12/18.
 */

public class Base64Util {

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String encryptBASE64(byte[] data) {
		// BASE64Encoder encoder = new BASE64Encoder();
		// String encode = encoder.encode(data);
		// 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
		Base64.Encoder encoder = Base64.getEncoder();
		String encode = encoder.encodeToString(data);
		return encode;
	}
	/**
	 * BASE64Decoder 解密
	 *
	 * @param data
	 *            要解密的字符串
	 * @return 解密后的byte[]
	 * @throws Exception
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static byte[] decryptBASE64(String data) throws Exception {
		// BASE64Decoder decoder = new BASE64Decoder();
		// byte[] buffer = decoder.decodeBuffer(data);
		// 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] buffer = decoder.decode(data);
		return buffer;
	}
}