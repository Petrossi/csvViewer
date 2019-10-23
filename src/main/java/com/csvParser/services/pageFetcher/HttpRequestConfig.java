package com.csvParser.services.pageFetcher;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpRequestConfig {
    RequestConfig config;
    HttpClientBuilder httpClientBuilder;
    CloseableHttpClient httpClient = null;
    CloseableHttpResponse response = null;
    public HttpRequestConfig(RequestConfig config, HttpClientBuilder httpClientBuilder) {
        this.config = config;
        this.httpClientBuilder = httpClientBuilder.setDefaultRequestConfig(config).disableAutomaticRetries();
    }
}
