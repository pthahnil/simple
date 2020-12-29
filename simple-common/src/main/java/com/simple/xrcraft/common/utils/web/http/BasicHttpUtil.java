package com.simple.xrcraft.common.utils.web.http;

import com.simple.xrcraft.common.constants.HttpConstants;
import com.simple.xrcraft.common.utils.web.http.model.HttpExchangeModel;
import com.simple.xrcraft.common.utils.web.http.model.KeyStoreProps;
import com.simple.xrcraft.common.utils.web.http.model.MultipartPartSegment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

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
        HttpURLConnection connection = null;
        try {
            boolean validProps = null != props && null != props.getStream();
            InputStream stream = validProps ? props.getStream() : null;
            String certPwd = validProps ? props.getCertPwd() : null;

            Map<String, String> headers = null != model ? model.getHeaders() : null;

            connection = basicConn(url, HttpConstants.METHOD_POST, headers, stream, certPwd);
            connection.connect();

            vertValidate(connection);

            if(null != model){
                HttpEntity entity = model.getEntity();
                IOUtils.copy(entity.getContent(), connection.getOutputStream());
            }

            InputStream is = connection.getInputStream();

            byte[] outBts = IOUtils.readFully(is, is.available());
            String resp = new String(outBts, Charset.forName(HttpConstants.CHARSET_UTF8));

            return resp;
        } catch (Exception e) {
            log.error("{}请求异常", url, e);
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
        String url = HttpClientUtil.assembleGetUrl(uri, params).toString();
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
