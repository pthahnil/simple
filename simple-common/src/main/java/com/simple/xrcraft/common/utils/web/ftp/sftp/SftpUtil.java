package com.simple.xrcraft.common.utils.web.ftp.sftp;

/**
 * Created by pthahnil on 2019/7/12.
 */

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Slf4j
public class SftpUtil {

	public static boolean uploadFile(SftpConnection conn, String pathName, String fileName, InputStream input) {
		return uploadFile(conn, pathName, fileName, input, true);
	}

	/**
	 * 上传文件
	 * @param pathName
	 * @param fileName
	 * @param input
	 * @return
	 */
	public static boolean uploadFile(SftpConnection conn, String pathName, String fileName, InputStream input, boolean forceUpload) {

		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		try {
			if (!changeDir(conn, pathName)) {
				if(!makeDir(conn, pathName)){
					return false;
				}
			}

			int mod = forceUpload ? ChannelSftp.OVERWRITE : ChannelSftp.RESUME;
			conn.getChannel().put(input, fileName, mod);
			if (!existFile(conn, fileName)) {
				log.debug("upload failed");
				return false;
			}
			log.debug("upload successful");
			return true;
		} catch (SftpException e) {
			log.error("upload failed", e);
			return false;
		} finally {
			conn.disConnect();
		}
	}

	public static boolean downloadFile(SftpConnection conn, String remotePath, String fileName, OutputStream os) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		if (!changeDir(conn,remotePath)) {
			return false;
		}

