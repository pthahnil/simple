package com.simple.xrcraft.common.utils.security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Created by pthahnil on 2019/6/26.
 */
public class SignUtils {

	/**
	 *  签名
	 * @param privateKey
	 * @param data
	 * @param algorithm
	 * @return
	 */
	public static byte[] sign(PrivateKey privateKey, byte[] data, String algorithm) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	/**
	 * 验签
	 * @param publicKey
	 * @param data
	 * @param sign
	 * @param algorithm
	 * @return
	 */
	public static boolean valifySign(PublicKey publicKey, byte[] data, byte[] sign, String algorithm) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(sign);
	}

}
