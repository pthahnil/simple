package com.simple.xrcraft.common.calculate.service.factory;


import com.simple.xrcraft.common.calculate.constants.CalculateType;
import com.simple.xrcraft.common.calculate.service.RepayPlanService;
import com.simple.xrcraft.common.calculate.service.plan.SameAmountCalculator;
import com.simple.xrcraft.common.calculate.service.plan.SamePrincipalCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/10/26.
 */
public class PlanCalculateFactory {

	private static Map<String, RepayPlanService> svcPool = new HashMap<>();
	static {
		try {
			svcPool.put(CalculateType.SAME_AMOUNT.getType(), SameAmountCalculator.class.newInstance());
			svcPool.put(CalculateType.SAME_PRINCIAPL.getType(), SamePrincipalCalculator.class.newInstance());
		} catch (Exception e) {}
	}
	private static PlanCalculateFactory factory = new PlanCalculateFactory();

	public RepayPlanService getSvc(String payType) {
		return svcPool.get(payType);
	}

	public static PlanCalculateFactory instance() {
		return factory;
	}
}
