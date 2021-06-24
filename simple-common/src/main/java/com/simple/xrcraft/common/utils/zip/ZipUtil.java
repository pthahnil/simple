package com.simple.xrcraft.common.utils.zip;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @description:
 * @author pthahnil
 * @date 2020/5/15 15:12
 */
public class ZipUtil {

	/**
	 * 递归压缩
	 * @param fileToZip
	 * @param fileName
	 * @throws IOException
	 */
	public static void zipFile(File fileToZip, String fileName) throws
			IOException {
		if(null == fileToZip || !fileToZip.exists()){
			throw new IOException("file to zip does't exists");
		}
		if(StringUtils.isBlank(fileName)) {
			fileName = fileToZip.getName();
		}

		String baseName = FilenameUtils.getBaseName(fileName);
		String targetFileName = baseName + ".zip";
		File zipFile = new File(fileToZip.getParent(), targetFileName);

		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			zipFile(fileToZip, fileName, zos);
			zos.flush();
			fos.flush();
		} finally {
			IOUtils.closeQuietly(zos);
			IOUtils.closeQuietly(fos);
		}

	}

	/**
	 * 递归压缩
	 * @param fileToZip
	 * @param fileName
	 * @param zipOut
	 * @throws IOException
	 */
	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws
			IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			String entryName = fileName.endsWith("/") ? fileName : fileName + "/";
			zipOut.putNextEntry(new ZipEntry(entryName));
			zipOut.closeEntry();
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * 递归解压
	 * @param file
	 * @throws Exception
	 */
	public static List<File> unzip(File file) throws Exception {
		if(null == file || !file.exists()){
			throw new Exception("file does't exists");
		}

		ZipFile zipFile = new ZipFile(file);
		List<File> files = new ArrayList<>();

		try {
			Enumeration<ZipEntry> entryIt = (Enumeration<ZipEntry>) zipFile.entries();
			String basePath = file.getParentFile().getCanonicalPath();
			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			while (entryIt.hasMoreElements()) {
				ZipEntry entry = entryIt.nextElement();

				String fileName = basePath + entry.getName();
				File unzipedFile = new File(fileName);
				if(entry.isDirectory()) {
					if(!unzipedFile.exists()){
						unzipedFile.mkdirs();
					}
				} else {
					if(unzipedFile.exists()) {
						unzipedFile.delete();
					}
					unzipedFile.createNewFile();
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(unzipedFile);
						InputStream ins = zipFile.getInputStream(entry);
						IOUtils.copy(ins, fos);
						fos.flush();
						files.add(unzipedFile);
					} finally {
						IOUtils.closeQuietly(fos);
					}
				}
			}
		} finally {
			try {zipFile.close();} catch (Exception e){}
		}
		return files;
	}
}
