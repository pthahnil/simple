package com.simple.xrcraft.common.utils.security;

/**
 * Created by pthahnil on 2019/5/28.
 */
public enum PaddingMode {
	/* 以下为所有sun提供的padding
	AES/CBC/NoPadding
	AES/CBC/PKCS5Padding
	AES/ECB/NoPadding
	AES/ECB/PKCS5Padding
	DES/CBC/NoPadding
	DES/CBC/PKCS5Padding
	DES/ECB/NoPadding
	DES/ECB/PKCS5Padding
	DESede/CBC/NoPadding
	DESede/CBC/PKCS5Padding
	DESede/ECB/NoPadding
	DESede/ECB/PKCS5Padding
	RSA/ECB/PKCS1Padding
	RSA/ECB/OAEPWithSHA-1AndMGF1Padding
	RSA/ECB/OAEPWithSHA-256AndMGF1Padding*/
	DES_CBC("DESede/CBC/PKCS5Padding"),
	DES_ECB("DESede/ECB/PKCS5Padding"),
	AES_CBC("AES/CBC/PKCS5Padding"),
	AES_ECB("AES/ECB/PKCS5Padding"),
	;
	String type;

	public String getType() {
		return type;
	}

	PaddingMode(String type) {
		this.type = type;
	}
}
