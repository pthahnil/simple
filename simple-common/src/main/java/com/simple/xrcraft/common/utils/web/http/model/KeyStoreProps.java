package com.simple.xrcraft.common.utils.web.http.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * @description:
 * @author pthahnil
 * @date 2020/10/16 10:01
 */
@Data
public class KeyStoreProps {

	/**keystore 的流*/
	private InputStream stream;

	/**keystore 的密码*/
	private String certPwd;

	/**用于获取线程池的key*/
	private String keyStoreAlias;

	private String keyStoreType = "JKS";

	public KeyStoreProps() { }

	public KeyStoreProps(InputStream stream, String certPwd, String keyStoreAlias) {
		this.stream = stream;
		this.certPwd = certPwd;
		this.keyStoreAlias = keyStoreAlias;
	}

	public KeyStore getKeyStore() throws Exception {

		if(null == stream) {
			return null;
		}

		char[] pass = null;
		if(StringUtils.isNotBlank(certPwd)){
			pass = certPwd.toCharArray();
		}

		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(stream, pass);

		return ks;
	}
}
