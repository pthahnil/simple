package com.simple.xrcraft.common.calculate.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pthahnil on 2019/10/26.
 */
@Data
public class RepayPeriod {

	private BigDecimal amount;

	private BigDecimal principal;

	private BigDecimal interest;

	private int period;

	private Date start;

	private Date end;
}
