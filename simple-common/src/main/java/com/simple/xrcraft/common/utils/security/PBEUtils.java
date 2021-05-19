package com.simple.xrcraft.common.utils.security;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/19 17:30
 */
public class PBEUtils extends SecurityUtil {

	private static final int count = 40;

	public static byte[] encrypt(byte[] content, String pwd, byte[] salt, String algo)
			throws Exception {
		return process(content, pwd, salt, algo, Cipher.ENCRYPT_MODE);
	}

	public static byte[] decrypt(byte[] content, String pwd, byte[] salt, String algo)
			throws Exception {
		return process(content, pwd, salt, algo, Cipher.DECRYPT_MODE);
	}

	public static byte[] process(byte[] content, String pwd, byte[] salt, String algo, int opMode)
			throws Exception {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inStream = new ByteArrayInputStream(content);
			outStream = new ByteArrayOutputStream();

			process(inStream, outStream, pwd, salt, algo, opMode);

			inStream.close();
			outStream.close();

			return outStream.toByteArray();
		} finally {
			IOUtils.closeQuietly(inStream, outStream);
		}
	}

	public static void encrypt(InputStream inStream, OutputStream outStream, String pwd, byte[] salt, String algo)
			throws Exception {
		process(inStream, outStream, pwd, salt, algo, Cipher.ENCRYPT_MODE);
	}

	public static void decrypt(InputStream inStream, OutputStream outStream, String pwd, byte[] salt, String algo)
			throws Exception {
		process(inStream, outStream, pwd, salt, algo, Cipher.DECRYPT_MODE);
	}

	public static void process(InputStream inStream, OutputStream outStream, String pwd, byte[] salt, String algo, int opMode)
			throws Exception {

		PBEParameterSpec spec = new PBEParameterSpec(salt, count);

		PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
		SecretKeyFactory factory = SecretKeyFactory.getInstance(algo);
		SecretKey key = factory.generateSecret(keySpec);

		universalProcess(inStream, outStream, algo, opMode, key, spec);
	}

}
