package com.simple.xrcraft.common.calculate.service.plan;

import com.simple.xrcraft.common.calculate.model.BasicLoanInfo;
import com.simple.xrcraft.common.calculate.model.PeriodGap;
import com.simple.xrcraft.common.calculate.model.RepayPeriod;
import com.simple.xrcraft.common.calculate.model.RepayPlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 等额本金
 * Created by pthahnil on 2019/10/26.
 */
public class SamePrincipalCalculator extends AbstractRepayPlanService {

	public RepayPlan mainCalculate(BasicLoanInfo loanInfo, Double periodRate, PeriodGap gap) {
		RepayPlan plan = new RepayPlan(loanInfo);

		BigDecimal amt = loanInfo.getPrincipal();
		int periodCount = loanInfo.getPeriodCount();

		//每月本金
		BigDecimal principal = amt.divide(new BigDecimal(periodCount), 4, RoundingMode.HALF_UP);

		BigDecimal totalInterest = BigDecimal.ZERO;
		List<RepayPeriod> periods = new ArrayList<>();

		Date startTime = loanInfo.getStartTime();
		for (int i = 0; i < periodCount; i++) {
			BigDecimal alreadyPay = new BigDecimal(i - 0).multiply(principal);
			//利息
			BigDecimal interest = amt.add(alreadyPay.negate()).multiply(new BigDecimal(periodRate)).setScale(4, BigDecimal.ROUND_HALF_UP);
			//应还
			BigDecimal needPay = principal.add(interest).setScale(4, BigDecimal.ROUND_HALF_UP);

			totalInterest = totalInterest.add(interest);

			RepayPeriod period = new RepayPeriod();
			period.setAmount(needPay.setScale(2, BigDecimal.ROUND_HALF_UP));
			period.setPrincipal(principal.setScale(2, BigDecimal.ROUND_HALF_UP));
			period.setInterest(interest.setScale(2, BigDecimal.ROUND_HALF_UP));
			period.setPeriod(i + 1);

			//起止日期计算
			period = calDate(period, startTime, gap);

			periods.add(period);
		}
		BigDecimal totalAmount = amt.add(totalInterest);

		plan.setPeriods(periods);
		plan.setTotalInterest(totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
		plan.setTotalAmount(totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));

		return plan;
	}
}
