package com.simple.xrcraft.rule.model;

import com.simple.xrcraft.rule.constants.ResultAccumulateType ;
import com.simple.xrcraft.rule.constants.ResultValidateType  ;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 11:14
 */
@Data
public class RuleUnit implements Serializable {

	/**
	 * 结果获取方式
	 * @see ResultAccumulateType
	 */
	private String resultAccululateType;

	/**
	 * 结果判定方式
	 * @see ResultValidateType
	 */
	private String resultValidateType;

	/** 规则集通过后的结果 */
	private String result;

	/** 一个unit内所有的规则 */
	private List<SimpleRuleUnit> rules;

}
