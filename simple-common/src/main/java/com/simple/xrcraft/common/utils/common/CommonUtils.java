package com.simple.xrcraft.common.utils.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description:
 * @author pthahnil
 * @date 2020/4/8 15:08
 */
public class CommonUtils {

	/**
	 * 掩码，头尾各保留一段
	 * @param str
	 * @param head
	 * @param tail
	 * @return
	 */
	public static String maskInfo(String str, int head, int tail) {
		return maskInfo(str, head, tail, "*");
	}


	public static String maskInfo(String str, int head, int tail, String seprator) {
		if(StringUtils.isBlank(str)){
			return str;
		}
		if(str.length() < head + tail) {
			return str;
		}

		int startCt = str.length() - head - tail;
		String stars = IntStream.range(0, startCt).boxed().map(i -> seprator).collect(Collectors.joining());

		return str.substring(0, head) + stars + str.substring(str.length() - tail, str.length());
	}

	/**
	 * list按批次拆分
	 * @param list
	 * @param batchSize 子list大小
	 * @param <T>
	 * @return
	 */
	public static <T> List<List<T>> splitList(List<T> list, int batchSize) {

		List<List<T>> retList = new ArrayList<>();
		if(CollectionUtils.isEmpty(list) || batchSize <= 0) {
			return retList;
		}
		int size = list.size();
		int listNum = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;

		for (int i = 0; i < listNum; i++) {
			int startIndex = i * batchSize;
			int endIndex = (startIndex + batchSize > size) ? size : startIndex + batchSize;
			List<T> subList = list.subList(startIndex, endIndex);
			retList.add(subList);
		}
		return retList;
	}

	/**
	 * 转驼峰命名
	 * @param fields
	 * @param seperator
	 * @return
	 */
	public static List<String> toCam(List<String> fields, String seperator) {

		if(CollectionUtils.isEmpty(fields)) {
			return null;
		}

		if(StringUtils.isBlank(seperator)) {
			return fields;
		}
		return fields.stream().map(field -> {
			String[] fieldSegs = field.split(seperator);
			StringBuffer buffer = new StringBuffer(fieldSegs[0]);
			if(fieldSegs.length > 1) {
				for (int i = 1; i < fieldSegs.length; i++) {
					String fieldSeg = fieldSegs[i];
					buffer.append(fieldSeg.substring(0, 1).toUpperCase()).append(fieldSeg.substring(1));
				}
			}
			return buffer.toString();
		}).collect(Collectors.toList());
	}

	/**
	 * 当前文件夹目录
	 * @return
	 * @throws Exception
	 */
	public static File getFilePath() throws Exception {
		String path = CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		return file.getParentFile();
	}
}
