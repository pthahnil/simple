package com.simple.xrcraft.rule.accumulator;

import com.simple.xrcraft.rule.model.SimpleRuleUnit;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 11:00
 */
public abstract class AbstractAccumulator implements Accumulator {

	public void accumulate(SimpleRuleUnit unit) throws Exception {

		if(null == unit || !unit.getRuleUnitResult().isResult()){
			throw new Exception("cann't get result from unexecuted runeUnit ");
		}
		getVal(unit);
	}

	public abstract void getVal(SimpleRuleUnit unit) throws Exception;
}
