package com.csvParser.common.response;

public class BasicResponse {

    public static String SUCCESS_MESSAGE = "success";

    public static final boolean SUCCESS_TRUE = true;
    public static final boolean SUCCESS_FALSE = false;

    public boolean success = false;
    public String message = "";

    public BasicResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BasicResponse(boolean success) {
        this.success = success;
    }
    public BasicResponse() {}
}
