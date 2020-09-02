package com.simple.xrcraft.rule.model.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/14 10:55
 */
@Data
public class SimpleRuleResult implements Serializable {

	/** 对比的结果 */
	private boolean result = false;

	/** 是否执行 */
	private boolean executed = false;

	/** 执行结果后的值 */
	private String resultValue;

	/** 执行结果码，一般用于校验参数 */
	private String resultCode;

	/** 执行结果信息，一般用于校验参数 */
	private String resultMsg;

	public void setResult(boolean result) {
		this.result = result;
		this.executed = true;
	}
}
