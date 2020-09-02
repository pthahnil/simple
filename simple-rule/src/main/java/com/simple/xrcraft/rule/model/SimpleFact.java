package com.simple.xrcraft.rule.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 单一输入条件
 * @author pthahnil
 * @date 2019/11/28 17:22
 */
@Data
public class SimpleFact implements Serializable {

	private String fact;

	private String value;

	public SimpleFact(String fact, String value) {
		this.fact = fact;
		this.value = value;
	}
}
