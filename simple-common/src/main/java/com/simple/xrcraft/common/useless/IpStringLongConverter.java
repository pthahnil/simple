package com.simple.xrcraft.common.useless;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/28 15:42
 */
public class IpStringLongConverter extends AbstractStringLongConverter {

	@Override
	protected Long getUnitMaxNumber() {
		return 255L;
	}

	@Override
	protected int getSegSize() {
		return 4;
	}

	@Override
	protected String getSeperator() {
		return "\\.";
	}
}
