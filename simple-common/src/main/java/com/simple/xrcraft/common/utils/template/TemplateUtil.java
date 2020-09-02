package com.simple.xrcraft.common.utils.template;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 模板填充
 * @author pthahnil
 * @date 2020/1/10 9:16
 */
public class TemplateUtil {

	/**
	 * 模板占位符
	 */
	public static Pattern placeHolderPattern = Pattern.compile("\\${1}\\{{1}(\\w+)\\}{1}");

	/**
	 * 模板填充  占位符${}，根据自己需要定制，现在都是这种
	 * @param templateContent
	 * @param params
	 * @return
	 */
	public static String fillTemplate(String templateContent, Map<String, String> params) {
		if(MapUtils.isEmpty(params)) {
			//占位符全部空字符串替换
			params = new HashMap<>();
		}

		Matcher matcher = placeHolderPattern.matcher(templateContent);

		String filledTemplate = new String(templateContent);
		while (matcher.find()) {
			String placeHolder = matcher.group();
			String key = placeHolder.substring(2, placeHolder.length() - 1);
			String value = params.get(key);
			value = StringUtils.isNotBlank(value) ? value : "";
			filledTemplate = filledTemplate.replace(placeHolder, value);
		}

		return filledTemplate;
	}

	/**
	 * 查看有那些变量
	 * @param template
	 * @return
	 */
	public static Set<String> findValiables(String template) {
		if(StringUtils.isBlank(template)) {
			return null;
		}
		Matcher matcher = placeHolderPattern.matcher(template);

		Set<String> valiables = new HashSet<>();
		while (matcher.find()) {
			String placeHolder = matcher.group();
			String key = placeHolder.substring(2, placeHolder.length() - 1);
			valiables.add(key);
		}
		return valiables;
	}
}
