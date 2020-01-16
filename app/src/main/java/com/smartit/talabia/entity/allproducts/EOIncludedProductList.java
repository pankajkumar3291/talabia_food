package com.smartit.talabia.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOIncludedProductList implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Includedproducts")
    @Expose
    private Integer includedproducts;
    @SerializedName("productdetails")
    @Expose
    private EOIncludedProductDetails productdetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIncludedproducts() {
        return includedproducts;
    }

    public void setIncludedproducts(Integer includedproducts) {
        this.includedproducts = includedproducts;
    }

    public EOIncludedProductDetails getProductdetails() {
        return productdetails;
    }

    public void setProductdetails(EOIncludedProductDetails productdetails) {
        this.productdetails = productdetails;
    }
}
