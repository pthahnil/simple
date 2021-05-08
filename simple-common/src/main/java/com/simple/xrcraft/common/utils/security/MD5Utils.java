package com.simple.xrcraft.common.utils.security;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 * Created by pthahnil on 2019/5/8.
 */
public class MD5Utils {

	public static String md5(String info) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(info.getBytes());

		byte[] digestedBytes = digest.digest();
		return new String(Hex.encode(digestedBytes));
	}

}
