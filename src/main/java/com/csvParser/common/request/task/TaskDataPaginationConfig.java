package com.csvParser.common.request.task;

import com.csvParser.common.request.TokenPaginationConfig;

public class TaskDataPaginationConfig extends TokenPaginationConfig {

    protected String search;

    public TaskDataPaginationConfig() {
    }

    public TaskDataPaginationConfig(String sortBy, String sortParam, int page, int pageSize, String token, String search) {
        super(sortBy, sortParam, page, pageSize, token);
        this.search = search;
    }

    public String getSearch() {
        return search;
    }
    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "TaskDataPaginationConfig{" +
                "sortBy='" + sortBy + '\'' +
                ", token='" + token + '\'' +
                ", sortParam='" + sortParam + '\'' +
                ", search=" + search +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
