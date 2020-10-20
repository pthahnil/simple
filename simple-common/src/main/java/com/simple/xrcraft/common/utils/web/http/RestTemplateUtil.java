package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.utils.bean.JsonUtils;
import com.simple.xrcraft.common.utils.web.http.interceptor.RequestHeaderInterceptor;
import com.simple.xrcraft.common.utils.web.http.model.KeyStoreProps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lixiaorong on 2018/11/1.
 */
public class RestTemplateUtil {

	/**
	 * post json方式
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, Object> params) throws Exception {
		return postJson(url, params, null);
	}

	public static String postJson(String url, Map<String, Object> params, Map<String, String> headerMap) throws Exception {
		return postJson(url, params, headerMap, null);
	}

	/**
	 * post json方式
	 * @param url
	 * @param params
	 * @param headerMap
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, Object> params, Map<String, String> headerMap, KeyStoreProps props) throws Exception {

		if(StringUtils.isBlank(url)){
			return null;
		}
		if(MapUtils.isEmpty(params)){
			return null;
		}
		String requestJson = JsonUtils.toJson(params);

		RestTemplate template = getTemplate(props);
		//header填充
		HttpHeaders headers = getheaders(headerMap);
		headers.add("Content-Type", "application/json; charset=UTF-8");

		HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);

		ResponseEntity<String> resp =  template.exchange(url, HttpMethod.POST, entity, String.class);
		return resp.getBody();
	}

	/**
	 * post form方式 除了200 其他会抛出错误
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, Object> params) throws Exception {
		return postForm(url, params, null);
	}

	public static String postForm(String url, Map<String, Object> params, Map<String, String> headerMap) throws Exception {
		return postForm(url, params, headerMap, null);
	}

	/**
	 * post form方式 除了200 其他会抛出错误
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, Object> params, Map<String, String> headerMap, KeyStoreProps props) throws Exception {

		if(StringUtils.isBlank(url)){
			return null;
		}

		RestTemplate template = getTemplate(props);

		if(MapUtils.isEmpty(params)){
			return null;
		}

		if(MapUtils.isEmpty(headerMap)){
			headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		}

		//header填充
		HttpHeaders headers = getheaders(headerMap);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params,headers);

		return template.execute(url, HttpMethod.POST, callback(), stringExtrator("UTF-8"), entity);
	}

	public static String postString(String url, String content) throws Exception {
		return postString(url, content, null, null);
	}

	public static String postString(String url, String content, Map<String, String> headerMap) throws Exception {
		return postString(url, content, headerMap, null);
	}

	/**
	 * 字符串直接请求
	 * @param url
	 * @param content
	 * @param headerMap
	 * @param props
	 * @return
	 * @throws Exception
	 */
	public static String postString(String url, String content, Map<String, String> headerMap, KeyStoreProps props) throws Exception {
		if(StringUtils.isBlank(url)){
			return null;
		}

		RestTemplate template = getTemplate(props);

		if(StringUtils.isBlank(content)){
			return null;
		}

		HttpHeaders headers = getheaders(headerMap);
		HttpEntity<String> entity = new HttpEntity<>(content, headers);

		return template.execute(url, HttpMethod.POST, callback(), stringExtrator("UTF-8"), entity);
	}

	public static String doGet(String url, Map<String, Object> params) throws Exception {
		return doGet(url, params, null, null);
	}

	public static String doGet(String url, Map<String, Object> params, Map<String, String> headerParams) throws Exception {
		return doGet(url, params, headerParams, null);
	}

	/**
	 * get
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String url, Map<String, Object> params, Map<String, String> headerParams, KeyStoreProps props) throws Exception {

		if(StringUtils.isBlank(url)){
			return null;
		}

		if(MapUtils.isNotEmpty(params)){
			url = HttpClientUtil.assembleGetUrl(url, params).toString();
		}

		RestTemplate template = getTemplate(props);
		//加header
		List<ClientHttpRequestInterceptor> interceptors = MapUtils.isNotEmpty(headerParams) ?
				headerParams.entrySet()
						.stream()
						.map(ent -> new RequestHeaderInterceptor(ent.getKey(), ent.getValue()))
						.collect(Collectors.toList()) : null;
		template.setInterceptors(interceptors);

		return template.execute(URI.create(url), HttpMethod.GET, callback(), stringExtrator("UTF-8"));
	}

	/**
	 *
	 * @return
	 */
	public static RequestCallback callback(){
		return request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
	}

	/**
	 * 用于提取流中的二进制
	 * @return
	 */
	public static ResponseExtractor<byte[]> byteExtrator(){
		return response -> {
			InputStream ins = response.getBody();
			return IOUtils.toByteArray(ins);
		};
	}

	/**
	 * 返回提取String
	 * @param charSet
	 * @return
	 */
	public static ResponseExtractor<String> stringExtrator(String charSet){
		return response -> {
			InputStream ins = response.getBody();
			byte[] bytes = IOUtils.toByteArray(ins);
			return new String(bytes, charSet);
		};
	}

	/**
	 * 是否ssl
	 * @return
	 */
	public static RestTemplate getTemplate(KeyStoreProps props) throws Exception {

		CloseableHttpClient client = HttpClientUtil.getClient(props);
		ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

		return new RestTemplate(factory);
	}

	/**
	 * header 参数填充
	 * @param headers
	 * @return
	 */
	private static HttpHeaders getheaders(Map<String, String> headers){
		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add("Content-Encoding", "UTF-8");

		if(MapUtils.isNotEmpty(headers)){
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		return httpHeaders;
	}
}
