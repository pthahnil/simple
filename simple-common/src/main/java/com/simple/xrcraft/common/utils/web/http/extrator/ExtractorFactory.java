package com.simple.xrcraft.common.utils.web.http.extrator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/5/21.
 */
public class ExtractorFactory {

	private static Extrator DEFAULT = new StringExtrator();

	private static Map<Class, Extrator> extrators = new HashMap<Class, Extrator>(){{
		put(String.class, new StringExtrator());
		put(byte[].class, new ByteExtrator());
	}};

	public static <T> Extrator<T> getExtrator(Class<T> clazz) {
		Extrator<T> extrator = extrators.get(clazz);
		return null == extrator ? DEFAULT : extrator;
	}

}
