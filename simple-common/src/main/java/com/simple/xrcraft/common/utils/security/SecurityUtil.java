package com.simple.xrcraft.common.utils.security;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @description:
 * @author pthahnil
 * @date 2021/4/30 9:03
 */
public class SecurityUtil {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * main process
	 * @param inStream
	 * @param outStream
	 * @param paddingAlgorithm
	 * @param opMode
	 * @param key
	 * @throws Exception
	 */
	protected static void universalProcess(InputStream inStream, OutputStream outStream, String paddingAlgorithm
			, int opMode, Key key, AlgorithmParameterSpec ivSpec) throws Exception {

		Cipher cipher = Cipher.getInstance(paddingAlgorithm);
		cipher.init(opMode, key, ivSpec);

		CipherOutputStream cipherOutputStream = new CipherOutputStream(outStream, cipher);

		IOUtils.copy(inStream, cipherOutputStream);

		cipherOutputStream.close();
	}

}
