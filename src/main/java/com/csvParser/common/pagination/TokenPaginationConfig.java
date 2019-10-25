package com.csvParser.common.pagination;

public class TokenPaginationConfig extends PaginationConfig {

    protected String token;

    public TokenPaginationConfig() {
    }

    public TokenPaginationConfig(String sortBy, String sortParam, int page, int pageSize, String token) {
        super(sortBy, sortParam, page, pageSize);
        this.token = token;
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