package com.simple.xrcraft.common.calculate.model;

import com.simple.xrcraft.common.calculate.constants.CalculateType;
import com.simple.xrcraft.common.calculate.constants.PeriodType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pthahnil on 2019/10/26.
 */
@Data
public class BasicLoanInfo {

	/**借款本金*/
	private BigDecimal principal;

	/**年利率*/
	private Double yearRate;

	/**借款周期*/
	private int periodCount;

	/**每期天数*/
	private int daysPerPeriod;

	/**借款日期*/
	private Date startTime;

	/**计息方式*/
	private CalculateType reapyType;

	/**周期方式*/
	private PeriodType periodType;
}
