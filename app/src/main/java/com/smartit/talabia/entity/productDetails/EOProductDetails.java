package com.smartit.talabia.entity.productDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOProductDetails implements Serializable {

    @SerializedName("isSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("isError")
    @Expose
    private Boolean isError;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EOProductDetailsPayload payload;

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

    public EOProductDetailsPayload getPayload() {
        return payload;
    }

    public void setPayload(EOProductDetailsPayload payload) {
        this.payload = payload;
    }
}
