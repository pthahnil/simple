package com.simple.xrcraft.common.utils.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/8/4 14:22
 */
@Slf4j
public class CsvUtils {

	/**
	 * 读取文件内容
	 * @param stream
	 * @param clazz
	 * @param encoding
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> read(InputStream stream, Class<T> clazz, String encoding, String seperator) throws Exception {
		if(null == stream){
			throw new Exception("csv文件为空");
		}
		List<String> allInfos = IOUtils.readLines(stream, encoding);
		if(CollectionUtils.isEmpty(allInfos) || allInfos.size() <= 1){
			log.info("csv文件内容为空");
			return null;
		}
		//列标题和字段段颖关系
		Map<String, String> columnFieldMap = columnFieldMap(clazz);

		String headerLine = allInfos.get(0);

		//列标题和列索引对应关系
		Map<Integer, String> columnIndexMap = columnIndexMap(headerLine, seperator);

		List<String> infos = allInfos.subList(1, allInfos.size());
		JSONArray array = new JSONArray();


		for (String info : infos) {
			if(StringUtils.isBlank(info)){
				continue;
			}

			String[] infoSegs = info.split(seperator);
			JSONObject json = new JSONObject();
			for (int i = 0; i < infoSegs.length; i++) {
				String columnName = columnIndexMap.get(i);
				String fieldName = columnFieldMap.get(columnName);
				String value = infoSegs[i].trim();

				json.put(fieldName, value);
			}
			array.add(json);
		}

		return array.toJavaList(clazz);
	}


	/**
	 * 列名 -> 脚表 对应关系
	 * @param headerLine
	 * @return
	 * @throws Exception
	 */
	private static Map<Integer, String> columnIndexMap(String headerLine, String seperator) throws Exception {

		if(StringUtils.isBlank(headerLine)){
			throw new Exception("表头为空");
		}

		String[] headerSegs = headerLine.split(seperator);
		Map<Integer, String> headerMap = new HashMap<>();
		for (int i = 0; i < headerSegs.length; i++) {
			headerMap.put(i, headerSegs[i].trim());
		}

		return headerMap;
	}

	/**
	 * 列名 -> 字段名 对应关系
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> columnFieldMap(Class clazz) throws Exception {
		Field[] fileds = clazz.getDeclaredFields();

		Map<String, String> columnFieldMap = new HashMap<>();
		for (Field filed : fileds) {

			String fieldName = filed.getName();
			ExcelProperty propAnno = filed.getAnnotation(ExcelProperty.class);
			if(null != propAnno && (null != propAnno.value() && propAnno.value().length > 0)){
				String columnName = propAnno.value()[0].trim();
				if(columnFieldMap.containsKey(columnName)){
					throw new Exception("列名："+ columnName +"重复");
				}
				columnFieldMap.put(columnName, fieldName);
			}
		}
		return columnFieldMap;
	}

}
