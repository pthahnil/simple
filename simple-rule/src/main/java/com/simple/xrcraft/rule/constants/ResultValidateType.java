package com.simple.xrcraft.rule.constants;

import lombok.Getter;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 10:53
 */
@Getter
public enum ResultValidateType {
	AND("and", "结果集and处理"),
	OR("or", "结果集or处理"),
	;
	private String type;
	private String desc;

	ResultValidateType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}
}
