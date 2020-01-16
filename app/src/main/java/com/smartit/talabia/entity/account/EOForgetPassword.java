package com.smartit.talabia.entity.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 2/5/19.
 */

public class EOForgetPassword implements Serializable {

    @SerializedName("isSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("isError")
    @Expose
    private Boolean isError;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payLoad")
    @Expose
    private EOForgetPayload payLoad;

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EOForgetPayload getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(EOForgetPayload payLoad) {
        this.payLoad = payLoad;
    }
}
