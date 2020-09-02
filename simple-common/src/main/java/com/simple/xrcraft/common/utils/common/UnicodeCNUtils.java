package com.simple.xrcraft.common.utils.common;

/**
 * Created by lixiaorong on 2017/10/31.
 */
public class UnicodeCNUtils {

	/**
	 *
	 * @param unicode
	 * @return
	 */
	public static String unicode2Cn(String unicode) {
		String[] strs = unicode.split("\\\\u");
		String returnStr = "";
		for (int i = 1; i < strs.length; i++) {
			returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
		}
		return returnStr;
	}

	/**
	 * 中文 转 unicode
	 * @param cn
	 * @return
	 */
	public static String cn2Unicode(String cn) {
		char[] chars = cn.toCharArray();
		String returnStr = "";
		for (int i = 0; i < chars.length; i++) {
			returnStr += "\\u" + Integer.toString(chars[i], 16);
		}
		return returnStr;
	}

}
