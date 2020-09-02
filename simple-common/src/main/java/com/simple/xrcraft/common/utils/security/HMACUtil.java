package com.simple.xrcraft.common.utils.security;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pthahnil on 2019/5/8.
 */
public class HMACUtil {

	/**
	 * 构建HmacMD5密钥
	 *
	 * @return
	 * @throws Exception
	 */
	public static byte[] initHmacMD5Key() throws Exception {
		return genKey(MacAlgorithm.MD5.getAlgorithm());
	}


	/**
	 * 生成32位HmacMD5加密数据
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeHmacMD5(byte[] data, byte[] key)
			throws Exception {
		return encode(data, key, MacAlgorithm.MD5.getAlgorithm());
	}

	/**
	 * 构建HmacSHA1密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initHmacSHAKey() throws NoSuchAlgorithmException {
		return genKey(MacAlgorithm.SHA1.getAlgorithm());
	}

	/**
	 * 生成40位HmacSHA1加密数据
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeHmacSHA(byte[] data, byte[] key)
			throws Exception {
		return encode(data, key, MacAlgorithm.SHA1.getAlgorithm());
	}

	/**
	 * 构建HmacSHA256密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initHmacSHA256Key() throws NoSuchAlgorithmException {
		return genKey(MacAlgorithm.SHA256.getAlgorithm());
	}

	/**
	 * 生成64位HmacSHA256加密数据
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeHmacSHA256(byte[] data, byte[] key)
			throws Exception {
		return encode(data, key, MacAlgorithm.SHA256.getAlgorithm());
	}

	/**
	 * 构建HmacSHA384密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initHmacSHA384Key() throws NoSuchAlgorithmException {
		return genKey(MacAlgorithm.SHA384.getAlgorithm());
	}

	/**
	 * 生成96位HmacSHA384加密数据
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeHmacSHA384(byte[] data, byte[] key)
			throws Exception {
		return encode(data, key, MacAlgorithm.SHA384.getAlgorithm());
	}

	/**
	 * 构建HmacSHA512密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initHmacSHA512Key() throws NoSuchAlgorithmException {
		return genKey(MacAlgorithm.SHA512.getAlgorithm());
	}

	/**
	 * 生成128位HmacSHA512加密信息
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeHmacSHA512(byte[] data, byte[] key)
			throws Exception {
		return encode(data, key, MacAlgorithm.SHA512.getAlgorithm());
	}

	/**
	 * 构建HmacSHA512密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] genKey(String algorithm) throws NoSuchAlgorithmException {
		// 初始化HmacMD5摘要算法的密钥产生器
		KeyGenerator generator = KeyGenerator.getInstance(algorithm);
		// 产生密钥
		SecretKey secretKey = generator.generateKey();
		// 获得密钥
		byte[] key = secretKey.getEncoded();
		return key;
	}

	/**
	 * 加密
	 * @param data
	 * @param key
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	public static byte[] encode(byte[] data, byte[] key, String algorithm) throws Exception {
		// 还原密钥
		SecretKey secretKey = new SecretKeySpec(key, algorithm);
		// 实例化Mac
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		// 初始化mac
		mac.init(secretKey);
		// 执行消息摘要
		return mac.doFinal(data);
		//return new HexBinaryAdapter().marshal(digest);// 转为十六进制的字符串
	}

	/**
	 * mac类型
	 */
	private enum MacAlgorithm{
		SHA512("HmacSHA512"),
		SHA384("HmacSHA384"),
		SHA256("HmacSHA256"),
		SHA1("HmacSHA1"),
		MD5("HmacMD5"),
		;
		private String algorithm;

		public String getAlgorithm() {
			return algorithm;
		}

		MacAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}
	}
}