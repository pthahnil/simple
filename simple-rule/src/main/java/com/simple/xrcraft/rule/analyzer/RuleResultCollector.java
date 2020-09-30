package com.simple.xrcraft.rule.analyzer;

import com.simple.xrcraft.rule.factory.AccumulatorFactory;
import com.simple.xrcraft.rule.model.SimpleRuleUnit;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 10:33
 */
@Slf4j
public class RuleResultCollector {

	public static void accumulate(SimpleRuleUnit unit) throws Exception {
		if(unit.getIsLeaf()){
			leafAccumulate(unit);
		} else {
			brahcnAccumulate(unit);
		}
	}

	private static void leafAccumulate(SimpleRuleUnit unit) throws Exception {
		AccumulatorFactory.instance().getSvc(unit.getResultAccululateType()).accumulate(unit);
	}

	private static void brahcnAccumulate(SimpleRuleUnit unit) throws Exception {
		List<SimpleRuleUnit> ruleUnits = unit.getRuleUnits();
		for (SimpleRuleUnit ruleUnit : ruleUnits) {
			if(ruleUnit.getIsLeaf()){
				leafAccumulate(ruleUnit);
			} else {
				brahcnAccumulate(ruleUnit);
			}
		}
	}
}
