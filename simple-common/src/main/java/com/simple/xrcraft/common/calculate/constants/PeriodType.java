package com.simple.xrcraft.common.calculate.constants;

import lombok.Getter;

/**
 * Created by pthahnil on 2019/10/28.
 */
@Getter
public enum PeriodType {
	MULDAY("MD", "数日一期"),
	DAY("D", "每日一期"),
	MONTH("M", "每月一期"),
	YEAR("Y", "每年一期"),
	;
	private String type;
	private String desc;

	PeriodType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}


}
