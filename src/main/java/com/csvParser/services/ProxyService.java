package com.csvParser.services;

import com.csvParser.services.abstraction.LoggableService;
import com.csvParser.services.pageFetcher.HttpClientFetcher;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "crawler.proxy")
public class ProxyService extends LoggableService {

    @Autowired
    private HttpClientFetcher httpClientFetcher;

    private Random randomGenerator = new Random();
    private String url;
    private boolean needProxy;
    private boolean inited;

    private List<CustomProxy> proxies = Collections.synchronizedList(new ArrayList<>());

    private int updateDelay;

    @PostConstruct
    public void init(){
        initProxiesFromBlazingseollc();
    }

    @Scheduled(fixedDelayString = "${proxy.updateDelay}")
    public void initProxiesFromBlazingseollc() {
        try{
            if(!needProxy){
                return;
            }

            String html = httpClientFetcher.doGet(url, false).html;

            proxies = Arrays.stream(html.split("\n")).filter(prxyStr -> !Objects.equals(prxyStr, ":")).map(proxyStr -> {
                try{
                    String prosyHost = proxyStr.split(":")[0];
                    int proxyPort = Integer.valueOf(proxyStr.split(":")[1]);

                    return new CustomProxy(prosyHost, proxyPort);
                }catch (Exception e){
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

            info("Inited proxies: " + proxies.size());

            inited = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Proxy getJavaNetRandomProxy(){
        if(!getCanReturnProxy()){
            return null;
        }

        return getRandomProxy().getJavaNetProxy();
    }

    public CustomProxy getRandomProxy(){
        int proxyCount = proxies.size();
        if(!getCanReturnProxy()){
            return null;
        }

        int index = randomGenerator.nextInt(proxyCount);

        CustomProxy proxy = proxies.get(index);

        return proxy;
    }

    public HttpHost getApacheRandomProxy(){
        if(!getCanReturnProxy()){
            return null;
        }

        return getRandomProxy().getApacheProxy();
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isNeedProxy() {
        return needProxy;
    }
    public void setNeedProxy(boolean needProxy) {
        this.needProxy = needProxy;
    }

    private boolean getCanReturnProxy(){
        return proxies.size() > 0 && needProxy && inited;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }
    public void setUpdateDelay(int updateDelay) {
        this.updateDelay = updateDelay;
    }

    private class CustomProxy{
        private String host;
        private int port;

        public CustomProxy(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }
        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }
        public void setPort(int port) {
            this.port = port;
        }

        public Proxy getJavaNetProxy(){
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getHost(), getPort()));
        }

        public HttpHost getApacheProxy(){
            return new HttpHost(getHost(), getPort());
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }
}