		boolean downloadSucc = false;
		try {
			InputStream stream = conn.getChannel().get(fileName);
			if(null != stream) {
				IOUtils.copy(stream, os);
				os.flush();
				downloadSucc = true;
			}
		} catch (Exception e) {
			log.error("download file failed", e);
		} finally {
			conn.disConnect();
		}
		return downloadSucc;
	}

	/**
	 * 切换工作目录
	 * @param pathName
	 * @return
	 */
	public static boolean changeDir(SftpConnection conn, String pathName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		if (StringUtils.isBlank(pathName)) {
			log.debug("invalid pathName");
			return false;
		}
		try {
			conn.getChannel().cd(pathName.replaceAll("\\\\", "/"));
			log.debug("directory successfully changed,current dir=" + conn.getChannel().pwd());
			return true;
		} catch (SftpException e) {
			log.error("failed to change directory");
			return false;
		}
	}

	/**
	 * 切换到上一级目录
	 * @return
	 */
	public static boolean changeToParentDir(SftpConnection conn) {
		return changeDir(conn, "..");
	}

	/**
	 * 切换到根目录
	 * @return
	 */
	public static boolean changeToHomeDir(SftpConnection conn) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		String homeDir = null;
		try {
			homeDir = conn.getChannel().getHome();
		} catch (SftpException e) {
			log.error("can not get home directory");
			return false;
		}
		return changeDir(conn, homeDir);
	}

	/**
	 * 创建目录
	 * @param dirName
	 * @return
	 */
	public static boolean makeDir(SftpConnection conn,String dirName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		try {
			String[] dirs = dirName.split("/");
			StringBuffer dirPath = new StringBuffer("/");
			if(dirs.length > 1){
				for (int i = 0; i < dirs.length; i++) {
					String dirUnit = dirs[i];
					if(StringUtils.isBlank(dirUnit)) {
						continue;
					}
					dirPath.append(dirUnit).append("/");
					if(!changeDir(conn, dirPath.toString())){
						conn.getChannel().mkdir(dirPath.toString());
					}
				}
			}
			log.debug("directory successfully created,dir=" + dirName);
			return true;
		} catch (SftpException e) {
			log.error("failed to create directory");
			return false;
		}
	}

	/**
	 * 删除文件夹
	 * @param dirName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean delDir(SftpConnection conn,String dirName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		if (!changeDir(conn, dirName)) {
			return false;
		}

		Vector<LsEntry> list = null;
		try {
			list = conn.getChannel().ls(conn.getChannel().pwd());
		} catch (SftpException e) {
			log.error("can not list directory");
			return false;
		}

		for (LsEntry entry : list) {
			String fileName = entry.getFilename();
			if (!fileName.equals(".") && !fileName.equals("..")) {
				if (entry.getAttrs().isDir()) {
					delDir(conn, fileName);
				} else {
					delFile(conn, fileName);
				}
			}
		}

		if (!changeToParentDir(conn)) {
			return false;
		}

		try {
			conn.getChannel().rmdir(dirName);
			log.debug("directory " + dirName + " successfully deleted");
			return true;
		} catch (SftpException e) {
			log.error("failed to delete directory " + dirName);
			return false;
		}
	}

	/**
	 * 删除文件
	 * @param fileName 文件名
	 * @return boolean
	 */
	public static boolean delFile(SftpConnection conn,String fileName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return false;
			}
		}
		if (fileName == null || fileName.trim().equals("")) {
			log.debug("invalid filename");
			return false;
		}

		try {
			conn.getChannel().rm(fileName);
			log.debug("file " + fileName + " successfully deleted");
			return true;
		} catch (SftpException e) {
			log.error("failed to delete file " + fileName);
			return false;
		}
	}

	/**
	 * 当前目录下文件及文件夹名称列表
	 * @return String[]
	 */
	public static String[] ls(SftpConnection conn) {
		return list(conn, Filter.ALL);
	}

	/**
	 * 指定目录下文件及文件夹名称列表
	 * @return String[]
	 */
	public static String[] ls(SftpConnection conn,String pathName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return new String[0];
			}
		}
		String currentDir = currentDir(conn);
		if (!changeDir(conn, pathName)) {
			return new String[0];
		}

		String[] result = list(conn, Filter.ALL);
		if (!changeDir(conn, currentDir)) {
			return new String[0];
		}
		return result;
	}

	/**
	 * 当前目录下文件名称列表
	 * @return String[]
	 */
	public static String[] lsFiles(SftpConnection conn) {
		return list(conn, Filter.FILE);
	}

	/**
	 * 指定目录下文件名称列表
	 * @return String[]
	 */
	public static String[] lsFiles(SftpConnection conn,String pathName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return new String[0];
			}
		}
		String currentDir = currentDir(conn);
		if (!changeDir(conn, pathName)) {
			return new String[0];
		}
		String[] result = list(conn, Filter.FILE);
		if (!changeDir(conn, currentDir)) {
			return new String[0];
		}
		return result;
	}

	/**
	 * 当前目录下文件夹名称列表
	 * @return String[]
	 */
	public static String[] lsDirs(SftpConnection conn) {
		return list(conn, Filter.DIR);
	}

	/**
	 * 指定目录下文件夹名称列表
	 * @return String[]
	 */
	public static String[] lsDirs(SftpConnection conn,String pathName) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return new String[0];
			}
		}
		String currentDir = currentDir(conn);
		if (!changeDir(conn, pathName)) {
			return new String[0];
		}
		String[] result = list(conn, Filter.DIR);
		if (!changeDir(conn, currentDir)) {
			return new String[0];
		}
		return result;
	}

	/**
	 * 当前目录是否存在文件或文件夹
	 * @param name 名称
	 * @return boolean
	 */
	public static boolean exist(SftpConnection conn,String name) {
		return exist(ls(conn), name);
	}

	/**
	 * 指定目录下，是否存在文件或文件夹
	 * @param path 目录
	 * @param name 名称
	 * @return boolean
	 */
	public static boolean exist(SftpConnection conn, String path, String name) {
		return exist(ls(conn, path), name);
	}

	/**
	 * 当前目录是否存在文件
	 * @param name 文件名
	 * @return boolean
	 */
	public static boolean existFile(SftpConnection conn,String name) {
		return exist(lsFiles(conn), name);
	}

	/**
	 * 指定目录下，是否存在文件
	 * @param path 目录
	 * @param name 文件名
	 * @return boolean
	 */
	public static boolean existFile(SftpConnection conn,String path, String name) {
		return exist(lsFiles(conn,path), name);
	}

	/**
	 * 当前目录是否存在文件夹
	 * @param name 文件夹名称
	 * @return boolean
	 */
	public static boolean existDir(SftpConnection conn,String name) {
		return exist(lsDirs(conn), name);
	}

	/**
	 * 指定目录下，是否存在文件夹
	 * @param path 目录
	 * @param name 文家夹名称
	 * @return boolean
	 */
	public boolean existDir(SftpConnection conn,String path, String name) {
		return exist(lsDirs(conn, path), name);
	}

	/**
	 * 当前工作目录
	 * @return String
	 */
	public static String currentDir(SftpConnection conn) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return null;
			}
		}
		try {
			return conn.getChannel().pwd();
		} catch (SftpException e) {
			log.error("failed to get current dir");
			return homeDir(conn);
		}
	}


	//------private method ------

	/** 枚举，用于过滤文件和文件夹  */
	private enum Filter {
		/** 文件及文件夹 */ALL, /** 文件 */FILE, /** 文件夹 */DIR
	}

	;

	/**
	 * 列出当前目录下的文件及文件夹
	 * @param filter 过滤参数
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	private static String[] list(SftpConnection conn,Filter filter) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return new String[0];
			}
		}
		Vector<LsEntry> list = null;
		try {
			//ls方法会返回两个特殊的目录，当前目录(.)和父目录(..)
			list = conn.getChannel().ls(conn.getChannel().pwd());
		} catch (SftpException e) {
			log.error("can not list directory");
			return new String[0];
		}

		List<String> resultList = new ArrayList<String>();
		for (LsEntry entry : list) {
			if (filter(entry, filter)) {
				resultList.add(entry.getFilename());
			}
		}
		return resultList.toArray(new String[0]);
	}

	/**
	 * 判断是否是否过滤条件
	 * @param entry LsEntry
	 * @param f 过滤参数
	 * @return boolean
	 */
	private static boolean filter(LsEntry entry, Filter f) {
		if (f.equals(Filter.ALL)) {
			return !entry.getFilename().equals(".") && !entry.getFilename().equals("..");
		} else if (f.equals(Filter.FILE)) {
			return !entry.getFilename().equals(".") && !entry.getFilename().equals("..") && !entry
					.getAttrs().isDir();
		} else if (f.equals(Filter.DIR)) {
			return !entry.getFilename().equals(".") && !entry.getFilename().equals("..") && entry
					.getAttrs().isDir();
		}
		return false;
	}

	/**
	 * 根目录
	 * @return String
	 */
	private static String homeDir(SftpConnection conn) {
		//登录
		if(!conn.getConnnected()) {
			boolean loginSucc = conn.connect();
			if(!loginSucc){
				return null;
			}
		}
		try {
			return conn.getChannel().getHome();
		} catch (SftpException e) {
			return "/";
		}
	}

	/**
	 * 判断字符串是否存在于数组中
	 * @param strArr 字符串数组
	 * @param str 字符串
	 * @return boolean
	 */
	private static boolean exist(String[] strArr, String str) {
		if (strArr == null || strArr.length == 0) {
			return false;
		}
		if (str == null || str.trim().equals("")) {
			return false;
		}
		for (String s : strArr) {
			if (s.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

}