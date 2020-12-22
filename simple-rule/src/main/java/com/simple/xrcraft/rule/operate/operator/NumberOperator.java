package com.simple.xrcraft.rule.operate.operator;

import com.simple.xrcraft.rule.operate.BaseOperator;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 9:00
 */
public class NumberOperator implements BaseOperator {

	@Override
	public Boolean gt(String factValue, String targetValue) throws Exception {
		if(StringUtils.isBlank(factValue) || StringUtils.isBlank(targetValue)){
			return false;
		}
		BigDecimal fact = new BigDecimal(factValue);
		BigDecimal target = new BigDecimal(targetValue);
		return fact.compareTo(target) > 0;

		//return compareTo(factValue, targetValue, ">");
	}

	@Override
	public Boolean ge(String factValue, String targetValue) throws Exception {
		if(StringUtils.isBlank(factValue) || StringUtils.isBlank(targetValue)){
			return false;
		}
		BigDecimal fact = new BigDecimal(factValue);
		BigDecimal target = new BigDecimal(targetValue);
		return fact.compareTo(target) >= 0;
//		return compareTo(factValue, targetValue, ">=");
	}

	@Override
	public Boolean lt(String factValue, String targetValue) throws Exception {
		if(StringUtils.isBlank(factValue) || StringUtils.isBlank(targetValue)){
			return false;
		}
		BigDecimal fact = new BigDecimal(factValue);
		BigDecimal target = new BigDecimal(targetValue);
		return fact.compareTo(target) < 0;
//		return compareTo(factValue, targetValue, "<");
	}

	@Override
	public Boolean le(String factValue, String targetValue) throws Exception {
		if(StringUtils.isBlank(factValue) || StringUtils.isBlank(targetValue)){
			return false;
		}
		BigDecimal fact = new BigDecimal(factValue);
		BigDecimal target = new BigDecimal(targetValue);
		return fact.compareTo(target) <= 0;
//		return compareTo(factValue, targetValue, "<=");
	}

	/**
	 * 比较
	 * @param factValue
	 * @param targetValue
	 * @param op
	 * @return
	 * @throws Exception
	 */
	/*private boolean compareTo(Object factValue, Object targetValue, String op) throws Exception {

		Map<String,Object> map = new HashMap<>();
		map.put("fact", factValue);
		map.put("target", targetValue);

		String expression = "fact "+ op +" target";

		OgnlContext context = new OgnlContext(null,null,new DefaultMemberAccess(true));
		Object exp = Ognl.parseExpression(expression);
		return (Boolean) Ognl.getValue(exp, context, map);
	}*/

}
