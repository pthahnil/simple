package com.simple.xrcraft.common.utils.web.http.model;

import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.web.http.model.entity.EntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.FormEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.JsonEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.MultiPartFormEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.StringEntityBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/6 9:11
 */
public class HttpExchangeModel {

	/**请求头*/
	private Map<String, String> headers;

	/**body的字段*/
	private Map<String, Object> params;

	/**直接一个string*/
	private String body;

	/**带文件的表单*/
	private List<MultipartPartSegment> segments;

	private String reqCharSet = HttpConstants.CHARSET_UTF8;

	private String respCharSet = HttpConstants.CHARSET_UTF8;

	private ExchangeType exchangeType;

	public HttpEntity getEntity(){
		ExchangeType type = this.exchangeType;

		HttpEntity entity = null;
		EntityBuilder builder = null;
		switch (type){
			case FORM:
				builder = new FormEntityBuilder();
				entity = builder.build(params, reqCharSet);
				break;
			case JSON:
				builder = new JsonEntityBuilder();
				entity = builder.build(params, reqCharSet);
				break;
			case STRING:
				builder = new StringEntityBuilder();
				entity = builder.build(body, reqCharSet);
				break;
			case MULTI_PART_FORM:
				builder = new MultiPartFormEntityBuilder();
				entity = builder.build(segments, reqCharSet);
				break;
		}
		return entity;
	}

	public HttpExchangeModel(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;

		String contentType = null;
		if(ExchangeType.FORM.equals(exchangeType)){
			contentType = "application/x-www-form-urlencoded;";
		} else if(ExchangeType.JSON.equals(exchangeType)){
			contentType = "application/json;";
		}
		if(StringUtils.isNotBlank(contentType)){
			headers = new HashMap<>();
			headers.put(HttpConstants.HEADER_KEY_CONTENT_TYPE, contentType);
		}
	}

	public HttpExchangeModel addHeader(String key, String value) {
		if(null == headers){
			headers = new HashMap<>();
		}
		headers.put(key, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		if(MapUtils.isNotEmpty(headers) && headers.containsKey(HttpConstants.HEADER_KEY_CONTENT_TYPE)){
			String val = headers.get(HttpConstants.HEADER_KEY_CONTENT_TYPE);
			if(StringUtils.isNotBlank(val) && !val.contains("charset=")){
				val = val + " charset=" + getReqCharSet();
				headers.put(HttpConstants.HEADER_KEY_CONTENT_TYPE, val);
			}
		}
		return headers;
	}

	public HttpExchangeModel addParams(String key, Object value) {
		if(null == params){
			params = new HashMap<>();
		}
		params.put(key, value);
		return this;
	}

	public HttpExchangeModel addSegment(String key, Object value) {
		return addSegment(new MultipartPartSegment(key, value));
	}

	public HttpExchangeModel addSegment(String key, Object value, String charset) {
		MultipartPartSegment segment = new MultipartPartSegment(key, value);
		segment.setCharSet(charset);
		return addSegment(segment);
	}

	public HttpExchangeModel addSegment(MultipartPartSegment segment) {
		if(null == segments){
			segments = new ArrayList<>();
		}
		segments.add(segment);
		return this;
	}

	public HttpExchangeModel setReqCharSet(String reqCharSet){
		this.reqCharSet = reqCharSet;
		return this;
	}

	public String getReqCharSet(){
		return this.reqCharSet;
	}

	public HttpExchangeModel setRespCharSet(String respCharSet){
		this.respCharSet = respCharSet;
		return this;
	}

	public String getRespCharSet(){
		return this.respCharSet;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public enum ExchangeType{
		JSON,
		STRING,
		FORM,
		MULTI_PART_FORM;
	}
}
