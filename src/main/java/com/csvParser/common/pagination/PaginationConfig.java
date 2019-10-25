package com.csvParser.common.pagination;

import java.util.Map;

public abstract class PaginationConfig {

    protected String sortBy = "asc";
    protected String sortParam = "";
    protected int page = 1;
    protected int pageSize = 10;

    public PaginationConfig() {
    }

    public PaginationConfig(Map<String, String> data) {
        this.sortBy = data.containsKey("sortBy") && data.get("sortBy").equals("asc") ? "asc": data.get("sortBy");
        this.sortParam = data.getOrDefault("sortParam", "");
        this.page = data.containsKey("page") ? Integer.parseInt(data.get("page")) : 1;
        setPageSize(data.containsKey("pageSize") ? Integer.parseInt(data.get("pageSize")) : -1);
    }

    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortParam() {
        return sortParam;
    }
    public void setSortParam(String sortParam) {
        this.sortParam = sortParam;
    }

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 200 ? pageSize : 200;
    }
}