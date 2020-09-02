package com.simple.xrcraft.rule.analyzer;

import com.simple.xrcraft.base.intf.TriFunction;
import com.simple.xrcraft.rule.constants.ResultAccumulateType;
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

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 11:30
 */
@Slf4j
public class SimpleRuleUnitAnalyzer {

	private static final Pattern numberPattern = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");

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
		Boolean isBaseRule = unit.getIsBaseRule();
		if(isBaseRule){
			baseRule(unit, factHolder);
		} else {
			ruleUnit(unit, factHolder);
		}

	}

	/**
	 * 上层
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	private static void ruleUnit(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {
		List<SimpleRuleUnit> units = unit.getRuleUnits();
		boolean checkResult = false;
		if(CollectionUtils.isNotEmpty(units)){

			String validateType = unit.getResultValidateType();
			for (SimpleRuleUnit simpleRuleUnit : units) {
				executeRules(simpleRuleUnit, factHolder);
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

	/**
	 * 基层
	 * @param unit
	 * @param factHolder
	 * @throws Exception
	 */
	private static void baseRule(SimpleRuleUnit unit, FactHolder factHolder) throws Exception {
		List<SimpleRule> rules = unit.getRules();
		boolean checkResult = false;

		if(CollectionUtils.isNotEmpty(rules)){
			String validateType = unit.getResultValidateType();
			for (SimpleRule rule : rules) {
				String factKey = rule.getFact();
				String factValue = factHolder.get(factKey);
				String targetValue = rule.getTarget();

				String dataType = rule.getDateType();
				String ope = rule.getOperation();

				BaseOperator operator = OperatorFactory.instance().getSvc(dataType);
				if (null == operator) {
					continue;
				}
				try {
					TriFunction<BaseOperator, String, String, Boolean> operation =
							OperationFactory.instance().getSvc(ope);
					boolean result = operation.apply(operator, factValue, targetValue);
					rule.getExecuteResult().setResult(result);
					if (result) {
						rule.getExecuteResult().setResultValue(rule.getEstimatedValue());
					}

					if (ResultValidateType.AND.getType().equals(validateType)) {
						checkResult = result;
						//todo 是否所有条件都执行
						if (!checkResult) {
							break;
						}
					} else if (ResultValidateType.OR.getType().equals(validateType)) {
						checkResult = checkResult || result;
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
		}
		unit.getRuleUnitResult().setResult(checkResult);
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

		//是否最基层
		Boolean isBaseRule = unit.getIsBaseRule();
		if(isBaseRule){
			baseRuleResults(unit);
		} else {
			ruleUnitResults(unit);
		}
	}

	/**
	 * 单条规则的值处理
	 * @param unit
	 * @throws Exception
	 */
	private static void baseRuleResults(SimpleRuleUnit unit) throws Exception {

		String resultAccululateType = unit.getResultAccululateType();

		List<SimpleRule> rules = unit.getRules();
		if(ResultAccumulateType.REJECT.getType().equals(resultAccululateType)) {

			SimpleRule ruleNeeded = rules.stream()
					.filter(rule -> rule.getExecuteResult().isResult())
					.sorted(Comparator.comparing(SimpleRule::getPriority))
					.findFirst().orElse(null);
			if(null != ruleNeeded) {
				unit.getRuleUnitResult().setResultValue(ruleNeeded.getExecuteResult().getResultValue());
			}
		} else if(ResultAccumulateType.ACCUMULATE.getType().equals(resultAccululateType)) {
			//结果累计，只能用于number
			boolean notDecimalResultFound = rules.stream().filter(rule -> rule.getExecuteResult().isResult())
					.filter(rule -> !numberPattern.matcher(rule.getExecuteResult().getResultValue()).find())
					.findAny().isPresent();
			if(notDecimalResultFound) {
				String info = "only decimals can be accumulated";
				log.info("{}, sorry", info);
				throw new Exception(info);
			}
			Double doubleValue = rules.stream().filter(rule -> rule.getExecuteResult().isResult())
					.map(SimpleRule::getExecuteResult)
					.map(res -> Double.parseDouble(res.getResultValue())).reduce(Double::sum).orElse(0d);
			unit.getRuleUnitResult().setResultValue(doubleValue.toString());
		}
	}

	/**
	 * 规则组的值处理
	 * @param unit
	 */
	private static void ruleUnitResults(SimpleRuleUnit unit){
		String resultAccululateType = unit.getResultAccululateType();

		List<SimpleRuleUnit> units = unit.getRuleUnits();
		if(ResultAccumulateType.REJECT.getType().equals(resultAccululateType)) {
			SimpleRuleUnit unitNeeded = units.stream()
					.filter(unitN -> unitN.getRuleUnitResult().isResult())
					.sorted(Comparator.comparing(SimpleRuleUnit::getPriority))
					.findFirst().orElse(null);

			if(null != unitNeeded) {
				unit.getRuleUnitResult().setResultValue(unitNeeded.getRuleUnitResult().getResultValue());
			}
		} else if(ResultAccumulateType.ACCUMULATE.getType().equals(resultAccululateType)) {
			Double val = units.stream()
					.filter(unitN -> unitN.getRuleUnitResult().isResult())
					.filter(unitN -> numberPattern.matcher(unitN.getRuleUnitResult().getResultValue()).find())
					.map(unitN -> Double.parseDouble(unitN.getRuleUnitResult().getResultValue()))
					.reduce(0D, Double::sum);

			unit.getRuleUnitResult().setResultValue(val.toString());
		}
	}

}
