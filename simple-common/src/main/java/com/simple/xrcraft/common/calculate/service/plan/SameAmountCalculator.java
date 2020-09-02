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
 * 等额本息
 * Created by pthahnil on 2019/10/26.
 */
public class SameAmountCalculator extends AbstractRepayPlanService {

	/**
	 * 计算逻辑
	 * @param loanInfo
	 * @param periodRate
	 * @return
	 */
	public RepayPlan mainCalculate(BasicLoanInfo loanInfo, Double periodRate, PeriodGap gap){
		RepayPlan plan = new RepayPlan(loanInfo);

		BigDecimal amt = loanInfo.getPrincipal();
		int periodCount = loanInfo.getPeriodCount();

		//每月还款金额  Aβ(1+β)^m /[(1+β)^m - 1]
		BigDecimal pow = new BigDecimal(Math.pow (1 + periodRate, periodCount));
		BigDecimal perMonth = amt.multiply(new BigDecimal(periodRate)).multiply(pow).divide(pow.subtract(BigDecimal.ONE), 4, RoundingMode.HALF_UP).setScale(4, BigDecimal.ROUND_HALF_UP);

		//分母
		BigDecimal devider = pow.subtract(BigDecimal.ONE);

		List<RepayPeriod> periods = new ArrayList<>();
		BigDecimal totalInterest = BigDecimal.ZERO;

		Date startTime = loanInfo.getStartTime();
		for (int i = 0; i < periodCount; i++) {
			//每月本金  B=a*i(1+i)^(n-1)/[(1+i)^N-1]
			Double up = Math.pow(1 + periodRate, i);

			//每月本金
			BigDecimal amtPerMaonth = amt.multiply(new BigDecimal(periodRate)).multiply(new BigDecimal(up)).divide(devider, 4, RoundingMode.HALF_UP);
			//每月利息
			BigDecimal interest = perMonth.subtract(amtPerMaonth);
			totalInterest = totalInterest.add(interest);

			RepayPeriod period = new RepayPeriod();
			period.setAmount(perMonth.setScale(2, BigDecimal.ROUND_HALF_UP));
			period.setPrincipal(amtPerMaonth.setScale(2, BigDecimal.ROUND_HALF_UP));
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
