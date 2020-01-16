package com.smartit.talabia.entity.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/4/19.
 */

public class EOProduct implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("featured_image")
    @Expose
    private String featuredImage;
    @SerializedName("Refundable")
    @Expose
    private Integer refundable;
    @SerializedName("price")
    @Expose
    private List<EOPrice> price = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public Integer getRefundable() {
        return refundable;
    }

    public void setRefundable(Integer refundable) {
        this.refundable = refundable;
    }

    public List<EOPrice> getPrice() {
        return price;
    }

    public void setPrice(List<EOPrice> price) {
        this.price = price;
    }

}
