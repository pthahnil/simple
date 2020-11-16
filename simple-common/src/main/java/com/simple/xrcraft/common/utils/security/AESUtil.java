package com.simple.xrcraft.common.utils.security;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * aes util
 * Created by pthahnil on 2019/5/8.
 */
public class AESUtil {

	/**
	 * 加密算法
	 */
	private static final String ALGORITHM = "AES";

	private static final String DEFAULT_PADDING = PaddingMode.AES_ECB.getType();

	/**
	 * encrypt with random seed, no padding
	 * @param data
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedEncrypt(byte[] data, byte[] seed)
			throws Exception {
		return seedEncrypt(data, seed, DEFAULT_PADDING);
	}

	/**
	 * encrypt with random seed, use padding
	 * @param data
	 * @param seed
	 * @param paddingMode
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedEncrypt(byte[] data, byte[] seed, String paddingMode)
			throws Exception {
		byte[] key = genKeyWithSeed(seed);
		byte[] ivKey = getIvKey(key, paddingMode);
		return process(data, key, ivKey, Cipher.ENCRYPT_MODE, paddingMode);
	}

	/**
	 * decrypt with random seed, no padding
	 * @param data
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedDecrypt(byte[] data, byte[] seed)
			throws Exception {
		return seedDecrypt(data, seed, DEFAULT_PADDING);
	}

	/**
	 * decrypt with random seed, use padding
	 * @param data
	 * @param seed
	 * @param paddingMode
	 * @return
	 * @throws Exception
	 */
	public static byte[] seedDecrypt(byte[] data, byte[] seed, String paddingMode)
			throws Exception {
		byte[] key = genKeyWithSeed(seed);
		byte[] ivKey = getIvKey(key, paddingMode);
		return process(data, key, ivKey, Cipher.DECRYPT_MODE, paddingMode);
	}

	/**
	 * encrypt with key, no padding
	 * @param data
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key, String iv)
			throws Exception {
		return encrypt(data, key, iv, DEFAULT_PADDING);
	}

	/**
	 * encrypt with key, use padding
	 * @param data
	 * @param key
	 * @param iv
	 * @param paddingMode
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key, String iv, String paddingMode)
			throws Exception {
		byte[] ivKey = StringUtils.isNotBlank(iv) ? getIvKey(iv.getBytes(), paddingMode) : null;

		return process(data, key, ivKey, Cipher.ENCRYPT_MODE, paddingMode);
	}

	/**
	 * decrypt with key, no padding
	 * @param content
	 * @param encKey
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] encKey, String iv)
			throws Exception {
		return decrypt(content, encKey, iv, DEFAULT_PADDING);
	}

	/**
	 * decrypt with key, use padding
	 * @param content
	 * @param encKey
	 * @param iv
	 * @param paddingMode
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] encKey, String iv, String paddingMode)
			throws Exception {
		byte[] ivKey = StringUtils.isNotBlank(iv) ? getIvKey(iv.getBytes(), paddingMode) : null;
		return process(content, encKey, ivKey, Cipher.DECRYPT_MODE, paddingMode);
	}

	/**
	 * main encrypt/decrypt process
	 * @param data
	 * @param key
	 * @param oprMode
	 * @param paddingMode
	 * @return
	 * @throws Exception
	 */
	private static byte[] process(byte[] data, byte[] key, byte[] spec, int oprMode, String paddingMode) throws Exception {
		if(null == key || (key.length != 16 && key.length != 24 && key.length != 32)){
			throw new Exception("key长度必须是16或者24或者32");
		}

		SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

		IvParameterSpec iv = null;
		if(null != spec && spec.length > 0){
			iv = new IvParameterSpec(spec);
		}

		Cipher cipher = Cipher.getInstance(paddingMode);
		int blockSize = cipher.getBlockSize();
		int plaintextLength = data.length;
		if (plaintextLength % blockSize != 0) {
			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
		}
		byte[] plaintext = new byte[plaintextLength];
		System.arraycopy(data, 0, plaintext, 0, data.length);

		cipher.init(oprMode, keySpec, iv);
		return cipher.doFinal(plaintext);
	}

	private static byte[] getIvKey(byte[] iv, String paddingMode) throws Exception {
		byte[] ivKey = null;

		if(paddingMode.contains("/CBC/")) {
			if(null == iv || iv.length < 16) {
				throw new Exception("CBC 必须包含16位的向量");
			} else {
				ivKey = new byte[16];
				System.arraycopy(iv, 0, ivKey, 0, 16);
			}
		}

		return ivKey;
	}

	/**
	 * generate key with seed
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	private static byte[] genKeyWithSeed(byte[] seed) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(256, new SecureRandom(seed));

		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}
}
