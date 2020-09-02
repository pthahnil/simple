package com.simple.xrcraft.common.calculate.service;

import com.simple.xrcraft.common.calculate.model.BasicLoanInfo;
import com.simple.xrcraft.common.calculate.model.RepayPlan;

/**
 * 还款计划
 * Created by pthahnil on 2019/10/26.
 */
public interface RepayPlanService {

	/**
	 * 计算
	 * @param loanInfo
	 * @return
	 */
	default RepayPlan calculate(BasicLoanInfo loanInfo) {
		throw new RuntimeException("支持的计息方式");
	}

}
