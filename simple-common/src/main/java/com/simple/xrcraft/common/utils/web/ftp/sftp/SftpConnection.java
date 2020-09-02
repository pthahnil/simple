package com.simple.xrcraft.common.utils.web.ftp.sftp;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.simple.xrcraft.common.utils.web.ftp.BaseFtpModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by pthahnil on 2019/7/12.
 */
@Data
@Slf4j
public class SftpConnection extends BaseFtpModel {

	/** Channel */
	private ChannelSftp channel = null;

	public SftpConnection(String host, Integer port, Integer timeout, String username, String password) {
		super(host, port, timeout, username, password);
		if(null == port) {
			setPort(22);
		}
		if(null == timeout) {
			setTimeout(1000 * 60);
		}
	}

	public boolean login() {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(getUsername(), getHost(), getPort());
			if (StringUtils.isNotBlank(getPassword())) {
				session.setPassword(getPassword());
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setTimeout(getTimeout());
			session.connect();
			log.debug("sftp session connected");

			log.debug("opening sftp channel");
			//文件传输
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			this.channel = channel;

			log.debug("connected successfully");
			this.setConnnected(true);
			return true;
		} catch (JSchException e) {
			log.error("sftp login failed", e);
			return false;
		}
	}

	/**
	 * 登陆
	 * @return
	 */
	public boolean connect(){
		boolean loginSucc = login();
		if(! loginSucc) {
			log.info("login failed");
			return false;
		}
		if(null == channel) {
			return false;
		}
		if(!channel.isConnected()) {
			try { channel.connect(); } catch (Exception e) { return false; }
		}
		return true;
	}

	/**
	 * 关闭连接
	 * @param
	 */
	public void disConnect(){
		if(null != channel && ! channel.isClosed()) {
			setConnnected(false);
			try {channel.disconnect();} catch (Exception e){}
			try {channel.getSession().disconnect();} catch (Exception e){}
		}
	}

	public SftpConnection() {
		super();
	}

	public ChannelExec getExec() throws JSchException {
		return (ChannelExec) channel.getSession().openChannel("exec");
	}

	public String executeCmd(List<String> commands){
		if(CollectionUtils.isEmpty(commands)) {
			return null;
		}
		ChannelExec exec = null;
		try {
			exec = getExec();
			InputStream in = exec.getInputStream();

			//命令
			String commandLine = commands.stream().collect(Collectors.joining(";"));
			exec.setCommand(commandLine);

			exec.connect();

			List<String> lines = IOUtils.readLines(in,"UTF8");
			return lines.stream().collect(Collectors.joining("\n"));
		} catch (Exception e) {
			log.error("command exe err:", e);
		} finally {
			if(null != exec) {
				try {exec.disconnect();} catch (Exception e){}
			}
		}
		return null;
	}
}
