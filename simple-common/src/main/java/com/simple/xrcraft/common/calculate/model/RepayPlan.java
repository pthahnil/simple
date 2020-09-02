package com.simple.xrcraft.common.calculate.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by pthahnil on 2019/10/26.
 */
@Data
public class RepayPlan extends BasicLoanInfo {

	private BigDecimal totalInterest;

	private BigDecimal totalAmount;

	private Date endTime;

	private List<RepayPeriod> periods;

	public RepayPlan(BasicLoanInfo loanInfo) {
		this.setPrincipal(loanInfo.getPrincipal());
		this.setYearRate(loanInfo.getYearRate());
		this.setPeriodCount(loanInfo.getPeriodCount());
		this.setDaysPerPeriod(loanInfo.getDaysPerPeriod());
		this.setStartTime(loanInfo.getStartTime());
		this.setReapyType(loanInfo.getReapyType());
	}
}
