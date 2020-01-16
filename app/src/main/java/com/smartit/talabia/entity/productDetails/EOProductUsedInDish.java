package com.smartit.talabia.entity.productDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smartit.talabia.entity.allproducts.EOIncludedProductDetails;

import java.io.Serializable;

public class EOProductUsedInDish implements Serializable {

    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("productdetails")
    @Expose
    private EOIncludedProductDetails productdetails;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public EOIncludedProductDetails getProductdetails() {
        return productdetails;
    }

    public void setProductdetails(EOIncludedProductDetails productdetails) {
        this.productdetails = productdetails;
    }

}
