package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.utils.bean.JsonUtils;
import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.web.http.model.MultipartPartSegment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用http/https工具类
 */
@Slf4j
public class BasicHttpUtil {

    /**连接超时*/
    private static int CONN_TIME_OUT = 15 * 1000;
    /**读取超时*/
    private static int READ_TIME_OUT = 10 * 1000;

    /**
     * 报文格式：常见的key1=val1&key2=val2形式
     * @param uri uri
     * @param params 请求参数
     * @return
     */
    public static String postRawForm(String uri, Map<String, Object> params, Map<String, String> headers) throws Exception {
        String requestStr =params.entrySet().stream()
                .map(ent -> ent.getKey() + "=" + (null != ent.getValue() ? ent.getValue().toString() : ""))
                .collect(Collectors.joining(HttpConstants.SEPERATOR_AND));
        return sendPost(uri, requestStr, headers);
    }

    /**
     * 报文格式：form表单
     * @param uri uri
     * @param params 请求参数
     * @return
     */
    public static String postEncodedForm(String uri, Map<String, Object> params, Map<String, String> headers) throws Exception {
        if(MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=" + HttpConstants.CHARSET_UTF8);

        String requestStr = params.entrySet()
                .stream()
                .map(ent -> {
                    try {
                        String encodedKey = URLEncoder.encode(ent.getKey(), HttpConstants.CHARSET_UTF8);
                        String encodedVal = null != ent.getValue() ? URLEncoder.encode(ent.getValue().toString(), HttpConstants.CHARSET_UTF8) : "";
                        return encodedKey + "=" + encodedVal;
                    } catch (Exception e) {}
                    return null;
                })
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(HttpConstants.SEPERATOR_AND));
        return sendPost(uri, requestStr, headers);
    }

    /**
     * 报文格式：json
     * @param uri uri
     * @param params 请求参数
     * @return
     */
    public static String postJson(String uri, Map<String, Object> params, Map<String, String> headers) throws Exception {
        if(MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "application/json; charset=" + HttpConstants.CHARSET_UTF8);
        return sendPost(uri, JsonUtils.toJson(params), headers);
    }

    /**
     * post
     * @param uri
     * @param requestStr
     * @param headers
     * @return
     */
    public static String sendPost(String uri, String requestStr, Map<String, String> headers) throws Exception {
        return sendPost(uri, requestStr, headers, null, null);
    }

    /**
     * post
     * @param uri uri
     * @param requestStr 请求报文体，string
     * @return
     */
    public static String sendPost(String uri, String requestStr, Map<String, String> headers, InputStream stream, String certPwd) throws Exception {
        log.info("请求参数：{}", requestStr);
        HttpURLConnection connection = null;
        try {
            connection = basicConn(uri, HttpConstants.METHOD_POST, headers, stream, certPwd);
            connection.connect();

            vertValidate(connection);

            connection.getOutputStream().write(requestStr.getBytes(StandardCharsets.UTF_8));
            InputStream is = connection.getInputStream();

            byte[] outBts = IOUtils.readFully(is, is.available());
            String resp = new String(outBts, Charset.forName(HttpConstants.CHARSET_UTF8));

            log.info("返回结果：{}", resp);
            return resp;
        } catch (Exception e) {
            log.error("{}请求异常", uri, e);
            throw e;
        } finally {
            if(null != connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * post 带文件的表单
     * @return
     * @throws Exception
     */
    public static String formWithFile(String uri, Map<String, String> headers, List<MultipartPartSegment> params) throws Exception {
        HttpURLConnection connection = null;
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());

            connection = basicConn(uri, HttpConstants.METHOD_POST, headers, null, null);
            connection.setRequestProperty(HttpConstants.HEADER_KEY_CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
            connection.connect();

            vertValidate(connection);

            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, HttpConstants.CHARSET_UTF8), true);
            for (MultipartPartSegment param : params) {
                writer.append("--" + boundary).append(HttpConstants.CRLF);

                String key = param.getKey();
                Object value = param.getValue();

                if(value instanceof File) {
                    File val = (File) value;
                    writer.append("Content-Disposition: form-data; name="+ key +"; filename=").append(val.getName()).append(HttpConstants.CRLF);
                    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(val.getName())).append(HttpConstants.CRLF);

                    writer.append("Content-Transfer-Encoding: binary").append(HttpConstants.CRLF);
                    writer.append(HttpConstants.CRLF).flush();

                    Files.copy(val.toPath(), output);
                    output.flush();

                    writer.append(HttpConstants.CRLF).flush();
                } else {
                    String valueStr = null == value ? "" : String.valueOf(value);
                    writer.append("Content-Disposition: form-data; name=").append(key).append(HttpConstants.CRLF);
                    writer.append("Content-Type: text/plain; charset=").append(param.getCharSet()).append(HttpConstants.CRLF);
                    writer.append(HttpConstants.CRLF).append(valueStr).append(HttpConstants.CRLF).flush();
                }
            }
            writer.append("--" + boundary + "--").append(HttpConstants.CRLF).flush();

            InputStream is = connection.getInputStream();
            byte[] outBts = IOUtils.toByteArray(is);
            return new String(outBts, Charset.forName(HttpConstants.CHARSET_UTF8));
        } catch (Exception e) {
            log.error("{}请求异常", uri, e);
            throw e;
        } finally {
            if(null != connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * get
     * @param uri uri
     * @param headers 请求头
     * @return
     */
    public static String doGet(String uri, Map<String, Object> params, Map<String, String> headers) throws Exception {

        URIBuilder builder = new URIBuilder(uri);
        if(MapUtils.isNotEmpty(params)){
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(null != entry.getValue() ? entry.getValue() : "");
                builder.addParameter(key, value);
            }
        }

        String url = builder.build().toString();
        return doGet(url, headers);
    }

    /**
     * get
     * @param uri uri
     * @param headers 请求头
     * @return
     */
    public static String doGet(String uri, Map<String, String> headers) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = basicConn(uri, HttpConstants.METHOD_GET, headers, null, null);
            connection.connect();

            vertValidate(connection);

            InputStream is = connection.getInputStream();

            byte[] outBts = IOUtils.readFully(is, is.available());
            String resp = new String(outBts, Charset.forName(HttpConstants.CHARSET_UTF8));

            log.info("返回结果：{}", resp);
            return resp;
        } catch (Exception e) {
            log.error("{}请求异常", uri, e);
            throw e;
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
        return basicConn(requestUrl, method, headerMap, null, null);
    }

    /**
     * 获取httpUrlConnection
     * @param requestUrl
     * @param method
     * @param headerMap
     * @return
     */
    private static HttpURLConnection basicConn(String requestUrl, String method, Map<String, String> headerMap, InputStream stream, String certPwd) throws Exception {
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
            SSLContext sslContext = getSslContext(stream, certPwd);
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

    /**
     * ssl with cert files
     * @param certStream
     * @param pwd
     * @return
     * @throws Exception
     */
    public static SSLContext getSslContext(InputStream certStream, String pwd) throws Exception {
        TrustManager[] tmgs = new TrustManager[]{getTrustAllManager()};
        KeyManager[] kmgs = null;

        SSLContext ctx = SSLContext.getInstance("TLS");
        if(null != certStream){
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] pass = StringUtils.isNotBlank(pwd) ? pwd.toCharArray() : null;
            ks.load(certStream, pass);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pass);

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
}
