package com.csvParser.common.pagination;

public abstract class PaginationConfig {

    protected String sortBy = "asc";
    protected String sortParam = "";
    protected int page = 1;
    protected int pageSize = 10;

    public PaginationConfig() {
    }

    public PaginationConfig(String sortBy, String sortParam, int page, int pageSize) {
        this.sortBy = sortBy;
        this.sortParam = sortParam;
        this.page = page;
        this.pageSize = pageSize;
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