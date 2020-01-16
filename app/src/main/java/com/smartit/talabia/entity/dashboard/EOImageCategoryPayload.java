package com.smartit.talabia.entity.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/4/19.
 */

public class EOImageCategoryPayload implements Serializable {

    @SerializedName("allcategory")
    @Expose
    private List<EOImageCategory> allcategory = null;

    public List<EOImageCategory> getAllcategory() {
        return allcategory;
    }

    public void setAllcategory(List<EOImageCategory> allcategory) {
        this.allcategory = allcategory;
    }
}
