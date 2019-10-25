package com.csvParser.common.response;

import org.json.JSONArray;

public class TaskDataResponse extends BasicResponse {

    private JSONArray summary = new JSONArray();

    public TaskDataResponse(boolean success, String message, JSONArray summary,  int totalCount, int filteredCount, int pages) {
        super(success, message);
        this.totalCount = totalCount;
        this.filteredCount = filteredCount;
        this.pages = pages;
        this.summary = summary;
    }

    public TaskDataResponse(boolean success, String message) {
        super(success, message);
    }

    public int totalCount;
    public int filteredCount;
    public int pages;

    @Override
    public String toString() {
        return "DomainWeightResponse{" +
            "success=" + success +
            ", message='" + message + '\'' +
            ", summary=" + summary +
            ", totalCount=" + totalCount +
            ", filteredCount=" + filteredCount +
            ", pages=" + pages +
        '}';
    }
}