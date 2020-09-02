package com.simple.xrcraft.common.calculate.constants;

/**
 * Created by pthahnil on 2019/10/26.
 */
public enum CalculateType {

	SAME_AMOUNT("SMAMT","等额本息"),
	SAME_PRINCIAPL("SMPRI","等额本金"),
	;
	private String type;

	private String desc;

	CalculateType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
}
