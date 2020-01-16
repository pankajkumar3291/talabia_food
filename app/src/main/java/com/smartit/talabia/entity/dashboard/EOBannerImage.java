package com.smartit.talabia.entity.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/4/19.
 */

public class EOBannerImage implements Serializable {

    @SerializedName("image")
    @Expose
    private List <String> image = null;

    public List <String> getImage() {
        return image;
    }

    public void setImage(List <String> image) {
        this.image = image;
    }
}
