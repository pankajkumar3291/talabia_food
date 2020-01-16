package com.smartit.talabia.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smartit.talabia.entity.dashboard.EOPrice;

import java.io.Serializable;
import java.util.List;

public class EOIncludedProductDetails implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Product_type")
    @Expose
    private String productType;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("featured_image")
    @Expose
    private String featuredImage;
    @SerializedName("Refundable")
    @Expose
    private Integer refundable;
    @SerializedName("prices")
    @Expose
    private List<EOPrice> prices = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

    public List<EOPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<EOPrice> prices) {
        this.prices = prices;
    }

}
