package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.web.http.model.HttpExchangeModel;
import com.simple.xrcraft.common.utils.web.http.model.KeyStoreProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用http/https工具类
 */
@Slf4j
public class BasicHttpUtil extends BaseHttpOperation {

    /**
     * post
     * @param url
     * @param model
     * @return
     * @throws Exception
     */
    public static String post(String url, HttpExchangeModel model) throws Exception {
        return post(url, model, null);
    }

    /**
     * todo 带文件的post未测试
     * @param url
     * @param model
     * @param props
     * @return
     * @throws Exception
     */
    public static String post(String url, HttpExchangeModel model, KeyStoreProps props) throws Exception {
        ByteArrayOutputStream os = basePost(url, model, props);
        byte[] bytes = os.toByteArray();
        return new String(bytes, model.getRespCharSet());
    }

    /**
     * todo 带文件的post未测试
     * @param url
     * @param model
     * @param props
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream basePost(String url, HttpExchangeModel model, KeyStoreProps props) throws Exception {
        HttpURLConnection connection = null;
        try {
            Map<String, String> headers = null != model ? model.getHeaders() : null;
            String boundary = null;
            if(null != model && HttpExchangeModel.ExchangeType.MULTI_PART_FORM.equals(model.getExchangeType())){
                boundary = Long.toHexString(System.currentTimeMillis());
                if(null == headers){
                    headers = new HashMap<>();
                }
                headers.put(HttpConstants.HEADER_KEY_CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
            }
            connection = basicConn(url, HttpConstants.METHOD_POST, headers, props);

            connection.connect();

            vertValidate(connection);
            if(null != model){
                if(HttpExchangeModel.ExchangeType.MULTI_PART_FORM.equals(model.getExchangeType())){
                    //用下面分方法会报错，只能这么玩
                    model.write(connection.getOutputStream(), boundary);
                } else {
                    HttpEntity entity = model.getEntity();
                    IOUtils.copy(entity.getContent(), connection.getOutputStream());
                }
            }

            int code = connection.getResponseCode();
            if(code != 200){
                throw new Exception(IOUtils.toString(connection.getErrorStream(), "utf8"));
            }

            InputStream is = connection.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            IOUtils.copy(is, outStream);

            return outStream;
        } finally {
            if(null != connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * get
     * @param uri
     * @param model
     * @return
     * @throws Exception
     */
    public static String doGet(String uri, HttpExchangeModel model) throws Exception {
        ByteArrayOutputStream stream = doGet(uri, model, null);
        byte[] outBts = stream.toByteArray();
        return new String(outBts, model.getRespCharSet());
    }

    /**
     * get
     * @param uri
     * @param model
     * @param props
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream doGet(String uri, HttpExchangeModel model, KeyStoreProps props) throws Exception {
        HttpURLConnection connection = null;
        try {
            Map<String, Object> params = model.getParams();
            if(MapUtils.isEmpty(params)){
                params = new HashMap<>();
            }
            Map<String, Object> urlParams = model.getUrlParams();
            if(MapUtils.isNotEmpty(urlParams)){
                params.putAll(urlParams);
            }
            Map<String, String> headers = model.getHeaders();
            String url = HttpClientUtil.assembleGetUrl(uri, params).toString();

            connection = basicConn(url, HttpConstants.METHOD_GET, headers, props);
            connection.connect();

            vertValidate(connection);

            InputStream is = connection.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            IOUtils.copy(is, outStream);

            return outStream;
        } finally {
            if(null != connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取httpUrlConnection
     * @param requestUrl
     * @param method
     * @param headerMap
     * @return
     * @throws Exception
     */
    private static HttpURLConnection basicConn(String requestUrl, String method, Map<String, String> headerMap) throws Exception {
        return basicConn(requestUrl, method, headerMap, null);
    }

    /**
     * 获取httpUrlConnection
     * @param requestUrl
     * @param method
     * @param headerMap
     * @return
     */
    private static HttpURLConnection basicConn(String requestUrl, String method, Map<String, String> headerMap, KeyStoreProps props) throws Exception {
        if(StringUtils.isBlank(requestUrl)){
            throw new Exception("request url can not be null!");
        }
        URL url = new URL(requestUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method == null ? HttpConstants.METHOD_POST : method);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(false);
        con.setConnectTimeout(CONN_TIME_OUT);
        con.setReadTimeout(READ_TIME_OUT);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                con.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if(HttpConstants.PROTOCOL_HTTPS.equalsIgnoreCase(url.getProtocol())){
            HttpsURLConnection httpScon = (HttpsURLConnection) con;

            httpScon.setHostnameVerifier((s, session)-> true);
            SSLContext sslContext = getSslContext(props);
            httpScon.setSSLSocketFactory(sslContext.getSocketFactory());
        }

        return con;
    }

    /**
     * 证书校验
     * @param connection
     * @return
     */
    public static boolean vertValidate(HttpURLConnection connection) {

        if(null == connection){
            log.info("connection is null");
            return false;
        }

        if(!(connection instanceof HttpsURLConnection)){
            log.info("非 https，无证书");
            return true;
        }

        HttpsURLConnection conn = (HttpsURLConnection) connection;

        Certificate[] certs = null;
        try {
            certs = conn.getServerCertificates();
        } catch (Exception e){
            log.info("证书获取异常");
            return false;
        }

        X509Certificate x509Cert = (X509Certificate) certs[0];
        try {
            x509Cert.checkValidity();
        } catch (Exception e){
            log.info("证书已过期");
            return false;
        }
        Principal issuerDN = x509Cert.getIssuerDN();
        Principal subjectDN = x509Cert.getSubjectDN();
        if(issuerDN.equals(subjectDN)){
            log.info("url:{} 的证书是自签证书，请谨慎！！", conn.getURL().toString());
            return false;
        }
        return true;
    }
}
