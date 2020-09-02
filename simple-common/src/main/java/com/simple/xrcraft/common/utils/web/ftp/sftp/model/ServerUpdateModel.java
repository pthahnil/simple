package com.simple.xrcraft.common.utils.web.ftp.sftp.model;

import com.simple.xrcraft.common.utils.web.ftp.sftp.SftpConnection;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2020/6/12 15:39
 */
@Data
public class ServerUpdateModel {

	private Boolean isWar = false;

	private List<String> uploadFileNames;

	private String tomcatPath;

	private SftpConnection conn;

	public Boolean allLib() {
		return !isWar && CollectionUtils.isEmpty(uploadFileNames);
	}

	public boolean clearPath(){
		return isWar || allLib();
	}
}
