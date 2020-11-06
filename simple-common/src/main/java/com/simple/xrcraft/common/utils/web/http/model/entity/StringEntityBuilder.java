package com.simple.xrcraft.common.utils.web.http.model.entity;

import com.simple.xrcraft.common.constants.HttpConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
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
public class StringEntityBuilder implements EntityBuilder<String> {

	@Override
	public HttpEntity build(String body, String charSet) {
		StringEntity entity = null;
		if(StringUtils.isNotBlank(body)){
			entity = new StringEntity(body, charSet);
			entity.setContentEncoding(charSet);
		}
		return entity;
	}
}
