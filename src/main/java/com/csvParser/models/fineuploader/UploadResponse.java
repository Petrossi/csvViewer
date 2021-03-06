package com.csvParser.models.fineuploader;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {

//    @JsonProperty("error")
    private String errorMsg;

    private boolean success;

    public UploadResponse(boolean success) {
        this.success = success;
    }

    public UploadResponse(boolean success, String errorMsg) {
        this.errorMsg = errorMsg;
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}