package com.csvParser.services.pageFetcher;

public class HttpConfig {

    public static final String GET_REQUEST = "GET";
    public static final String POST_REQUEST = "POST";
    public static int DEFAULT_TIMEOUT = 15 * 1000;

    public static final String DESCRIPTOR_METHOD = "descriptor";
    public static final String PHANTOM_METHOD = "phantom";
    public static final String HTTP_METHOD = "http";

    public String url;
    public String type = GET_REQUEST;
    public boolean onlyHeaders;
    public boolean followRedirect;
    public int maxRetries = 1;
    public boolean proxy;
    public String contentType = null;
    public String requestData = null;
    public String serviceType = "http";
    int timeout = DEFAULT_TIMEOUT;


    public HttpConfig() {
    }

    public HttpConfig(String url, boolean proxy) {
        this.url = url;
        this.proxy = proxy;
    }

    public HttpConfig(String url, boolean proxy, boolean onlyHeaders, boolean followRedirect) {
        this.url = url;
        this.proxy = proxy;
        this.onlyHeaders = onlyHeaders;
        this.followRedirect = followRedirect;
    }

    public HttpConfig(String url, String type, boolean onlyHeaders, boolean followRedirect, boolean proxy, String contentType, String requestData) {
        this.url = url;
        this.type = type;
        this.onlyHeaders = onlyHeaders;
        this.followRedirect = followRedirect;
        this.proxy = proxy;
        this.contentType = contentType;
        this.requestData = requestData;
    }
    public void setTimeoutSeconds(int timeout){
        this.timeout = timeout * 1000;
    }
}