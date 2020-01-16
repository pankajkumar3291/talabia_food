package com.smartit.talabia.entity.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/4/19.
 */

public class EODashboardCategoryPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("collection_name")
    @Expose
    private String collectionName;
    @SerializedName("products")
    @Expose
    private List<EOProduct> products = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<EOProduct> getProducts() {
        return products;
    }

    public void setProducts(List<EOProduct> products) {
        this.products = products;
    }

}
