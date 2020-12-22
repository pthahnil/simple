package com.simple.xrcraft.rule.operate.operator;

import com.simple.xrcraft.rule.constants.CommonRuleConstants;
import com.simple.xrcraft.rule.operate.BaseOperator;
import com.simple.xrcraft.rule.operate.BaseTimeOperator;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 9:06
 */
public class DateOperator extends BaseTimeOperator implements BaseOperator {

	@Override
	public Boolean gt(String factValue, String targetValue) throws Exception {
		LocalDate factDate = getDate(factValue);
		LocalDate targetDate = getDate(targetValue);
		return factDate.isAfter(targetDate);
	}

	@Override
	public Boolean ge(String factValue, String targetValue) throws Exception {
		LocalDate factDate = getDate(factValue);
		LocalDate targetDate = getDate(targetValue);
		return !factDate.isBefore(targetDate);
	}

	@Override
	public Boolean lt(String factValue, String targetValue) throws Exception {
		LocalDate factDate = getDate(factValue);
		LocalDate targetDate = getDate(targetValue);
		return factDate.isBefore(targetDate);
	}

	@Override
	public Boolean le(String factValue, String targetValue) throws Exception {
		LocalDate factDate = getDate(factValue);
		LocalDate targetDate = getDate(targetValue);
		return !factDate.isAfter(targetDate);
	}

	@Override
	public Boolean eq(String factValue, String targetValue) throws Exception {
		LocalDate factDate = getDate(factValue);
		LocalDate targetDate = getDate(targetValue);
		return factDate.isEqual(targetDate);
	}

	@Override
	public Boolean ne(String factValue, String targetValue) throws Exception {
		return !eq(factValue, targetValue);
	}

	@Override
	public Boolean in(String factValue, String targetValue) throws Exception {
		if(StringUtils.isBlank(factValue) || StringUtils.isBlank(targetValue)){
			throw new Exception("target must be a collection");
		}
		LocalDate factDate = getDate(factValue);

		return Arrays.stream(targetValue.split(CommonRuleConstants.RULE_COLL_SEPARATOR))
				.map(tgt -> {
					try {return getDate(tgt);} catch (Exception e) {}
					return null;
				})
				.filter(tgt -> null != tgt && factDate.isEqual((LocalDate)tgt))
				.findAny().isPresent();
	}

}
