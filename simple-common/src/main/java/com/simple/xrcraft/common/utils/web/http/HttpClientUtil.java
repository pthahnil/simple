package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.bean.JsonUtils;
import com.simple.xrcraft.common.utils.web.http.extrator.ExtractorFactory;
import com.simple.xrcraft.common.utils.web.http.extrator.Extrator;
import com.simple.xrcraft.common.utils.web.http.model.KeyStoreProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *  https://www.programcreek.com/java-api-examples/?class=org.apache.http.impl.conn.PoolingHttpClientConnectionManager&method=setMaxPerRoute
 *  Example 9
 *
 * Created by lixiaorong on 2018/11/5.
 */
@Slf4j
public class HttpClientUtil {

	//超时
	private static int CONNECT_TIMEOUT = 20 * 1000;

	//默认连接池最大=====基本请求都用默认，所以线程数设大一点
	private static int POOL_MAX_DEFAULT = 20;

	//默认单个地址连接最大
	private static int ROUTER_MAX_DEFAULT = 5;

	//连接池最大
	private static int POOL_MAX_CT = 10;

	//单个地址连接最大
	private static int ROUTER_MAX_CT = 5;

	//默认连接池 key
	private static String DEFAULT_POOL = "defaultPool";

	//连接池
	private static Map<String, PoolingHttpClientConnectionManager> mappedConnManger = new HashMap<>();

	//链接配置
	private static RequestConfig REQ_CONFIG;

	//http直连
	private static ConnectionSocketFactory PLAIN_SF = PlainConnectionSocketFactory.getSocketFactory();

	/**
	 * post json
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, Object> params) throws Exception {
		return postJson(url, params, null, null, String.class);
	}

	public static <T> T postJson(String url, Map<String, Object> params, Class<T> clazz) throws Exception {
		return postJson(url, params, null, null, clazz);
	}

	public static <T> T postJson(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) throws Exception {
		return postJson(url, params, headers, null, clazz);
	}

	public static <T> T postJson(String url, Map<String, Object> params, Map<String, String> headers, KeyStoreProps props, Class<T> clazz) throws Exception {

		String json = JsonUtils.toJson(params);
		StringEntity entity = new StringEntity(json, HttpConstants.CHARSET_UTF8);// 解决中文乱码问题
		entity.setContentEncoding(HttpConstants.CHARSET_UTF8);
		entity.setContentType("application/json");

		return post(url, headers, entity, props, clazz);
	}

	/**
	 * post String
	 * @param url
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String postString(String url, String content) throws Exception {
		return postString(url, content, null, null, String.class);
	}

	public static <T> T postString(String url, String content, Class<T> clazz) throws Exception {
		return postString(url, content, null, null, clazz);
	}

	public static <T> T postString(String url, String content, Map<String, String> headers, Class<T> clazz) throws Exception {
		return postString(url, content, headers, null, clazz);
	}

	public static <T> T postString(String url, String content, Map<String, String> headers, KeyStoreProps props, Class<T> clazz) throws Exception {

		if(StringUtils.isBlank(content)){
			return null;
		}

		StringEntity entity = new StringEntity(content, HttpConstants.CHARSET_UTF8);
		entity.setContentEncoding(HttpConstants.CHARSET_UTF8);

		return post(url, headers, entity, props, clazz);
	}

	/**
	 * post form
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, Object> params) throws Exception {
		return postForm(url, params, null, null, String.class);
	}

	public static <T> T postForm(String url, Map<String, Object> params, Class<T> clazz) throws Exception {
		return postForm(url, params, null, null, clazz);
	}

	public static <T> T postForm(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) throws Exception {
		return postForm(url, params, headers, null, clazz);
	}

	public static <T> T postForm(String url, Map<String, Object> params, Map<String, String> headers, KeyStoreProps props, Class<T> clazz) throws Exception {

		HttpEntity entity = getEncodedFormEntity(params);

		return post(url, headers, entity, props, clazz);
	}

	/**
	 * main post
	 * @param url
	 * @param headers
	 * @param entity
	 * @param props 信任证书相关参数
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> T post(String url, Map<String, String> headers, HttpEntity entity, KeyStoreProps props, Class<T> clazz) throws Exception {

		if(StringUtils.isBlank(url)){
			return null;
		}

		HttpPost method = new HttpPost(url);
		setHeaders(headers, method);

		HttpEntity responseEntity = null;
		try {
			CloseableHttpClient client = getClient(props);
			method.setEntity(entity);

			CloseableHttpResponse response = client.execute(method);

			Integer statusCode = response.getStatusLine().getStatusCode();
			responseEntity = response.getEntity();

			Extrator<T> extrator = ExtractorFactory.getExtrator(clazz);

			return dealResponse(responseEntity, extrator, statusCode);
		} catch (Exception e){
			log.error("{}调用异常", url, e);
			//释放连接
			method.releaseConnection();
			throw e;
		} finally {
			if(null != responseEntity) {
				//连接丢回连接池
				EntityUtils.consumeQuietly(responseEntity);
			}
		}
	}

	public static String doGet(String url) throws Exception {
		return doGet(url, null, null, null, String.class, true);
	}

	public static <T> T doGet(String url, Class<T> clazz) throws Exception {
		return doGet(url, null, null, null, clazz, true);
	}

	public static String doGet(String url, Map<String, Object> params) throws Exception {
		return doGet(url, params, null, null, String.class, true);
	}

	public static String doGet(String url, Map<String, Object> params, boolean raw) throws Exception {
		return doGet(url, params, null, null, String.class, raw);
	}

	public static <T> T doGet(String url, Map<String, Object> params, Class<T> clazz) throws Exception {
		return doGet(url, params, null, null, clazz, true);
	}

	public static <T> T doGet(String url, Map<String, Object> params, Class<T> clazz, boolean raw) throws Exception {
		return doGet(url, params, null, null, clazz, raw);
	}

	public static <T> T doGet(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) throws Exception {
		return doGet(url, params, headers, null, clazz, true);
	}

	/**
	 * get调用
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public static <T> T doGet(String url, Map<String, Object> params, Map<String, String> headers, KeyStoreProps props, Class<T> clazz, boolean raw) throws Exception {

		if(StringUtils.isBlank(url)){
			return null;
		}

		String getUrl = raw ? assembleGetUrlRaw(url, params) : assembleGetUrl(url, params).toString();

		HttpGet method = new HttpGet(getUrl);

		setHeaders(headers, method);
		HttpEntity responseEntity = null;
		try {
			CloseableHttpClient client = getClient(props);
			CloseableHttpResponse response = client.execute(method);

			if(null != response){
				int statusCode = response.getStatusLine().getStatusCode();
				responseEntity = response.getEntity();

				Extrator<T> extrator = ExtractorFactory.getExtrator(clazz);
				return dealResponse(responseEntity, extrator, statusCode);
			}
		} catch (Exception e){
			log.error("{}调用异常", url, e);
			//释放连接
			method.releaseConnection();
			throw e;
		} finally {
			if(null != responseEntity) {
				//连接丢回连接池
				EntityUtils.consumeQuietly(responseEntity);
			}
		}
		return null;
	}

	/**
	 * response 处理
	 * @param responseEntity
	 * @param extrator
	 * @param statusCode
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	private static <T> T dealResponse(HttpEntity responseEntity, Extrator<T> extrator, int statusCode) throws Exception {
		if(HttpStatus.SC_OK != statusCode){
			String info = EntityUtils.toString(responseEntity, HttpConstants.CHARSET_UTF8);
			log.error("接口调用异常，返回码:{},错误信息:{}" , statusCode, info);
			throw new Exception(info);
		} else {
			T t = extrator.extract(responseEntity, HttpConstants.CHARSET_UTF8);
			return t;
		}
	}

	/**
	 * form表单组装
	 * @param params
	 * @return
	 */
	public static HttpEntity getEncodedFormEntity(Map<String, Object> params){
		HttpEntity entity = null;
		if(MapUtils.isNotEmpty(params)){
			List<NameValuePair> pairList = new ArrayList<>();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			entity = new UrlEncodedFormEntity(pairList, Charset.forName(HttpConstants.CHARSET_UTF8));
		}
		return entity;
	}

