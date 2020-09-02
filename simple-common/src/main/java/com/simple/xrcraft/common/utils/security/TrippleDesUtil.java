package com.simple.xrcraft.common.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by pthahnil on 2019/5/8.
 */
public class TrippleDesUtil {

	private static final String CHARSET = "utf-8";

	private static final String ALGORITHM = "DESede";

	/**
	 * cbc padding encrypt
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] cbcEncryt(byte[] content, byte[] key) throws Exception {
		return cbcEncryt(content, key, key);
	}

	public static byte[] cbcEncryt(byte[] content, byte[] key, byte[] icv) throws Exception {
		return process(content, key, icv, PaddingMode.DES_CBC.getType(), Cipher.ENCRYPT_MODE);
	}

	/**
	 * ecb padding encrypt
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] ecbEncryt(byte[] content, byte[] key) throws Exception {
		return process(content, key, null, PaddingMode.DES_ECB.getType(), Cipher.ENCRYPT_MODE);
	}

	public static byte[] seedEncryt(byte[] content, byte[] seed) throws Exception {
		byte[] key = getKey(seed);
		return ecbEncryt(content, key);
	}

	/**
	 * cbc padding decrypt
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] cbcDecrypt(byte[] content, byte[] key) throws Exception {
		return cbcDecrypt(content, key, key);
	}

	public static byte[] cbcDecrypt(byte[] content, byte[] key, byte[] icv) throws Exception {
		return process(content, key, icv, PaddingMode.DES_CBC.getType(), Cipher.DECRYPT_MODE);
	}

	/**
	 * ecb padding decrypt
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] ecbDecrypt(byte[] content, byte[] key) throws Exception {
		return process(content, key, null, PaddingMode.DES_ECB.getType(), Cipher.DECRYPT_MODE);
	}

	public static byte[] seedDecrypt(byte[] content, byte[] seed) throws Exception {
		byte[] key = getKey(seed);
		return ecbDecrypt(content, key);
	}

	/**
	 * 加/解密
	 * @param content
	 * @param key
	 * @param padding
	 * @param processMode
	 * @return
	 * @throws Exception
	 */
	protected static byte[] process(byte[] content, byte[] key, byte[] vector, String padding, int processMode)
			throws Exception {
		final SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
		final Cipher cipher = Cipher.getInstance(padding);
		IvParameterSpec iv = null;
		if(padding.contains("/CBC/")) {
			if(null == vector) {
				throw new Exception("cbc padding must have a vector");
			}
			byte[] icv = new byte[8];
			System.arraycopy(vector, 0, icv, 0, 8);
			iv = new IvParameterSpec(icv);
		}
		cipher.init(processMode, secretKey, iv);
		return cipher.doFinal(content);
	}

	/**
	 * seed获取key
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	private static byte[] getKey(final byte[] seed) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);

		/*SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(seed.getBytes());
		kg.init(168, secureRandom);*/

		kg.init(new SecureRandom(seed));

		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

	public static void main(String[] args) throws Exception {
		String key = "86KPi4OEI64E7rrr57o07wUr";

		String infoToEncrypt = "{\"dealer_id\":\"20479991\"}";

		/*String cbcEncrypted = cbcEncryt(infoToEncrypt, key);
		System.out.println("cbcEncryt: " + cbcEncrypted);

		String cbcDecrypted = cbcDecrypt(cbcEncrypted, key);
		System.out.println("cbcDecrypted: " + cbcDecrypted);

		String ecbEncrypted = ecbEncryt(infoToEncrypt, key);
		System.out.println("ecbEncryt: " + ecbEncrypted);

		String ecbDecrypted = ecbDecrypt(ecbEncrypted, key);
		System.out.println("ecbDecrypted: " + ecbDecrypted);*/

		/*String info = "0xfLqVX3/QLqp0HYwbkhzS6BV1gGkteDG+pM0vmP/K31fkQSAx/oWFBv+l1MNQm8Cl6u1HiV0QI57XAe5IFfMDq430myV4k9TI/Vc8agq8Xsixpi+LqgV9ptGGJBhmRrvCciVGc7ZhiDdcwXAbV/DVCM6zey711AUDeI0Kcez+rWf1E5mZ8MmMAvozpMu2EOWz5PY5hMl3NQlNxaKxx2dPQARkop4VZxQaskeREi6luC1hbxfccHuAJ5Vw9JrL1KP1I8omY0NXUXtZZ9euO5oZwi6CNwReU6f16+hwGL4MIsXY9AzxXtDGeg//wKV6w3fxryhs6CHf/4DAHIwxvac9mH7ZOyZyMJF2ouUuXplia7x+DWy3RsQwKTUdVeKdWOL6TUKKfcSsHiMKzAnft6C6NUYTstinIQO8O8FmTQPjRYI4BYiBMqYRDjQCK2NEVv8ByTQbOyb3NJByexu2e8EeTtVESy1DNGJXdsxbDeeyGB5LVfJ5c9tP0OX7Td1O1QjWz8DFQctlDyD4mpWZxUg9aWAa9v7ulaxg/2Yu31Cwq+JWmxcGhlP/19zAN0s0XlnJj0XBvcpRfQ0X4fE4WxuRV8hQQXYo9PLilzxvZ072GwkmPXrFnGrO0VHnCuZ9/Aw8yDGOADgxCm6JZqKgBHw/sN7ppR219sM3UTxQgEbHyhtcRQrSUxBiihNM/F4CWpTmTnLjFAxU2BWkHekE98FA==";
		String decrypted = cbcDecrypt(info, key);
		System.out.println(decrypted);*/
	}

}
