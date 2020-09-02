package com.simple.xrcraft.common.models;

import lombok.Data;

/**
 * @description:
 * @author pthahnil
 * @date 2020/4/8 11:19
 */
@Data
public class IdCardAreaModel {

	private String areaCode;

	private String areaName;

	public IdCardAreaModel(String areaCode, String areaName) {
		this.areaCode = areaCode;
		this.areaName = areaName;
	}

	public IdCardAreaModel() {
	}
}
