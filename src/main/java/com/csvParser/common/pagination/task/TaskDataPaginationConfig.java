package com.csvParser.common.pagination.task;

import com.csvParser.common.pagination.TokenPaginationConfig;

public class TaskDataPaginationConfig extends TokenPaginationConfig {

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
