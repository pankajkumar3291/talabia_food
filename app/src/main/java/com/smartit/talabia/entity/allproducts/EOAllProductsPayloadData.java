package com.smartit.talabia.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smartit.talabia.entity.dashboard.EOPrice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 25/4/19.
 */

public class EOAllProductsPayloadData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("featured_image")
    @Expose
    private String featuredImage;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
    @SerializedName("prices")
    @Expose
    private List<EOPrice> prices = null;

    //TODO added new key for checking dish and bundle
    @SerializedName("Product_type")
    @Expose
    private String productType;
    @SerializedName("includedproducts")
    @Expose
    private ArrayList<EOIncludedProductList> includedproducts;
    @SerializedName("Dish_price")
    @Expose
    private EODishBundlePrice dishPrice;
    @SerializedName("bunndl_price")
    @Expose
    private EODishBundlePrice bunndlPrice;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<EOPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<EOPrice> prices) {
        this.prices = prices;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public ArrayList<EOIncludedProductList> getIncludedproducts() {
        return includedproducts;
    }

    public void setIncludedproducts(ArrayList<EOIncludedProductList> includedproducts) {
        this.includedproducts = includedproducts;
    }

    public EODishBundlePrice getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(EODishBundlePrice dishPrice) {
        this.dishPrice = dishPrice;
    }

    public EODishBundlePrice getBunndlPrice() {
        return bunndlPrice;
    }

    public void setBunndlPrice(EODishBundlePrice bunndlPrice) {
        this.bunndlPrice = bunndlPrice;
    }
}
