package com.simple.xrcraft.rule.accumulator;

import com.simple.xrcraft.rule.model.SimpleRule;
import com.simple.xrcraft.rule.model.SimpleRuleUnit;

import java.util.Comparator;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 11:08
 */
public class RejectAccumulator extends AbstractAccumulator {

	@Override
	public void getVal(SimpleRuleUnit unit) throws Exception {
		SimpleRule ruleNeeded = unit.getRules().stream()
				.filter(rule -> rule.getExecuteResult().isResult())
				.sorted(Comparator.comparing(SimpleRule::getPriority))
				.findFirst().orElse(null);
		if(null != ruleNeeded) {
			unit.getRuleUnitResult().setResultValue(ruleNeeded.getExecuteResult().getResultValue());
		}
	}
}
