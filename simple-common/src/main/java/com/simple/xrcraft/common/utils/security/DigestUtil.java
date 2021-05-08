package com.simple.xrcraft.common.utils.security;

import java.security.MessageDigest;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/8 17:38
 */
public class DigestUtil {

	public static byte[] digest(byte[] toDigest, String algo) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(algo);
		digest.update(toDigest);

		return digest.digest();
	}

}
