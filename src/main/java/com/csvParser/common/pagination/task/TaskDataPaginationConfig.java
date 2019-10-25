package com.csvParser.common.pagination.task;

import com.csvParser.common.pagination.TokenPaginationConfig;

public class TaskDataPaginationConfig extends TokenPaginationConfig {

    public TaskDataPaginationConfig() {
    }

    public TaskDataPaginationConfig(String sortBy, String sortParam, int page, int pageSize, String token) {
        super(sortBy, sortParam, page, pageSize, token);
    }

    @Override
    public String toString() {
        return "TaskDataPaginationConfig{" +
                "sortBy='" + sortBy + '\'' +
                ", token='" + token + '\'' +
                ", sortParam='" + sortParam + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
