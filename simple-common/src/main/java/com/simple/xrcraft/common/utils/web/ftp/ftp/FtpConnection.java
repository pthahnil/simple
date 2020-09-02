package com.simple.xrcraft.common.utils.web.ftp.ftp;

import com.simple.xrcraft.common.utils.web.ftp.BaseFtpModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Created by pthahnil on 2019/7/12.
 */
@Slf4j
@Data
public class FtpConnection extends BaseFtpModel {

	/** client */
	private FTPClient client;

	public FtpConnection(String host, String username, String password) {
		this(host, null, null, username, password);
	}

	public FtpConnection(String host, Integer port, Integer timeout, String username, String password) {
		super(host, port, timeout, username, password);
		if(null == port) {
			setPort(21);
		}
		if(null == timeout) {
			setTimeout(1000 * 60);
		}
	}

	/**
	 * login
	 * @param
	 * @return
	 */
	public boolean connect(){
		FTPClient client = new FTPClient();
		try {
			client.setDataTimeout(getTimeout());
			client.setConnectTimeout(getTimeout());
			client.connect(getHost(), getPort());
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}

			client.login(getUsername(), getPassword());
			setConnnected(true);

			this.client = client;
			return true;
		} catch (Exception e){
			log.error("ftp server:{} login failed", getHost());
			return false;
		}
	}

	public boolean disconnect(){
		if(null != getClient() && getClient().isConnected()) {
			setConnnected(false);
			try { getClient().disconnect(); } catch (Exception e) { return false; }
		}
		return true;
	}


	public FtpConnection() {
		super();
	}

	private Boolean isPassive = false;
}
