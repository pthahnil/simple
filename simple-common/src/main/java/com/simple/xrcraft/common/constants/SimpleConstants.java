package com.simple.xrcraft.common.constants;

import lombok.Getter;

/**
 * @description: 详细文档参见 https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyStore
 * @author pthahnil
 * @date 2019/12/14 10:17
 */
public class SimpleConstants {

	/**
	 * keystore 类型
	 */
	@Getter
	public enum KeyStoreType{
		JCEKS("jceks"),
		JKS("jks"),
		PKCS12("pkcs12"),
		;
		private String type;

		KeyStoreType(String type) {
			this.type = type;
		}
	}



}
