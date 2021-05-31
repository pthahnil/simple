package com.simple.xrcraft.common.useless;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/28 15:42
 */
public abstract class AbstractStringLongConverter implements StringLongConverter {

	public Long toLong(String target){
		if(StringUtils.isBlank(target)){
			throw new RuntimeException("input can't be empty");
		}
		int segSize = getSegSize();
		String[] ipSegs = target.split(getSeperator());
		if(ipSegs.length != segSize){
			throw new RuntimeException("input format error");
		}
		int segLength = getMaxBinaryLength();

		Long targetValue = 0L;
		for (int i = 0; i < segSize; i++) {
			Long num = Long.parseLong(ipSegs[i]);
			if(num > getUnitMaxNumber()){
				throw new RuntimeException("unit number:" + num + " larger then max number:" + getUnitMaxNumber());
			}
			int move = (segSize - 1 - i) * segLength;
			targetValue += (num << move);
		}
		return targetValue;
	}

	public String toStr(Long target){
		int segSize = getSegSize();
		int segLength = getMaxBinaryLength();

		Long[] ips = new Long[segSize];
		for (int i = 0; i < segSize; i++) {
			String keepStr = getKeepStr(segSize, i);
			if(StringUtils.isBlank(keepStr)){
				continue;
			}
			Long keep = Long.parseLong(keepStr, 16);
			Long keepLong = target & keep;
			int move = ((segSize -1) - i) * segLength;
			ips[i] = keepLong >> move;
		}
		return Arrays.stream(ips).map(String::valueOf).collect(Collectors.joining("."));
	}

	private int getMaxBinaryLength(){
		Long unitMax = getUnitMaxNumber();
		String maxStr = BigInteger.valueOf(unitMax).toString(2);
		return maxStr.length();
	}

	private int getMaxHexLength(){
		Long unitMax = getUnitMaxNumber();
		String maxStr = BigInteger.valueOf(unitMax).toString(16);
		return maxStr.length();
	}

	private String getDumpUnit(){
		return getCalUnit(false);
	}

	private String getKeepUnit(){
		return getCalUnit(true);
	}

	private String getCalUnit(Boolean keep){
		String s = keep ? "f" : "0";
		int length = getMaxHexLength();
		return IntStream.range(0, length).boxed().map(i -> s).collect(Collectors.joining());
	}

	private String getKeepStr(int segSize, int dumpSize){
		String keep = getKeepUnit();
		String dump = getDumpUnit();
		if(dumpSize > segSize){
			throw new RuntimeException("dump index out of bound");
		}
		int keepSize = segSize - dumpSize;

		String dumpStr = IntStream.range(0, dumpSize).boxed().map(i -> dump).collect(Collectors.joining());
		String keepStr = IntStream.range(0, keepSize).boxed().map(i -> keep).collect(Collectors.joining());
		return dumpStr + keepStr;
	}

	protected abstract Long getUnitMaxNumber();

	protected abstract int getSegSize();

	protected abstract String getSeperator();
}
