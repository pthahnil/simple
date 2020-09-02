package com.simple.xrcraft.common.utils.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by pthahnil on 2019/11/10.
 */
public class PBEUtil {

	private static int ITERATION_COUNT = 20;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 加密
	 * @param planContent
	 * @param pwd
	 * @param algoName
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] planContent, String pwd, String algoName) throws Exception {

		byte[] salt = genSalt();
		SecretKey pbeKey = str2Key(pwd, algoName);

		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
		Cipher pbeCipher = Cipher.getInstance(algoName);
		pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
		byte[] ciphertext = pbeCipher.doFinal(planContent);

		// Now construct  PKCS #8 EncryptedPrivateKeyInfo object
		AlgorithmParameters algparms = AlgorithmParameters.getInstance(algoName);
		algparms.init(pbeParamSpec);
		EncryptedPrivateKeyInfo encinfo = new EncryptedPrivateKeyInfo(algparms, ciphertext);

		// and here we have it! a DER encoded PKCS#8 encrypted key!
		return encinfo.getEncoded();
	}

	/**
	 * 解密
	 * @param encryptedInfo
	 * @param passwd
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] encryptedInfo, String passwd) throws Exception{
		EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(encryptedInfo);
		SecretKey pbeKey = str2Key(passwd, encryptPKInfo.getAlgName());

		Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());

		AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);

		PKCS8EncodedKeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
		return pkcs8KeySpec.getEncoded();
	}


	/**
	 * "盐"初始化<br>
	 * 盐长度必须为8字节
	 * @return byte[] 盐
	 * @throws Exception
	 */
	private static byte[] genSalt() throws Exception {
		//实例化安全随机数
		SecureRandom random = new SecureRandom();
		//产出盐
		return random.generateSeed(8);
	}

	/**
	 * 转换密钥
	 * @param password 密码
	 * @return Key密钥
	 * @throws Exception
	 *
	 */
	private static SecretKey str2Key(String password, String algName) throws Exception {
		//密钥材料转换
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		//实例化
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algName);
		//生成密钥
		return keyFactory.generateSecret(keySpec);
	}

}
