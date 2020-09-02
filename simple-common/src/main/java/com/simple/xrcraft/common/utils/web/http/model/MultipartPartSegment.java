package com.simple.xrcraft.common.utils.web.http.model;

import com.simple.xrcraft.common.constants.HttpConstants;
import lombok.Data;

/**
 * @description:
 * @author pthahnil
 * @date 2020/3/28 10:00
 */
@Data
public class MultipartPartSegment {

	private String key;

	private Object value;

	private String charSet = HttpConstants.charset_ISO;//默认编码

	public MultipartPartSegment() { }

	public MultipartPartSegment(String key, Object value) {
		this.key = key;
		this.value = value;
	}

}
