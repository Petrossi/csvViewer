package com.csvParser.services.pageFetcher;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResult {

    public int httpStatusCode;
    public String html;
    public String proxy = "";
    public String location;
    public String charsetStr = "";
    public String contentType = "";
    public String serviceType = "";
    public long contentLength;
    public List<String> headers = new ArrayList<>();

    public void setHeaders(HttpResponse response){
        headers = Arrays.stream(response.getAllHeaders())
                .map(header -> header.getName()+ " : " + header.getValue())
                .collect(Collectors.toList())
        ;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "httpStatusCode=" + httpStatusCode +
                ", html='" + html + '\'' +
                ", proxy='" + proxy + '\'' +
                ", location='" + location + '\'' +
                ", charsetStr='" + charsetStr + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                ", headers=" + headers +
                '}';
    }
}
