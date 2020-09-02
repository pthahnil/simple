package com.simple.xrcraft.common.calculate.service.factory;

import com.simple.xrcraft.common.calculate.constants.PeriodType;
import com.simple.xrcraft.common.calculate.service.PeriodService;
import com.simple.xrcraft.common.calculate.service.period.DayRate;
import com.simple.xrcraft.common.calculate.service.period.MonthRate;
import com.simple.xrcraft.common.calculate.service.period.MulDayRate;
import com.simple.xrcraft.common.calculate.service.period.YearRate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/10/26.
 */
public class PeriodRateFactory {

	private static Map<String, PeriodService> svcPool = new HashMap<>();
	static {
		try {
			svcPool.put(PeriodType.DAY.getType(), DayRate.class.newInstance());
			svcPool.put(PeriodType.MULDAY.getType(), MulDayRate.class.newInstance());
			svcPool.put(PeriodType.MONTH.getType(), MonthRate.class.newInstance());
			svcPool.put(PeriodType.YEAR.getType(), YearRate.class.newInstance());
		} catch (Exception e) {}
	}
	private static PeriodRateFactory factory = new PeriodRateFactory();

	public PeriodService getSvc(String periodType) {
		return svcPool.get(periodType);
	}

	public static PeriodRateFactory instance() {
		return factory;
	}
}
