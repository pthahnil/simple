package com.simple.xrcraft.rule.accumulator;

import com.simple.xrcraft.rule.model.SimpleRuleUnit;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 10:57
 */
public interface Accumulator {

	void accumulate(SimpleRuleUnit unit) throws Exception;
}
