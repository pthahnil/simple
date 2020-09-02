package com.simple.xrcraft.common.calculate.service.plan;

import com.simple.xrcraft.common.calculate.model.BasicLoanInfo;
import com.simple.xrcraft.common.calculate.model.PeriodGap;
import com.simple.xrcraft.common.calculate.model.RepayPeriod;
import com.simple.xrcraft.common.calculate.model.RepayPlan;
import com.simple.xrcraft.common.calculate.service.PeriodService;
import com.simple.xrcraft.common.calculate.service.RepayPlanService;
import com.simple.xrcraft.common.calculate.service.factory.PeriodRateFactory;
import com.simple.xrcraft.common.utils.common.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created by pthahnil on 2019/11/22.
 */
@Slf4j
public abstract class AbstractRepayPlanService implements RepayPlanService {

	@Override
	public RepayPlan calculate(BasicLoanInfo loanInfo) {
		PeriodService rateService  = PeriodRateFactory
				.instance().getSvc(loanInfo.getPeriodType().getType());
		if(null == rateService) {
			String info = "不支持的计息周期方式";
			log.info(info);
			throw new RuntimeException(info);
		}
		//每期利率
		Double periodRate = rateService.calculateRate(loanInfo);
		PeriodGap gap = rateService.getGap(loanInfo);
		return mainCalculate(loanInfo, periodRate, gap);
	}

	/**
	 * 主要计算
	 * @param loanInfo
	 * @param periodRate
	 * @param gap
	 * @return
	 */
	public abstract RepayPlan mainCalculate(BasicLoanInfo loanInfo, Double periodRate, PeriodGap gap);

	/**
	 * 起止时间计算
	 * @param period
	 * @param startTime
	 * @param gap
	 * @return
	 */
	protected RepayPeriod calDate(RepayPeriod period, Date startTime, PeriodGap gap){
		int i = period.getPeriod() - 1;
		Date start = DateUtils.getNext(startTime, i * gap.getGap(), gap.getUnit());
		if(i > 0) {
			start = DateUtils.getStartOfDate(start);
		}
		Date end = DateUtils.getNext(start, gap.getGap(), gap.getUnit());
		end = DateUtils.getEndOfDate(end);
		end = DateUtils.getNextNDay(end, -1);

		period.setStart(start);
		period.setEnd(end);

		return period;
	}
}
