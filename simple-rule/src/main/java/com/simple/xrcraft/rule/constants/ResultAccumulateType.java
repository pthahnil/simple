package com.simple.xrcraft.rule.constants;

import lombok.Getter;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 10:53
 */
@Getter
public enum ResultAccumulateType {
	REJECT("rej", "结果互斥，选其一（适用于任何情况）"),
	ACCUMULATE("acc", "结果积累，相加（适用于number, 适用于or）"),
	MAX_VAL("max", "取大值"),
	MIN_VAL("min", "取小值"),
	;
	private String type;
	private String desc;

	ResultAccumulateType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}
}
