package com.simple.xrcraft.rule.constants;

import lombok.Getter;

/**
 * Created by pthahnil on 2019/9/27.
 */
@Getter
public enum DataTypeEnum {
	STRING("STR", "String"),
	NUMBER("NUM", "Number"),
	BOOLEAN("BOO", "Boolean"),
	DATE("DATE", "Date"),
	TIME("TIME", "Time"),
	DTTM("DTTM", "DateTime"),
	;
	String type;
	String typeName;

	DataTypeEnum(String type, String typeName) {
		this.type = type;
		this.typeName = typeName;
	}
}
