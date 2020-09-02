package com.simple.xrcraft.common.calculate.service;

import com.simple.xrcraft.common.calculate.model.BasicLoanInfo;
import com.simple.xrcraft.common.calculate.model.PeriodGap;

/**
 * 周期相关计算
 * Created by pthahnil on 2019/11/22.
 */
public interface PeriodService {

	/**
	 * 计算周期利率
	 * @param loanInfo
	 * @return
	 */
	Double calculateRate(BasicLoanInfo loanInfo);

	/**
	 * 每期信息
	 * @param loanInfo
	 * @return
	 */
	PeriodGap getGap(BasicLoanInfo loanInfo);
}
