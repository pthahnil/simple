package com.simple.xrcraft.common.calculate.service.period;

import com.simple.xrcraft.common.calculate.model.BasicLoanInfo;
import com.simple.xrcraft.common.calculate.model.PeriodGap;
import com.simple.xrcraft.common.calculate.service.PeriodService;

import java.time.temporal.ChronoUnit;

/**
 * 年利率
 * Created by pthahnil on 2019/11/22.
 */
public class YearRate implements PeriodService {

	@Override
	public Double calculateRate(BasicLoanInfo loanInfo) {
		return loanInfo.getYearRate();
	}

	@Override
	public PeriodGap getGap(BasicLoanInfo loanInfo) {
		return new PeriodGap(1, ChronoUnit.YEARS);
	}
}
