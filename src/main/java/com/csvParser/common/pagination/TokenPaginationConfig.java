package com.csvParser.common.pagination;

import java.util.Map;

public class TokenPaginationConfig extends PaginationConfig {

    protected String token;

    public TokenPaginationConfig() { }

    public TokenPaginationConfig(Map<String, String> data){
        super(data);
        this.token = data.get("token") != null ? data.get("token") : "";
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UrlPaginationConfig{" +
                "sortBy='" + sortBy + '\'' +
                ", sortParam='" + sortParam + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", token=" + token +
                '}';
    }
}
