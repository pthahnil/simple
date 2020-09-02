package com.simple.xrcraft.rule.model;

import com.simple.xrcraft.rule.constants.DataTypeEnum;
import com.simple.xrcraft.rule.model.result.SimpleRuleResult;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/28 17:31
 */
@Data
public class SimpleRule implements Serializable {

	/** 规则对比的key */
	private String fact;

	/** 计算，比如 >, >= */
	private String operation;

	/** 规则对比的目标值 */
	private String target;

	/** 优先级，若或者，多个条件通过，则取优先级最小的 */
	private Integer priority;

	/**
	 * 子规则的数据格式
	 * @see DataTypeEnum
	 */
	private String dateType;

	/** 通过后的预估值 */
	private String estimatedValue;

	/** 执行结果 */
	private SimpleRuleResult executeResult = new SimpleRuleResult();
}
