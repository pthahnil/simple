package com.simple.xrcraft.common.utils.security;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 * Created by pthahnil on 2019/5/8.
 */
public class MD5Utils extends DigestUtil {

	public static String md5(String info) throws Exception {
		byte[] digestedBytes = digest(info.getBytes(), "MD5");
		return new String(Hex.encode(digestedBytes));
	}

}
