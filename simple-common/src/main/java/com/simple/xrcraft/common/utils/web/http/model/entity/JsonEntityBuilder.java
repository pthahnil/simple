package com.simple.xrcraft.common.utils.web.http.model.entity;

import com.simple.xrcraft.common.utils.bean.JsonUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/6 10:11
 */
public class JsonEntityBuilder implements EntityBuilder<Map<String, Object>> {

	@Override
	public HttpEntity build(Map<String, Object> params, String charSet) {

		StringEntity entity = null;
		if(MapUtils.isNotEmpty(params)){
			String json = JsonUtils.toJson(params);
			entity = new StringEntity(json, charSet);

			entity.setContentEncoding(charSet);
			entity.setContentType("application/json");
		}
		return entity;
	}
}
