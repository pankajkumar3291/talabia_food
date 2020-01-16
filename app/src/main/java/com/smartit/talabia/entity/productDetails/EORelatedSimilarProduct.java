package com.smartit.talabia.entity.productDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smartit.talabia.entity.allproducts.EOIncludedProductDetails;

import java.io.Serializable;

public class EORelatedSimilarProduct implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("similarprods")
    @Expose
    private Integer similarprods;
    @SerializedName("productdetails")
    @Expose
    private EOIncludedProductDetails productdetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSimilarprods() {
        return similarprods;
    }

    public void setSimilarprods(Integer similarprods) {
        this.similarprods = similarprods;
    }

    public EOIncludedProductDetails getProductdetails() {
        return productdetails;
    }

    public void setProductdetails(EOIncludedProductDetails productdetails) {
        this.productdetails = productdetails;
    }

}
