package com.simple.xrcraft.common.utils.security;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @description:
 * @author pthahnil
 * @date 2021/4/28 17:42
 */
public class AsymmetricSecurity extends SecurityUtil {

	/**
	 * 非对称加密
	 * @param content
	 * @param paddingAlgorithm
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] content, String paddingAlgorithm,
			PublicKey key) throws Exception {

		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(content);
			outStream = new ByteArrayOutputStream();

			encrypt(inStream, outStream, paddingAlgorithm, key);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 非对称解密
	 * @param content
	 * @param paddingAlgorithm
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, String paddingAlgorithm,
			PrivateKey key) throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(content);
			outStream = new ByteArrayOutputStream();

			decrypt(inStream, outStream, paddingAlgorithm, key);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 非对称加密流
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param key
	 * @throws Exception
	 */
	public static void encrypt(InputStream inStream, OutputStream outStream, String paddingAlgorithm,
			PublicKey key) throws Exception {
		process(inStream, outStream, paddingAlgorithm, Cipher.ENCRYPT_MODE, key);
	}

	/**
	 * 非对称解密流
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param key
	 * @throws Exception
	 */
	public static void decrypt(InputStream inStream, OutputStream outStream, String paddingAlgorithm,
			PrivateKey key) throws Exception {
		process(inStream, outStream, paddingAlgorithm, Cipher.DECRYPT_MODE, key);
	}

	private static void process(InputStream inStream, OutputStream outStream, String paddingAlgorithm,
			int opmode, Key key) throws Exception {
		universalProcess(inStream, outStream, paddingAlgorithm, opmode, key, null);
	}


}
