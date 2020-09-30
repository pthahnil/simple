package com.simple.xrcraft.rule.accumulator;

import com.simple.xrcraft.rule.constants.DataTypeEnum;
import com.simple.xrcraft.rule.model.SimpleRule;
import com.simple.xrcraft.rule.model.SimpleRuleUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 11:08
 */
@Slf4j
public class SumAccumulator extends AbstractAccumulator {

	@Override
	public void getVal(SimpleRuleUnit unit) throws Exception {
		String dataType = unit.getDataType();
		boolean isNumber = DataTypeEnum.NUMBER.getType().equals(dataType);
		if(!isNumber){
			String info = "only decimals can be summed";
			log.info("{}, sorry", info);
			throw new Exception(info);
		}
		Double doubleValue = unit.getRules().stream().filter(rule -> rule.getExecuteResult().isResult())
				.map(SimpleRule::getExecuteResult)
				.map(res -> Double.parseDouble(res.getResultValue()))
				.reduce(Double::sum)
				.orElse(0d);
		unit.getRuleUnitResult().setResultValue(doubleValue.toString());
	}
}