	/**
	 * multiPartFormData表单组装,用于发送包含文件和字段的
	 * @param params
	 * @return
	 */
	public static HttpEntity getFormEntity(Map<String, Object> params){

		HttpEntity entity = null;

		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		if(MapUtils.isNotEmpty(params)){
			ContentType contentType = ContentType.create("text/plain", HttpConstants.CHARSET_UTF8);
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				ContentBody body = null;
				if(value instanceof String) {
					body = new StringBody(value.toString(), contentType);
				} else if(value instanceof File) {
					body = new FileBody(((File)value));
				} else if(value instanceof byte[]) {
					body = new ByteArrayBody(((byte[])value), key);
				} else if(value instanceof InputStream) {
					body = new InputStreamBody(((InputStream)value), key);
				}
				if(null != body) {
					multipartEntityBuilder.addPart(key, body);
				}
			}
			entity = multipartEntityBuilder.build();
		}

		return entity;
	}

	/**
	 * 设置请求头
	 * @param headers
	 * @param request
	 */
	private static void setHeaders(Map<String, String> headers, HttpRequest request){
		if(MapUtils.isNotEmpty(headers)){
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * ssl with cert files
	 * @param keyStore
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSslContext(KeyStore keyStore, String pwd) throws Exception {
		TrustManager[] tmgs = new TrustManager[]{getTrustAllManager()};
		KeyManager[] kmgs = null;

		SSLContext ctx = SSLContext.getInstance("TLS");
		if(null != keyStore){
			char[] pass = StringUtils.isNotBlank(pwd) ? pwd.toCharArray() : null;

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, pass);

			kmgs = kmf.getKeyManagers();
		}
		ctx.init(kmgs, tmgs, null);

		return ctx;
	}

	/**
	 * trustAll
	 * @return
	 */
	private static TrustManager getTrustAllManager(){
		return new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
	}

	/**
	 * 注意！！！外部使用这个方法，必须释放连接
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpClient getClient() throws Exception {
		return getClient(null);
	}

	/**
	 * 注意！！！外部使用这个方法，必须释放连接
	 * 基本配置，例如 超时，跳转
	 * @return
	 */
	public static CloseableHttpClient getClient(KeyStoreProps props) throws Exception {

		return getClient(props, null);
	}

	/**
	 * 基本配置，例如 超时，跳转
	 * @return
	 */
	public static CloseableHttpClient getClient(KeyStoreProps props, BasicCookieStore cookieStore) throws Exception {
		KeyStore keyStore = null;
		String keyStoreName = null;
		String certPwd = null;

		if(null != props){
			keyStore = props.getKeyStore();
			keyStoreName = props.getKeyStoreAlias();
			certPwd = props.getCertPwd();
		}

		boolean isDefault = null == keyStore;

		String key = isDefault ? DEFAULT_POOL : keyStoreName;
		PoolingHttpClientConnectionManager connectionManager = mappedConnManger.get(key);
		if(null == connectionManager) {
			Registry<ConnectionSocketFactory> registry = getRegistry(keyStore, certPwd);

			connectionManager = new PoolingHttpClientConnectionManager(registry);
			connectionManager.setMaxTotal(isDefault ? POOL_MAX_DEFAULT : POOL_MAX_CT);
			connectionManager.setDefaultMaxPerRoute(isDefault ? ROUTER_MAX_DEFAULT : ROUTER_MAX_CT);
			connectionManager.setValidateAfterInactivity(500);//检查连接是否可用
			connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);//空闲连接关闭
			connectionManager.closeExpiredConnections();

			mappedConnManger.put(key, connectionManager);
		}

		RequestConfig reqConfig = getReqConfig();
		return HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(reqConfig)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setConnectionManagerShared(true)
				.setKeepAliveStrategy((response, contest) -> 60 * 1000)
				.setRetryHandler(getRetryHandler())//重试三次
				.setDefaultCookieStore(cookieStore)
				.useSystemProperties()
				.build();
	}

	/**
	 * 重试机制
	 * @return
	 */
	private static HttpRequestRetryHandler getRetryHandler(){
		return (exception, count, context) ->{
			if (count > 3) {//重试次数
				return false;
			}
			if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
				return true;
			}
			if (exception instanceof InterruptedIOException) {// 超时
				return false;
			}
			if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
				return false;
			}
			if (exception instanceof UnknownHostException) {// 目标服务器不可达
				return false;
			}
			if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
				return false;
			}
			if (exception instanceof SSLException) {// SSL握手异常
				return false;
			}
			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			// 如果请求是幂等的，就再次尝试
			if (!(request instanceof HttpEntityEnclosingRequest)) {
				return true;
			}
			return false;
		};
	}

	/**
	 * default registry
	 * @return
	 * @throws Exception
	 */
	private static Registry<ConnectionSocketFactory> getRegistry(KeyStore keyStore, String certPwd) throws Exception {
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(getSslContext(keyStore, certPwd));

		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register(HttpConstants.PROTOCOL_HTTP, PLAIN_SF)
				.register(HttpConstants.PROTOCOL_HTTPS, sslsf)
				.build();

		return registry;
	}

	/**
	 * 超时设置
	 * @return
	 */
	private static RequestConfig getReqConfig(){
		if(null == REQ_CONFIG){
			REQ_CONFIG = RequestConfig.custom()
					.setSocketTimeout(CONNECT_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT)
					.setConnectionRequestTimeout(CONNECT_TIMEOUT)
					.setCircularRedirectsAllowed(false)//不跳转
					.setRedirectsEnabled(false)//不跳转
					.build();
		}
		return REQ_CONFIG;
	}

	/**
	 * 组装get地址
	 * @param url
	 * @param params
	 * @return
	 */
	public static URI assembleGetUrl(String url, Map<String, Object> params) throws Exception {

		URIBuilder builder = new URIBuilder(url);
		if(MapUtils.isNotEmpty(params)){
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String key = entry.getKey();
				String value = String.valueOf(null != entry.getValue() ? entry.getValue() : "");
				builder.addParameter(key, value);
			}
		}
		return builder.build();
	}

	/**
	 * 组装get地址
	 * @param url
	 * @param params
	 * @return
	 */
	public static String assembleGetUrlRaw(String url, Map<String, Object> params) throws Exception {

		StringBuilder builder = new StringBuilder();
		builder.append(url);

		if(null != params && !params.isEmpty()){
			String paramsStr = params.entrySet()
					.stream()
					.map(ent -> ent.getKey() + "=" + ((null != ent.getValue()) ? String.valueOf(ent.getValue()) : ""))
					.collect(Collectors.joining(HttpConstants.SEPERATOR_AND));
			builder.append("?").append(paramsStr);
		}
		return builder.toString();
	}

}
