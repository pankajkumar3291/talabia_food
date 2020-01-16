package com.smartit.talabia.entity.productDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smartit.talabia.entity.allproducts.EODishBundlePrice;
import com.smartit.talabia.entity.dashboard.EOPrice;

import java.io.Serializable;
import java.util.List;

public class EOProductDetailsPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("Product_type")
    @Expose
    private String productType;
    @SerializedName("featured_image")
    @Expose
    private String featuredImage;
    @SerializedName("Refundable")
    @Expose
    private Integer refundable;
    @SerializedName("includedproducts")
    @Expose
    private List<EORelatedSimilarProduct> includedproducts = null;
    @SerializedName("Dish_price")
    @Expose
    private EODishBundlePrice dishPrice;
    @SerializedName("prices")
    @Expose
    private List<EOPrice> prices = null;
    @SerializedName("bunndl_price")
    @Expose
    private EODishBundlePrice bunndlPrice;
    @SerializedName("ProductUsedInDishes")
    @Expose
    private List<EORelatedSimilarProduct> productUsedInDishes = null;
    @SerializedName("Relatedproducts")
    @Expose
    private List<EORelatedSimilarProduct> relatedproducts = null;
    @SerializedName("Similarproducts")
    @Expose
    private List<EORelatedSimilarProduct> similarproducts = null;
    @SerializedName("cuttingcharges")
    @Expose
    private EOCuttingCharges cuttingcharges;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("how_to_prepare")
    @Expose
    private String howToPrepare;
    @SerializedName("how_to_prepare_video")
    @Expose
    private String howToPrepareVideo;
    @SerializedName("recipe_ideas")
    @Expose
    private String recipeIdeas;
    @SerializedName("nutritional_fact")
    @Expose
    private String nutritionalFact;

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

    public List<EORelatedSimilarProduct> getIncludedproducts() {
        return includedproducts;
    }

    public void setIncludedproducts(List<EORelatedSimilarProduct> includedproducts) {
        this.includedproducts = includedproducts;
    }

    public EODishBundlePrice getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(EODishBundlePrice dishPrice) {
        this.dishPrice = dishPrice;
    }

    public List<EOPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<EOPrice> prices) {
        this.prices = prices;
    }

    public EODishBundlePrice getBunndlPrice() {
        return bunndlPrice;
    }

    public void setBunndlPrice(EODishBundlePrice bunndlPrice) {
        this.bunndlPrice = bunndlPrice;
    }

    public List<EORelatedSimilarProduct> getProductUsedInDishes() {
        return productUsedInDishes;
    }

    public void setProductUsedInDishes(List<EORelatedSimilarProduct> productUsedInDishes) {
        this.productUsedInDishes = productUsedInDishes;
    }

    public List<EORelatedSimilarProduct> getRelatedproducts() {
        return relatedproducts;
    }

    public void setRelatedproducts(List<EORelatedSimilarProduct> relatedproducts) {
        this.relatedproducts = relatedproducts;
    }

    public List<EORelatedSimilarProduct> getSimilarproducts() {
        return similarproducts;
    }

    public void setSimilarproducts(List<EORelatedSimilarProduct> similarproducts) {
        this.similarproducts = similarproducts;
    }

    public EOCuttingCharges getCuttingcharges() {
        return cuttingcharges;
    }

    public void setCuttingcharges(EOCuttingCharges cuttingcharges) {
        this.cuttingcharges = cuttingcharges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHowToPrepare() {
        return howToPrepare;
    }

    public void setHowToPrepare(String howToPrepare) {
        this.howToPrepare = howToPrepare;
    }

    public String getHowToPrepareVideo() {
        return howToPrepareVideo;
    }

    public void setHowToPrepareVideo(String howToPrepareVideo) {
        this.howToPrepareVideo = howToPrepareVideo;
    }

    public String getRecipeIdeas() {
        return recipeIdeas;
    }

    public void setRecipeIdeas(String recipeIdeas) {
        this.recipeIdeas = recipeIdeas;
    }

    public String getNutritionalFact() {
        return nutritionalFact;
    }

    public void setNutritionalFact(String nutritionalFact) {
        this.nutritionalFact = nutritionalFact;
    }
}
