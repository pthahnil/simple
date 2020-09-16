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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author pthahnil
 * @date 2020/8/4 14:22
 */
@Slf4j
public class CsvUtils {

	private static final Pattern pattern = Pattern.compile("\"{1}\\d+([,]\\d{3})*([.]\\d*)\"{1}");

	private static final Pattern numPattern = Pattern.compile(".\\d+");
	/**
	 * 读取文件内容
	 * @param stream
	 * @param clazz
	 * @param encoding
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> read(InputStream stream, Class<T> clazz, Integer headerIndex, String encoding) throws Exception {
		if(null == stream){
			throw new Exception("csv文件为空");
		}
		if(null == headerIndex){
			headerIndex = 0;
		}
		if(headerIndex < 0){
			throw new Exception("headerIndex 必须为正数");
		}
		List<String> allInfos = IOUtils.readLines(stream, encoding);
		if(CollectionUtils.isEmpty(allInfos) || allInfos.size() <= 1){
			log.info("csv文件内容为空");
			return null;
		}
		//列标题和字段段颖关系
		Map<String, String> columnFieldMap = columnFieldMap(clazz);


		String headerLine = allInfos.get(headerIndex);

		//列标题和列索引对应关系
		Map<Integer, String> columnIndexMap = columnIndexMap(headerLine);

		List<String> infos = allInfos.subList(headerIndex + 1, allInfos.size());
		JSONArray array = new JSONArray();


		for (String info : infos) {
			if(StringUtils.isBlank(info)){
				continue;
			}

			Matcher matcher = pattern.matcher(info);
			while (matcher.find()){
				String number = matcher.group();
				String cleanNumber = number.replace(",", "").replace("\"", "");
				info = info.replace(number, cleanNumber);
			}

			String[] infoSegs = info.split(",");
			JSONObject json = new JSONObject();
			for (int i = 0; i < infoSegs.length; i++) {
				String columnName = columnIndexMap.get(i);
				String fieldName = columnFieldMap.get(columnName);
				String value = infoSegs[i].trim();

				//.00， .01 这种
				Matcher numMatcher = numPattern.matcher(value);
				if(numMatcher.matches()){
					value = "0" + value;
				}
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
	private static Map<Integer, String> columnIndexMap(String headerLine) throws Exception {

		if(StringUtils.isBlank(headerLine)){
			throw new Exception("表头为空");
		}

		String[] headerSegs = headerLine.split(",");
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
