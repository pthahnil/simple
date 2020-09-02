package com.simple.xrcraft.common.utils.web.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by pthahnil on 2019/1/24.
 */
@Slf4j
public class FileDownloader {

	/**
	 * 文件下载,支持断点续传
	 * @param url 下载地址
	 * @param dir 存储目录
	 */
	public static void download(String url, String dir) throws Exception {
		if(StringUtils.isBlank(url) || StringUtils.isBlank(dir)){
			log.info("url或者本地目录dir为指定");
			return;
		}

		OutputStream output = null;
		try {
			URL remoteUrl = new URL(url);

			File localDirectory = new File(dir);
			if(!localDirectory.exists()){
				localDirectory.mkdirs();
			}

			if(!dir.endsWith("/")){
				dir = dir + "/";
			}
			String fileName = url.substring(url.lastIndexOf("/"));
			if(fileName.contains("?")) {
				fileName = fileName.substring(0, fileName.indexOf("?"));
			}

			File dest = new File(dir + fileName);
			long startPosition = 0;
			if(dest.exists()){
				startPosition = dest.length();
			}
			output = new FileOutputStream(dest, true);

			URLConnection connection = remoteUrl.openConnection();
			connection.setRequestProperty("Range", "bytes=" + startPosition + "-");
			connection.connect();
			InputStream input = new BufferedInputStream(connection.getInputStream());

			IOUtils.copy(input, output);
		} catch (Exception e){
			log.error("文件下载出错", e);
			throw e;
		} finally {
			if(null != output){
				output.close();
			}
		}
	}

}
