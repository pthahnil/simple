package com.simple.xrcraft.common.utils.web.ftp.sftp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2020/4/29 9:05
 */
public class LinuxServerCommand {

	/**
	 * 解压war包
	 * @param webAppName
	 * @param tomcatPath
	 * @param warFileName
	 * @return
	 */
	public static List<String> unzipWar(String webAppName, String tomcatPath, String warFileName){

		String webPath = tomcatPath + "/webapps/" + webAppName;

		List<String> commands = new ArrayList<>();
		commands.add("cd " + webPath);
		commands.add("source /etc/profile");
		commands.add("jar -xvf " + warFileName);
		commands.add("rm -f " + warFileName);
		return commands;
	}

	/**
	 * 备份web目录
	 * @param webAppName
	 * @param tomcatPath
	 * @return
	 */
	public static List<String> backupWeb(String webAppName, String tomcatPath){

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		String webPath = tomcatPath + "/webapps";
		String backupPath = tomcatPath + "/backUp";

		String backUpFileName = webAppName + "." + today + ".tar";

		List<String> commands = new ArrayList<>();

		String backupPathToday = backupPath + "/" + today;

		commands.add("cd " + webPath);
		commands.add("mkdir " + backupPathToday);
		commands.add("tar -cvf "+ backUpFileName +" ./");
		commands.add("mv " + backUpFileName + " " + backupPath);

		return commands;
	}

	/**
	 * 重启tomcat
	 * @param tomcatPath
	 * @return
	 */
	public static List<String> restartTomcat(String tomcatPath){
		List<String> commands = new ArrayList<>();

		List<String> shutDown = tomcatShutDown(tomcatPath);
		commands.addAll(shutDown);

		List<String> startUp = tomcatStartUp(tomcatPath);
		commands.addAll(startUp);

		return commands;
	}

	/**
	 * 停掉tomcat
	 * @param tomcatPath
	 * @return
	 */
	public static List<String> tomcatShutDown(String tomcatPath){
		List<String> commands = new ArrayList<>();
		commands.add("pkill -9 -f " + tomcatPath);
		return  commands;
	}

	/**
	 * 启动tomcat
	 * @param tomcatPath
	 * @return
	 */
	public static List<String> tomcatStartUp(String tomcatPath){
		List<String> commands = new ArrayList<>();
		commands.add("source /etc/profile");
		commands.add("sh " + tomcatPath + "/bin/startup.sh");
		return  commands;
	}

}
