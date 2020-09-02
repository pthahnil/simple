package com.simple.xrcraft.rule.model.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 10:55
 */
@Data
public class SimpleRuleUnitResult implements Serializable {

	/** 对比的结果 */
	private boolean result = false;

	/** 执行结果后的值 */
	private String resultValue;

}
