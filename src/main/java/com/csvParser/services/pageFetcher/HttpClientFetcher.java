package com.csvParser.services.pageFetcher;

import com.csvParser.services.ProxyService;
import com.csvParser.services.abstraction.LoggableService;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.ArrayUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static com.csvParser.utils.Utils.isRedirected;
import static com.csvParser.utils.Utils.isValidContentType;

@Service
public class HttpClientFetcher extends LoggableService {

    @Autowired
    private ProxyService proxyService;

    public static final List badStatusCodes = Arrays.asList(
        0,
        403,
        429,
        503,
        504,
        520,
        529,
        451,
        521
    );

    private String[] NOT_SECURE_PROTOCOLS = {"SSLv2Hello", "SSLv3", "TLSv1.1"};
    private String[] SECURE_PROTOCOLS = {"TLSv1", "TLSv1.2"};


    private final List<String> USER_AGENTS = Arrays.asList(
        "Linux / Firefox 29: Mozilla/5.0 (X11; Linux x86_64; rv:29.0) Gecko/20100101 Firefox/29.0",
        "Linux / Chrome 34: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36",
        "Mac / Firefox 29: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:29.0) Gecko/20100101 Firefox/29.0",
        "Mac / Chrome 34: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36",
        "Mac / Safari 7: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/537.75.14",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
        "Windows / Firefox 29: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0",
        "Windows / Chrome 34: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36",
        "Windows / IE 6: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
        "Windows / IE 7: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
        "Windows / IE 8: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
        "Windows / IE 9: Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
        "Windows / IE 10: Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
        "Windows / IE 11: Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
        "Android / Firefox 29: Mozilla/5.0 (Android; Mobile; rv:29.0) Gecko/29.0 Firefox/29.0",
        "Android / Chrome 34: Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36",
        "iOS / Chrome 34: Mozilla/5.0 (iPad; CPU OS 7_0_4 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) CriOS/34.0.1847.18 Mobile/11B554a Safari/9537.53",
        "iOS / Safari 7: Mozilla/5.0 (iPad; CPU OS 7_0_4 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11B554a Safari/9537.53"
    );

    public HttpResult getPage(HttpConfig httpConfig) throws IOException {
        HttpGet httpGet = createGet(httpConfig.url);

        return execute(httpConfig, httpGet);
    }

    public HttpResult doGet(String url, boolean needsProxy) throws Exception {
        if (!needsProxy) {
            return doGet(new HttpConfig(url, false, false, false));
        }

        return getPageWithProxyRetry(url, true);
    }

    public HttpResult getPageWithProxyRetry(String url, boolean onlyHeaders) {
        int count = 0;
        int max = 5;

        HttpResult result = null;

        while (count < max) {
            try {
                result = doGet(new HttpConfig(url, count > 0, onlyHeaders, false));

                if (badStatusCodes.contains(result.httpStatusCode)) {
                    count++;

                    continue;
                }

                break;
            } catch (Exception ignored) { }

            count++;
        }

        return result;
    }

    private RequestConfig createRequestConfig(HttpConfig httpConfig){
        return RequestConfig.custom()
            .setSocketTimeout(httpConfig.timeout)
            .setConnectTimeout(httpConfig.timeout)
            .setConnectionRequestTimeout(httpConfig.timeout)
            .setRedirectsEnabled(httpConfig.followRedirect)
            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build()
        ;
    }

    public HttpResult execute(HttpConfig httpConfig, HttpRequestBase httpMethod) throws IOException {
        HttpResult httpResult = new HttpResult();
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig(
            createRequestConfig(httpConfig),
            createHandchakrFailtureAllHttpClientBuilder()
        );

        if (httpConfig.proxy) {
            HttpHost httpHost = proxyService.getApacheRandomProxy();
            if(httpHost != null){
                httpResult.proxy = httpHost.toString();
                httpResult.headers.add("Proxy: " + httpHost.toString());
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);
                httpRequestConfig.httpClientBuilder.setRoutePlanner(routePlanner);
            }
        }

