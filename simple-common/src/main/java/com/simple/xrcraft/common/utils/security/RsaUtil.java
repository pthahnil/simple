package com.simple.xrcraft.common.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by pthahnil on 2019/5/13.
 */
@Slf4j
public class RsaUtil {

	/**
	 * 加密算法RSA
	 */
	public static final String ALGORITHM = "RSA";

	public static final String DEFAULT_PADDING = "RSA/ECB/PKCS1PADDING";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 加密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] content, byte[] key) throws Exception {
		return encrypt(content, key, DEFAULT_PADDING);
	}

	public static byte[] encrypt(byte[] content, PublicKey key) throws Exception {
		return encrypt(content, key, DEFAULT_PADDING);
	}

	/**
	 * 加密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] content, byte[] key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "publicKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.ENCRYPT_MODE, paddingMod);
	}

	public static byte[] encrypt(byte[] content, PublicKey key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "publicKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.ENCRYPT_MODE, paddingMod);
	}

	/**
	 * 解密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] key) throws Exception {
		return decrypt(content, key, DEFAULT_PADDING);
	}

	public static byte[] decrypt(byte[] content, PrivateKey key) throws Exception {
		return decrypt(content, key, DEFAULT_PADDING);
	}

	/**
	 * 解密
	 * @param content
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "privateKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}

		return process(content, key, Cipher.DECRYPT_MODE, paddingMod);
	}

	public static byte[] decrypt(byte[] content, PrivateKey key, String paddingMod) throws Exception {
		if(null == content){
			return null;
		}
		if(null == key){
			String info = "privateKey is empty";
			log.info(info);
			throw new RuntimeException(info);
		}
		if(StringUtils.isBlank(paddingMod)){
			String info = "please choose a padding mod";
			log.info(info);
			throw new RuntimeException(info);
		}
		return process(content, key, Cipher.DECRYPT_MODE, paddingMod);
	}


	private static byte[] process(byte[] data, byte[] keyBytes, int opmode, String paddingMod) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		Key key;
		if(opmode == Cipher.ENCRYPT_MODE){
			key = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
		} else if(opmode == Cipher.DECRYPT_MODE){
			key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
		} else {
			throw new Exception("不支持加解密之外的操作");
		}

		return process(data, key, opmode, paddingMod);
	}

	/**
	 * RSA算法分段加解密数据
	 * @param data
	 * @param key
	 * @param opmode
	 * @return
	 * @throws Exception
	 */
	private static byte[] process(byte[] data, Key key, int opmode, String paddingMod) throws Exception {

		int keySize = getKeySize(key);

		Cipher cipher = Cipher.getInstance(paddingMod);
		cipher.init(opmode, key);

		int maxBlock;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;

		byte[] resultDatas;
		try {
			while (data.length > offSet) {
				if (data.length - offSet > maxBlock) {
					buff = cipher.doFinal(data, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(data, offSet, data.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
			resultDatas = out.toByteArray();
		} finally {
			if(null != out){
				try { out.close(); } catch (Exception e) { }
			}
		}
		return resultDatas;
	}

	/**
	 * key长度
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static int getKeySize(Key key) throws Exception {
		if(null == key){
			throw new RuntimeException("key is null");
		} else if(key instanceof RSAPublicKey){
			RSAPublicKey pubKey = (RSAPublicKey) key;
			return pubKey.getModulus().toString(2).length();
		} else if(key instanceof RSAPrivateKey){
			RSAPrivateKey priKey = (RSAPrivateKey) key;
			return priKey.getModulus().toString(2).length();
		} else {
			throw new RuntimeException("neither a rsa private nor a public key");
		}
	}

}
