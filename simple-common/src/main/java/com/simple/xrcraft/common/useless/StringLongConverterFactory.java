package com.simple.xrcraft.common.useless;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/31 11:45
 */
public class StringLongConverterFactory {
	private static final Map<ConverterType, StringLongConverter> converterMap = new HashMap<>();

	static {
		converterMap.put(ConverterType.IP, new IpStringLongConverter());
	}

	public static Long toLong(String target, ConverterType type){
		StringLongConverter converter = converterMap.get(type);
		if(null == converter){
			throw new RuntimeException("converter type error");
		}
		return converter.toLong(target);
	}

	public static String toStr(Long target, ConverterType type){
		StringLongConverter converter = converterMap.get(type);
		if(null == converter){
			throw new RuntimeException("converter type error");
		}
		return converter.toStr(target);
	}

	@Getter
	public enum ConverterType{
		IP,
		;
	}
}
