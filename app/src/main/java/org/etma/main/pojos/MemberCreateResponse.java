package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberCreateResponse {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("targetUrl")
    @Expose
    private String targetUrl;
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("unAuthorizedRequest")
    @Expose
    private boolean unAuthorizedRequest;
    @SerializedName("__abp")
    @Expose
    private boolean abp;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isUnAuthorizedRequest() {
        return unAuthorizedRequest;
    }

    public void setUnAuthorizedRequest(boolean unAuthorizedRequest) {
        this.unAuthorizedRequest = unAuthorizedRequest;
    }

    public boolean isAbp() {
        return abp;
    }

    public void setAbp(boolean abp) {
        this.abp = abp;
    }

}
