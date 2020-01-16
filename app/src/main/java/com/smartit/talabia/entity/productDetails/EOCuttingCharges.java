package com.smartit.talabia.entity.productDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOCuttingCharges implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("cutting_changes")
    @Expose
    private Double cuttingChanges;

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

    public Double getCuttingChanges() {
        return cuttingChanges;
    }

    public void setCuttingChanges(Double cuttingChanges) {
        this.cuttingChanges = cuttingChanges;
    }
}
