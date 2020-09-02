package com.simple.xrcraft.rule.operate.operator;

import com.simple.xrcraft.rule.constants.CommonRuleConstants;
import com.simple.xrcraft.rule.operate.BaseOperator;
import com.simple.xrcraft.rule.operate.BaseTimeOperator;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 9:06
 */
public class TimeOperator extends BaseTimeOperator implements BaseOperator {

	@Override
	public Boolean gt(String factValue, String targetValue) throws Exception {
		LocalTime factTime = getTime(factValue);
		LocalTime targetTime = getTime(targetValue);
		return factTime.isAfter(targetTime);
	}

	@Override
	public Boolean ge(String factValue, String targetValue) throws Exception {
		LocalTime factTime = getTime(factValue);
		LocalTime targetTime = getTime(targetValue);
		return !factTime.isBefore(targetTime);
	}

	@Override
	public Boolean lt(String factValue, String targetValue) throws Exception {
		LocalTime factTime = getTime(factValue);
		LocalTime targetTime = getTime(targetValue);
		return factTime.isBefore(targetTime);
	}

	@Override
	public Boolean le(String factValue, String targetValue) throws Exception {
		LocalTime factTime = getTime(factValue);
		LocalTime targetTime = getTime(targetValue);
		return !factTime.isAfter(targetTime);
	}

	@Override
	public Boolean eq(String factValue, String targetValue) throws Exception {
		LocalTime factTime = getTime(factValue);
		LocalTime targetTime = getTime(targetValue);
		return factTime.equals(targetTime);
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

		LocalTime factTime = getTime(factValue);

		return Arrays.stream(targetValue.split(CommonRuleConstants.RULE_COLL_SEPARATOR))
				.map(tgt -> {
					try {return getTime(tgt);} catch (Exception e) {}
					return null;
				})
				.filter(tgt -> null != tgt && factTime.equals(tgt))
				.findAny().isPresent();
	}

}
