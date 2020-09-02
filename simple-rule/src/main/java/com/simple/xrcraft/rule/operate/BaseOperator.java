package com.simple.xrcraft.rule.operate;

import com.simple.xrcraft.rule.constants.CommonRuleConstants;

import java.util.Arrays;
import java.util.Collection;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/28 17:38
 */
public interface BaseOperator {

	String OPER_NOT_SUPPORTED = "error! operation not supported!";

	/**
	 * >
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean gt(String factValue, String targetValue) throws Exception {
		throw new Exception(OPER_NOT_SUPPORTED);
	}

	/**
	 * >=
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean ge(String factValue, String targetValue) throws Exception {
		throw new Exception(OPER_NOT_SUPPORTED);
	}

	/**
	 * <
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean lt(String factValue, String targetValue) throws Exception {
		throw new Exception(OPER_NOT_SUPPORTED);
	}

	/**
	 * <=
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean le(String factValue, String targetValue) throws Exception {
		throw new Exception(OPER_NOT_SUPPORTED);
	}

	/**
	 * ==
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean eq(String factValue, String targetValue) throws Exception {
		return factValue.equals(targetValue);
	}

	/**
	 * !=
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean ne(String factValue, String targetValue) throws Exception {
		return !eq(factValue, targetValue);
	}

	/**
	 * in
	 * @param factValue
	 * @param targetValue
	 * @return
	 * @throws Exception
	 */
	default Boolean in(String factValue, String targetValue) throws Exception {
		if(null == factValue || null == targetValue) {
			return false;
		}
		String[] segs = targetValue.split(CommonRuleConstants.RULE_COLL_SEPARATOR);
		return Arrays.asList(segs).contains(factValue);
	}

}