        try {
            httpRequestConfig.httpClient = httpRequestConfig.httpClientBuilder.build();

            httpMethod.setConfig(httpRequestConfig.config);

            httpRequestConfig.response = httpRequestConfig.httpClient.execute(httpMethod);
            httpResult.httpStatusCode = httpRequestConfig.response.getStatusLine().getStatusCode();

            httpResult.setHeaders(httpRequestConfig.response);

            if (isRedirected(httpResult.httpStatusCode)) {
                Header locationHeader = httpRequestConfig.response.getFirstHeader("location");
                if(locationHeader == null){
                    URL urlParts = new URL(httpConfig.url);
                    httpResult.location = String.format("%s://%s", urlParts.getProtocol(), urlParts.getHost());
                }else{
                    httpResult.location = locationHeader.getValue();
                }
            } else if (httpResult.httpStatusCode >= 200 && httpResult.httpStatusCode < 300) {
                HttpEntity entity = httpRequestConfig.response.getEntity();

                Charset charset = Charset.defaultCharset();

                try {
                    ContentType contentTypeObj = ContentType.getOrDefault(entity);

                    if (contentTypeObj.getCharset() != null) {
                        charset = contentTypeObj.getCharset();
                    }
                    httpResult.contentType = contentTypeObj.getMimeType();
                } catch (Exception ignored) {
                }
                httpResult.charsetStr = charset.displayName();

                if (!httpConfig.onlyHeaders) {

                    httpResult.contentLength = entity.getContentLength();

                    if (httpResult.contentType == null) {
                        throw new IOException("content type not found");
                    }
                    if (isValidContentType(httpResult.contentType)) {
                        httpResult.html = EntityUtils.toString(entity, charset);
                    }
                }
            }
        } finally {
            if(httpMethod != null){
                httpMethod.releaseConnection();
            }
            if(httpRequestConfig.response != null){
                httpRequestConfig.response.close();
            }
            if(httpRequestConfig.httpClient != null){
                httpRequestConfig.httpClient.close();
            }
        }

        return httpResult;
    }

    public HttpResult doGet(HttpConfig httpConfig) throws Exception {
        HttpGet httpGet = createGet(httpConfig.url);

        return execute(httpConfig, httpGet);
    }

    private HttpGet createGet(String url) {
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(url);
        } catch (IllegalArgumentException e) {
            try {
                URL urlToValidate = new URL(url);
                URI uri = new URI(urlToValidate.getProtocol(), urlToValidate.getUserInfo(), urlToValidate.getHost(), urlToValidate.getPort(), urlToValidate.getPath(), urlToValidate.getQuery(), urlToValidate.getRef());
                url = uri.toURL().toString();
            } catch (MalformedURLException | URISyntaxException e1) {
            }
            httpGet = new HttpGet(url);
        }

        return (HttpGet) initHttpRequestBase(httpGet);
    }

    public HttpResult doPost(HttpConfig httpConfig) throws IOException {
        HttpPost httpPost = createPost(httpConfig.url, httpConfig.requestData, httpConfig.contentType);;

        return execute(httpConfig, httpPost);
    }

    private HttpPost createPost(String url, String requestData, String contentType) {
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(url);
        } catch (IllegalArgumentException e) {
            try {
                URL urlToValidate = new URL(url);
                URI uri = new URI(urlToValidate.getProtocol(), urlToValidate.getUserInfo(), urlToValidate.getHost(), urlToValidate.getPort(), urlToValidate.getPath(), urlToValidate.getQuery(), urlToValidate.getRef());
                url = uri.toURL().toString();
            } catch (MalformedURLException | URISyntaxException e1) {
            }
            httpPost = new HttpPost(url);
        }
        initHttpRequestBase(httpPost);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        HttpEntity entity = null;
        try {
            entity = new StringEntity(requestData);
        } catch (UnsupportedEncodingException ignored) {}
        httpPost.setEntity(entity);

        return httpPost;
    }

    private HttpRequestBase initHttpRequestBase(HttpRequestBase httpRequestBase){
        httpRequestBase.setHeader(HttpHeaders.USER_AGENT, USER_AGENTS.get(0));
        httpRequestBase.setHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        httpRequestBase.setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        return httpRequestBase;
    }

    private HttpClientBuilder createHandchakrFailtureAllHttpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                ArrayUtils.addAll(NOT_SECURE_PROTOCOLS, SECURE_PROTOCOLS) , null,
                NoopHostnameVerifier.INSTANCE
            );
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", new SSLConnectionSocketFactory(
                    sslContext,
                    SECURE_PROTOCOLS, // important
                    null,
                    NoopHostnameVerifier.INSTANCE))
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build()
            ;

            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
            httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory).setConnectionManager(ccm);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return httpClientBuilder;
    }
}