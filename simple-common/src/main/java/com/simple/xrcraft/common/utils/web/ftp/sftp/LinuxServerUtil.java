package com.simple.xrcraft.common.utils.web.ftp.sftp;

import com.simple.xrcraft.common.utils.web.ftp.sftp.model.ServerUpdateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 只用于开发测试！！
 * @author pthahnil
 * @date 2020/4/15 9:47
 */
public class LinuxServerUtil {

	public static void upload(List<File> files, SftpConnection conn, String serverPath) throws Exception {
		for (File file : files) {
			boolean succ = SftpUtil.uploadFile(conn, serverPath, file.getName(), new FileInputStream(file));
			System.out.println("upload file :" + file.getName() + " is success:" + succ);
		}
	}

	/**
	 * 本地文件
	 * @param fileNames
	 */
	public static List<File> getLocalFiles(List<String> fileNames, String localLibPath, boolean isWar) throws Exception {
		File classPath = new File(LinuxServerUtil.class.getResource("/").getPath());
		//target 目录
		String basePath = classPath.getParentFile().getCanonicalPath();

		//本地路径
		String localPath = isWar ? basePath : localLibPath;

		List<File> files = new ArrayList<>();
		//全量部署，war包直接丢上去
		if(isWar) {
			files.add(warFile());
		} else {
			File localPathDir = new File(localPath);
			if(!localPathDir.exists()){
				throw new FileNotFoundException("本地好像没有打包");
			}

			if(CollectionUtils.isEmpty(fileNames)) {
				//指定的文件为空，上传整个lib目录
				files = Arrays.stream(localPathDir.listFiles())
						.filter(fil -> fil.getName().endsWith(".jar"))
						.collect(Collectors.toList());
			} else {
				//上传指定的文件
				for (String fileName : fileNames) {
					File file = new File(localPath, fileName);
					if(!file.exists()) {
						throw new FileNotFoundException("本地好像没有打包");
					}
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * 本地的war文件名
	 * @return
	 */
	public static File warFile() throws Exception{
		File file = new File(LinuxServerUtil.class.getResource("/").getPath());
		File targetPath = file.getParentFile();
		File warFile = Arrays.stream(targetPath.listFiles()).filter(childFil -> childFil.isFile() && childFil.getName().endsWith(".war"))
				.findAny().orElse(null);
		if(null == warFile || !warFile.exists()){
			throw new Exception("本地好像没有打包");
		}
		return warFile;
	}

	/**
	 * 清空web目录
	 * @param conn
	 * @param warFileName
	 */
	public static void unzipWarFile(SftpConnection conn, String tomcatPath, String warFileName, String appName) {

		List<String> commands = LinuxServerCommand.unzipWar(appName, tomcatPath, warFileName);
		String result = conn.executeCmd(commands);

		System.out.println(result);
	}

	/**
	 * 清空目录
	 * @param conn
	 * @param path
	 */
	public static void clearPath(SftpConnection conn, String path) {
		List<String> commands = new ArrayList<>();
		commands.add("cd " + path);
		commands.add("rm -rf * ");
		String result = conn.executeCmd(commands);

		System.out.println(result);
	}

	/**
	 * 备份文件
	 * @param conn
	 */
	public static void backup(SftpConnection conn, String tomcatPath, String appName) {

		List<String> commands = LinuxServerCommand.backupWeb(appName, tomcatPath);
		String result = conn.executeCmd(commands);

		System.out.println(result);
	}

	/**
	 * 停止tomcat
	 * @param conn
	 * @param tomcatPath
	 */
	public static void tomcatShutDown(SftpConnection conn, String tomcatPath){
		List<String> commands = LinuxServerCommand.tomcatShutDown(tomcatPath);
		String result = conn.executeCmd(commands);

		System.out.println(result);

	}

	/**
	 * 启动tomcat
	 * @param conn
	 * @param tomcatPath
	 */
	public static void tomcatStartUp(SftpConnection conn, String tomcatPath){
		List<String> commands = LinuxServerCommand.tomcatStartUp(tomcatPath);
		String result = conn.executeCmd(commands);

		System.out.println(result);
	}

	/**
	 * 本地target目录
	 * @return
	 * @throws Exception
	 */
	public static String getBasePath() throws Exception {
		File classPath = new File(LinuxServerUtil.class.getResource("/").getPath());
		//target 目录
		return classPath.getParentFile().getCanonicalPath();
	}

	/**
	 * tomcat路径
	 * @param userName
	 * @return
	 */
	public static String getTomcatPath(String userName){
		return  "/opt/appl/" + userName + "/tomcat";
	}

	/**
	 * web上下文基本路径
	 * @param tomcatPath
	 * @param webAppName
	 * @return
	 */
	public static String getWebPath(String tomcatPath, String webAppName){
		return tomcatPath + "/webapps/" + webAppName;
	}

	/**
	 * 递归查找文件
	 * @param file
	 * @param fileNames
	 * @return
	 */
	public static List<File> getFilesByName(File file , List<String> fileNames){
		if(!file.exists()) {
			return null;
		}
		if(!file.isDirectory()) {
			return null;
		}

		File[] files = file.listFiles();
		if(null == files || files.length == 0){
			return null;
		}

		List<File> retFiles = new ArrayList<>();
		Map<Boolean, List<File>> groupedFiles = Arrays.stream(files).collect(Collectors.partitioningBy(unit -> unit.isDirectory()));
		List<File> subFiles = groupedFiles.get(false);
		if(CollectionUtils.isNotEmpty(subFiles)) {
			List<File> subTarget =  subFiles.stream().filter(unit -> fileNames.contains(unit.getName())).collect(Collectors.toList());
			retFiles.addAll(subTarget);
		}

		List<File> subDirs = groupedFiles.get(true);
		if(CollectionUtils.isNotEmpty(subDirs)) {
			for (File subDir : subDirs) {
				List<File> subRetFiles = getFilesByName(subDir, fileNames);
				if(CollectionUtils.isNotEmpty(subRetFiles)) {
					retFiles.addAll(subRetFiles);
				}
			}
		}
		return retFiles;
	}

	/**
	 * lib目录
	 * @param file
	 * @return
	 */
	public static List<File> getAllJars(File file){
		if(!file.exists()) {
			return null;
		}
		if(!file.isDirectory()) {
			return null;
		}
		File[] files = file.listFiles();
		if(null == files || files.length == 0){
			return null;
		}

		List<File> retFiles = new ArrayList<>();
		if(file.getName().endsWith("lib")) {
			retFiles = Arrays.asList(files);
		} else {
			List<File> subDirs = Arrays.stream(files).filter(unit -> unit.isDirectory()).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(subDirs)){
				for (File subDir : subDirs) {
					List<File> subRetFiles = getAllJars(subDir);
					if(CollectionUtils.isNotEmpty(subRetFiles)) {
						retFiles.addAll(subRetFiles);
					}
				}
			}
		}
		return retFiles;
	}


	/**
	 * 单个文件上传
	 * @param file
	 * @param conn
	 * @param serverPath
	 * @throws Exception
	 */
	public static void singleUpload(File file, SftpConnection conn, String serverPath) throws Exception {
		boolean succ = SftpUtil.uploadFile(conn, serverPath, file.getName(), new FileInputStream(file));
		System.out.println("upload file :" + file.getName() + " is success:" + succ);
	}


	/**
	 * 本地文件
	 * @param fileNames
	 */
	public static List<File> getLocalFilesV2(List<String> fileNames, String webInfoPath, boolean isWar) throws Exception {
		File classPath = new File(LinuxServerUtil.class.getResource("/").getPath());
		//target 目录
		String basePath = classPath.getParentFile().getCanonicalPath();

		//本地路径
		String localPath = isWar ? basePath : webInfoPath;

		List<File> files = new ArrayList<>();
		//全量部署，war包直接丢上去
		if(isWar) {
			files.add(warFile());
		} else {
			File localPathDir = new File(localPath);
			if(!localPathDir.exists()){
				throw new FileNotFoundException("本地好像没有打包");
			}

			if(CollectionUtils.isEmpty(fileNames)) {
				//指定的文件为空，上传整个lib目录
				files = getAllJars(localPathDir);
			} else {
				//上传指定的文件
				files = getFilesByName(localPathDir, fileNames);
			}
		}
		return files;
	}

	/**
	 * 重启tomcat
	 * @param conn
	 */
	public static void restartTomcat(SftpConnection conn, String tomcatPath){
		try {
			//开发环境
			conn.connect();
			//停tomcat
			tomcatShutDown(conn, tomcatPath);
			//起tomcat
			tomcatStartUp(conn, tomcatPath);
			conn.disConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 本地webpath路径
	 * @return
	 */
	public static String localWebPath() throws Exception {
		File targetPath = new File(getBasePath());
		if(!targetPath.exists()){
			throw new Exception("target 目录不存在");
		}

		File[] files = targetPath.listFiles();
		if(null == files || files.length ==0 ){
			throw new Exception("target 目录为空");
		}

		File warFile = Arrays.stream(files).filter(file -> file.getName().endsWith(".war")).findAny().orElse(null);
		if(null == warFile || !warFile.exists()){
			throw new Exception("尚未打包");
		}

		String warName = warFile.getName();
		String rawName = warName.substring(0, warName.indexOf(".war"));

		String str = null;
		for (File file : files) {
			if(file.isFile()){
				continue;
			}
			String fileName = file.getName();
			if(fileName.startsWith(rawName)){
				str = file.getAbsolutePath();
				break;
			}
		}

		if(StringUtils.isBlank(str)){
			throw new Exception("local webpath is null");
		}

		return str;
	}

	/**
	 * web上下文
	 * @param conn
	 * @param tomcatPath
	 * @return
	 * @throws Exception
	 */
	public static String getWebAppName(SftpConnection conn, String tomcatPath) throws Exception {
		List<String> commands = new ArrayList<>();
		commands.add("cd " + tomcatPath + "/webapps");
		commands.add("ls");
		String content = conn.executeCmd(commands);
		if(StringUtils.isBlank(content)){
			throw new Exception("上下文目录不存在");
		}
		String[] segs = content.split("\n");

		return Arrays.stream(segs).filter(seg -> !seg.contains(".")).findAny().orElse(null);
	}

	/**
	 * 完整的流程
	 * @param model
	 */
	public static void uploadProcess(ServerUpdateModel model) {
		try {
			SftpConnection conn = model.getConn();
			conn.connect();

			List<String> fileNames = model.getUploadFileNames();

			//服务器的tomcat路径
			String tomcatPath = model.getTomcatPath();
			//本地打包后的web目录
			String localWebPath = LinuxServerUtil.localWebPath();

			//服务器的web目录
			String appContext = LinuxServerUtil.getWebAppName(conn, tomcatPath);

			//服务器的app上下文路径
			String webPath = LinuxServerUtil.getWebPath(tomcatPath, appContext);
			String libPath = webPath + "/WEB-INF/lib";

			String serverPath = model.getIsWar() ? webPath : libPath;

			if (model.clearPath()) {
				//清空目录
				LinuxServerUtil.clearPath(conn, serverPath);
			}

			//备份web目录
			LinuxServerUtil.backup(conn, tomcatPath, appContext);

			//本地需要上传的文件
			List<File> files = LinuxServerUtil.getLocalFilesV2(fileNames, localWebPath, model.getIsWar());

			//停tomcat
			LinuxServerUtil.tomcatShutDown(conn, tomcatPath);

			//上传
			for (File file : files) {
				String fileServerPath;
				if (model.getIsWar()) {
					//war包
					fileServerPath = webPath;
				} else {
					//其他文件
					String fileName = file.getName();
					String path = file.getAbsolutePath();

					//相对路径，服务器和本地是一致的
					String relativePath = path.substring(localWebPath.length(), path.indexOf(fileName));

					fileServerPath = webPath + "/" + relativePath;
				}
				LinuxServerUtil.singleUpload(file, conn, fileServerPath);
			}

			if (model.getIsWar()) {
				//解压war包
				LinuxServerUtil.unzipWarFile(conn, tomcatPath, files.get(0).getName(), appContext);
			}
			//起tomcat
			LinuxServerUtil.tomcatStartUp(conn, tomcatPath);

			conn.disConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
