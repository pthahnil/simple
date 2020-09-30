package com.simple.xrcraft.rule.analyzer;

import com.simple.xrcraft.base.intf.TriFunction;
import com.simple.xrcraft.rule.constants.ResultValidateType;
import com.simple.xrcraft.rule.factory.OperationFactory;
import com.simple.xrcraft.rule.factory.OperatorFactory;
import com.simple.xrcraft.rule.model.FactHolder;
import com.simple.xrcraft.rule.model.SimpleRule;
import com.simple.xrcraft.rule.model.SimpleRuleUnit;
import com.simple.xrcraft.rule.model.result.SimpleRuleUnitResult;
import com.simple.xrcraft.rule.operate.BaseOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 10:16
 */
@Slf4j
public class RuleAnalyser {

	/**
	 * 分析
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	public static void analyse (SimpleRuleUnit unit, FactHolder factHolder) throws Exception {
		if(unit.getIsLeaf()){
			leafAnalyse(unit, factHolder);
		} else {
			branchAnalyse(unit, factHolder);
		}
	}

	/**
	 * 底层分析
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	private static void leafAnalyse(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {
		List<SimpleRule> rules = unit.getRules();
		boolean checkResult = false;

		if(CollectionUtils.isEmpty(rules)) {
			return;
		}

		String validateType = unit.getResultValidateType();
		for (SimpleRule rule : rules) {
			String factKey = rule.getFact();
			String factValue = factHolder.get(factKey);
			String targetValue = rule.getTarget();

			String dataType = rule.getDataType();
			String ope = rule.getOperation();

			BaseOperator operator = OperatorFactory.instance().getSvc(dataType);
			TriFunction<BaseOperator, String, String, Boolean> operation =
					OperationFactory.instance().getSvc(ope);
			if(null == operator || null == operation){
				if (ResultValidateType.AND.getType().equals(validateType)) {
					rule.getExecuteResult().setResult(false);
					unit.getRuleUnitResult().setResult(false);
					return;
				} else if (ResultValidateType.OR.getType().equals(validateType)) {
					continue;
				} else {
					throw new Exception("not supported rule validate type");
				}
			}
			try {
				boolean executeResult = operation.apply(operator, factValue, targetValue);
				rule.getExecuteResult().setResult(executeResult);
				if (executeResult) {
					String estimatedValue = rule.getEstimatedValue();
					if(StringUtils.isBlank(estimatedValue)){
						estimatedValue = unit.getEstimatedValue();
					}
					rule.getExecuteResult().setResultValue(estimatedValue);
				}
				if (ResultValidateType.AND.getType().equals(validateType)) {
					checkResult = executeResult;
					if (!checkResult) {
						break;
					}
				} else if (ResultValidateType.OR.getType().equals(validateType)) {
					checkResult = checkResult || executeResult;
				} else {
					throw new Exception("not supported rule validate type");
				}
			} catch (Exception e) {
				log.error("{} {} {} 执行错误" , factValue, ope, targetValue, e);
				if (ResultValidateType.AND.getType().equals(validateType)) {
					throw e;
				}
			}
		}
		unit.getRuleUnitResult().setResult(checkResult);
	}

	/**
	 * 上层分析
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	private static void branchAnalyse(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {
		List<SimpleRuleUnit> units = unit.getRuleUnits();
		boolean checkResult = false;
		if(CollectionUtils.isEmpty(units)){
			return;
		}

		String validateType = unit.getResultValidateType();
		for (SimpleRuleUnit simpleRuleUnit : units) {

			if(simpleRuleUnit.getIsLeaf()){
				leafAnalyse(simpleRuleUnit, factHolder);
			} else {
				branchAnalyse(simpleRuleUnit, factHolder);
			}

			SimpleRuleUnitResult resultHolder = simpleRuleUnit.getRuleUnitResult();
			Boolean result = resultHolder.isResult();
			if (ResultValidateType.AND.getType().equals(validateType)) {
				checkResult = result;
				if (!checkResult) {
					break;
				}
			} else if (ResultValidateType.OR.getType().equals(validateType)) {
				checkResult = checkResult || result;
			} else {
				throw new Exception("not supported rule validate type");
			}
		}
		unit.getRuleUnitResult().setResult(checkResult);
	}
}
