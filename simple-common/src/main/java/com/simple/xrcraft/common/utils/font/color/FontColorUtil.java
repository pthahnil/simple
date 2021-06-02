package com.simple.xrcraft.common.utils.font.color;

import com.simple.xrcraft.common.constants.FontColor;

/**
 * @description:
 * @author pthahnil
 * @date 2021/6/2 11:34
 */
public class FontColorUtil {

	/**
	 * 红
	 * @param msg
	 * @return
	 */
	public static String red(String msg) {
		return colorFormat(FontColor.RED, msg);
	}

	/**
	 * 绿
	 * @param msg
	 * @return
	 */
	public static String green(String msg) {
		return colorFormat(FontColor.GREEN, msg);
	}

	/**
	 * 黄
	 * @param msg
	 * @return
	 */
	public static String yellow(String msg) {
		return colorFormat(FontColor.YELLOW, msg);
	}

	/**
	 * 蓝
	 * @param msg
	 * @return
	 */
	public static String blue(String msg) {
		return colorFormat(FontColor.BLUE, msg);
	}

	/**
	 * 紫
	 * @param msg
	 * @return
	 */
	public static String purple(String msg) {
		return colorFormat(FontColor.PURPLE, msg);
	}

	/**
	 *
	 * @param msg
	 * @return
	 */
	public static String cyan(String msg) {
		return colorFormat(FontColor.CYAN, msg);
	}

	/**
	 * 格式换
	 * @param color
	 * @param msg
	 * @return
	 */
	public static String colorFormat(String color, String msg) {
		if(null == msg || msg.trim().isEmpty()){
			return null;
		}
		return color + msg + FontColor.RESET;
	}
}
