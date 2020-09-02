package com.simple.xrcraft.common.utils.web.ftp.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pthahnil on 2019/7/19.
 */
@Slf4j
public class FtpUtil {

	/**
	 * download file
	 * @param conn
	 * @param serverPath
	 * @param serverFileName
	 * @return
	 */
	public static boolean download(FtpConnection conn, String serverPath, String serverFileName, String localFile){
		//login
		try {
			String msg = null;

			//登录
			if(!conn.getConnnected()) {
				boolean loginSucc = conn.connect();
				if(!loginSucc){
					return false;
				}
			}

			FTPClient client = conn.getClient();
			if(conn.getIsPassive()) {
				client.enterLocalPassiveMode();
			}
			if(StringUtils.isNotBlank(serverFileName)){
				boolean pathChanged = client.changeWorkingDirectory(serverPath);
				if(!pathChanged){
					msg = "file path may not exists";
					log.info(msg);
					throw new Exception(msg);
				}
			}
			File file = new File(localFile);
			File parent = file.getParentFile();
			if(!parent.exists()){
				parent.mkdirs();
			}
			if(file.exists()){
				file.delete();
			}

			InputStream is = client.retrieveFileStream(serverFileName);
			if(null == is){
				msg = "file may not exists";
				log.info(msg);
				throw new Exception(msg);
			}
			OutputStream outStream = new FileOutputStream(file);
			IOUtils.copy(is, outStream);

			return true;
		} catch (Exception e) {
			log.error("download file:{} from:{} failed", serverFileName, conn.getHost());
			return false;
		} finally {
			conn.disconnect();
		}
	}

	/**
	 * upload file
	 * @param conn
	 * @param serverPath
	 * @param serverFileName
	 * @return
	 */
	public static boolean upload(FtpConnection conn, String serverPath, String serverFileName, InputStream stream){

		try {
			String msg = null;
			FTPClient client = conn.getClient();
			if(conn.getIsPassive()) {
				client.enterLocalPassiveMode();
			}
			if(StringUtils.isNotBlank(serverFileName)){
				boolean pathChanged = client.changeWorkingDirectory(serverPath);
				if(!pathChanged){
					msg = "file path may not exists";
					log.info(msg);
					throw new Exception(msg);
				}
			}

			boolean uploadSucc = client.storeFile(serverFileName, stream);
			if(!uploadSucc){
				msg = "upload failed";
				log.info(msg);
				throw new Exception(msg);
			}
			return uploadSucc;
		} catch (Exception e) {
			log.error("upload file:{} to:{} failed", serverFileName, conn.getHost());
			return false;
		} finally {
			conn.disconnect();
		}
	}

}
