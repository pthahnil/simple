package com.simple.xrcraft.common.utils.web.http.model.entity;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/6 10:11
 */
public class FormEntityBuilder implements EntityBuilder<Map<String, Object>> {

	@Override
	public HttpEntity build(Map<String, Object> params, String charSet) {

		HttpEntity entity = null;
		if(MapUtils.isNotEmpty(params)){
			List<NameValuePair> pairList = new ArrayList<>();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			entity = new UrlEncodedFormEntity(pairList, Charset.forName(charSet));
		}
		return entity;
	}
}
