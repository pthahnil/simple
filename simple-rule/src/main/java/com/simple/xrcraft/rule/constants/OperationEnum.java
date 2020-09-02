package com.simple.xrcraft.rule.constants;

import lombok.Getter;

/**
 * Created by pthahnil on 2019/9/26.
 */
@Getter
public enum OperationEnum {
	GT(">", "大于"),
	GE(">=", "大于等于"),
	LT("<", "小于"),
	LE("<=", "小于等于"),
	EQ("==", "等于"),
	NE("!=", "不等于"),
	IN("in", "包含"),
	;
	private String operation;
	private String desc;

	OperationEnum(String ope, String desc) {
		this.operation = ope;
		this.desc = desc;
	}
}