package com.simple.xrcraft.rule.analyzer;

import com.simple.xrcraft.rule.model.FactHolder;
import com.simple.xrcraft.rule.model.SimpleRuleUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 11:30
 */
@Slf4j
public class SimpleRuleUnitAnalyzer {

	/**
	 * 规则执行
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	public static void analyze(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {

		//规则执行
		executeRules(unit, factHolder);

		//结果结果收集
		ruleResults(unit);
	}

	/**
	 * 规则执行
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	private static void executeRules(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {

		//是否已经执行
		if(unit.getRuleUnitResult().isResult()){
			log.info("rule already executed, result:" + unit.getRuleUnitResult().toString());
			return;
		}
		//是否最基层
		RuleAnalyser.analyse(unit, factHolder);

	}

	/**
	 * 收集结果
	 * @param unit
	 * @throws Exception
	 */
	private static void ruleResults(SimpleRuleUnit unit) throws Exception {
		Boolean exeResult = unit.getRuleUnitResult().isResult();
		if(!exeResult) {
			log.info("unit execute result is :{}, sorry", exeResult);
			return;
		}

		if(StringUtils.isNotBlank(unit.getEstimatedValue())) {
			unit.getRuleUnitResult().setResultValue(unit.getEstimatedValue());
			return;
		}
		RuleResultCollector.accumulate(unit);
	}

}
