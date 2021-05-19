package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.utils.web.http.model.KeyStoreProps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/19 14:51
 */
public class BaseHttpOperation {

	/**连接超时*/
	protected static final int CONN_TIME_OUT = 5 * 1000;

	protected static final int CONN_REQ_TIME_OUT = 5 * 1000;
	/**读取超时*/
	protected static final int READ_TIME_OUT = 10 * 1000;


	private static final TrustManager trustAll;
	static {
		trustAll = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}
			public void checkServerTrusted(X509Certificate[] chain, String authType){}
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
	}

	/**
	 * trustAll
	 * @return
	 */
	protected static TrustManager getTrustAllManager(){
		return trustAll;
	}

	/**
	 * ssl with cert files
	 * @param props
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSslContext(KeyStoreProps props) throws Exception {
		TrustManager[] tmgs = new TrustManager[]{getTrustAllManager()};
		KeyManager[] kmgs = null;

		SSLContext ctx = SSLContext.getInstance("TLS");
		if(null != props.getStream()){
			KeyStore ks = KeyStore.getInstance(props.getKeyStoreType());
			char[] pass = StringUtils.isNotBlank(props.getCertPwd()) ? props.getCertPwd().toCharArray() : null;
			ks.load(props.getStream(), pass);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, pass);

			kmgs = kmf.getKeyManagers();
		}
		ctx.init(kmgs, tmgs, null);

		return ctx;
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
}
