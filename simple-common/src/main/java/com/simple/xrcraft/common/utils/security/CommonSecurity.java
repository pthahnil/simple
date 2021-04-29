package com.simple.xrcraft.common.utils.security;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

/**
 * @description:
 * @author pthahnil
 * @date 2021/4/28 17:42
 */
public class CommonSecurity {

	/**
	 * 加密
	 * @param info
	 * @param paddingMode
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] info, byte[] key, String paddingMode) throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(info);
			outStream = new ByteArrayOutputStream();

			encrypt(inStream, outStream, paddingMode, key);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 解密
	 * @param info
	 * @param paddingMode
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] info, byte[] key, String paddingMode) throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(info);
			outStream = new ByteArrayOutputStream();

			decrypt(inStream, outStream, paddingMode, key);


			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 加密
	 * @param info
	 * @param paddingMode
	 * @param seed
	 * @param keyLength
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedEncrypt(byte[] info, byte[] seed, String paddingMode, Integer keyLength) throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(info);
			outStream = new ByteArrayOutputStream();

			seedEncrypt(inStream, outStream, paddingMode, seed, keyLength);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 解密
	 * @param info
	 * @param paddingMode
	 * @param seed
	 * @param keyLength
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedDecrypt(byte[] info, byte[] seed, String paddingMode, Integer keyLength) throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(info);
			outStream = new ByteArrayOutputStream();

			seedDecrypt(inStream, outStream, paddingMode, seed, keyLength);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	/**
	 * 加密流
	 * @param inStream
	 * @param outStream
	 * @param paddingMode
	 * @param key
	 * @throws Exception
	 */
	public static void encrypt(InputStream inStream, OutputStream outStream, String paddingMode,
			byte[] key) throws Exception {
		process(inStream, outStream, paddingMode, Cipher.ENCRYPT_MODE, key);
	}

	/**
	 * 解密流
	 * @param inStream
	 * @param outStream
	 * @param paddingMode
	 * @param key
	 * @throws Exception
	 */
	public static void decrypt(InputStream inStream, OutputStream outStream, String paddingMode,
			byte[] key) throws Exception {
		process(inStream, outStream, paddingMode, Cipher.DECRYPT_MODE, key);
	}

	/**
	 * 加密流
	 * @param inStream
	 * @param outStream
	 * @param paddingMode
	 * @param seed
	 * @param keyLength
	 * @throws Exception
	 */
	public static void seedEncrypt(InputStream inStream, OutputStream outStream, String paddingMode,
			byte[] seed, Integer keyLength) throws Exception {
		seedProcess(inStream, outStream, paddingMode, Cipher.ENCRYPT_MODE, seed, keyLength);
	}

	/**
	 * 解密流
	 * @param inStream
	 * @param outStream
	 * @param paddingMode
	 * @param seed
	 * @param keyLength
	 * @throws Exception
	 */
	public static void seedDecrypt(InputStream inStream, OutputStream outStream, String paddingMode,
			byte[] seed, Integer keyLength) throws Exception {
		seedProcess(inStream, outStream, paddingMode, Cipher.ENCRYPT_MODE, seed, keyLength);
	}

	/**
	 * 加/解密
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param key
	 * @throws Exception
	 */
	private static void process(InputStream inStream, OutputStream outStream, String paddingAlgorithm
			, int opMode, byte[] key) throws Exception {

		String keyAlgorithm = paddingAlgorithm.contains("/") ? paddingAlgorithm.substring(0, paddingAlgorithm.indexOf("/")) : paddingAlgorithm;

		SecretKey secretKey = new SecretKeySpec(key, keyAlgorithm);
		universalProcess(inStream, outStream, paddingAlgorithm, opMode, secretKey);
	}

	/**
	 * 加/解密
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param opMode
	 * @param seed
	 * @param keyLength
	 * @throws Exception
	 */
	private static void seedProcess(InputStream inStream, OutputStream outStream, String paddingAlgorithm
			, int opMode, byte[] seed, Integer keyLength) throws Exception {

		String keyAlgorithm = paddingAlgorithm.contains("/") ? paddingAlgorithm.substring(0, paddingAlgorithm.indexOf("/")) : paddingAlgorithm;

		SecretKey secretKey = genKeyWithSeed(seed, keyLength, keyAlgorithm);
		universalProcess(inStream, outStream, paddingAlgorithm, opMode, secretKey);
	}

	/**
	 * main process
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param opMode
	 * @param secretKey
	 * @throws Exception
	 */
	private static void universalProcess(InputStream inStream, OutputStream outStream, String paddingAlgorithm
			, int opMode, SecretKey secretKey) throws Exception {

		Cipher cipher = Cipher.getInstance(paddingAlgorithm);
		cipher.init(opMode, secretKey);

		CipherOutputStream cipherOutputStream = new CipherOutputStream(outStream, cipher);

		IOUtils.copy(inStream, cipherOutputStream);

		cipherOutputStream.close();
	}

	/**
	 * generate key with seed
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	private static SecretKey genKeyWithSeed(byte[] seed, Integer keyLength, String keyAlgorithm) throws Exception {
		keyLength = null != keyLength ? keyLength : 256;

		KeyGenerator kg = KeyGenerator.getInstance(keyAlgorithm);
		kg.init(keyLength, new SecureRandom(seed));

		return kg.generateKey();
	}


}
