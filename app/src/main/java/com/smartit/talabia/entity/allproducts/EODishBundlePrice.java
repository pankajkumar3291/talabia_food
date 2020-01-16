package com.smartit.talabia.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EODishBundlePrice implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
    @SerializedName("regular_price")
    @Expose
    private double regularPrice;
    @SerializedName("discount")
    @Expose
    private double discount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(double regularPrice) {
        this.regularPrice = regularPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
