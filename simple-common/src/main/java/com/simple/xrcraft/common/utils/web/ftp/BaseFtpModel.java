package com.simple.xrcraft.common.utils.web.ftp;

import lombok.Data;

/**
 * Created by pthahnil on 2019/7/19.
 */
@Data
public class BaseFtpModel {

	/** SFTP服务器IP地址 */
	private String host;

	/** SFTP服务器端口 */
	private int port;

	/** 连接超时时间，单位毫秒  */
	private int timeout;

	/** 用户名 */
	private String username;

	/** 密码 */
	private String password;

	private Boolean connnected = false;

	public BaseFtpModel(String host, int port, int timeout, String username, String password) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.username = username;
		this.password = password;
	}

	public BaseFtpModel() {
	}
}
