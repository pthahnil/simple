package com.simple.xrcraft.common.utils.web.http.model;

import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.web.http.model.entity.EntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.FormEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.JsonEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.MultiPartFormEntityBuilder;
import com.simple.xrcraft.common.utils.web.http.model.entity.StringEntityBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
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

	public void write(OutputStream os, String boundary) throws Exception {
		ExchangeType type = this.exchangeType;
		if(this.exchangeType.equals(ExchangeType.FORM) || this.exchangeType.equals(ExchangeType.JSON)
				|| this.exchangeType.equals(ExchangeType.STRING)){
			HttpEntity entity = this.getEntity();
			IOUtils.copy(entity.getContent(), os);
		} else {
			write(this.segments, os, boundary);
		}
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

	public void setBody(String body) {
		this.body = body;
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

	public List<MultipartPartSegment> getSegments() {
		return segments;
	}

	public String getRespCharSet(){
		return this.respCharSet;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	public enum ExchangeType{
		JSON,
		STRING,
		FORM,
		MULTI_PART_FORM;
	}

	public static void write(List<MultipartPartSegment> segments, OutputStream os, String boundary) throws Exception {
		if(null == os){
			throw new IOException("outputstream already closed");
		}

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, HttpConstants.CHARSET_UTF8), true);
		for (MultipartPartSegment param : segments) {
			writer.append("--" + boundary).append(HttpConstants.CRLF);

			String key = param.getKey();
			Object value = param.getValue();

			if(value instanceof File) {
				File val = (File) value;
				writer.append("Content-Disposition: form-data; name="+ key +"; filename=").append(val.getName()).append(HttpConstants.CRLF);
				writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(val.getName())).append(HttpConstants.CRLF);

				writer.append("Content-Transfer-Encoding: binary").append(HttpConstants.CRLF);
				writer.append(HttpConstants.CRLF).flush();

				Files.copy(val.toPath(), os);
				os.flush();

				writer.append(HttpConstants.CRLF).flush();
			} else {
				String valueStr = null == value ? "" : String.valueOf(value);
				writer.append("Content-Disposition: form-data; name=").append(key).append(HttpConstants.CRLF);
				writer.append("Content-Type: text/plain; charset=").append(param.getCharSet()).append(HttpConstants.CRLF);
				writer.append(HttpConstants.CRLF).append(valueStr).append(HttpConstants.CRLF).flush();
			}
		}
		writer.append("--" + boundary + "--").append(HttpConstants.CRLF).flush();
	}
}
