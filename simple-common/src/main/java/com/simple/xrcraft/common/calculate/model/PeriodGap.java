package com.simple.xrcraft.common.calculate.model;

import lombok.Data;

import java.time.temporal.ChronoUnit;

/**
 * Created by pthahnil on 2019/11/22.
 */
@Data
public class PeriodGap {

	/**时长*/
	private int gap;

	/**单位*/
	private ChronoUnit unit;

	public PeriodGap(int gap, ChronoUnit unit) {
		this.gap = gap;
		this.unit = unit;
	}
}
