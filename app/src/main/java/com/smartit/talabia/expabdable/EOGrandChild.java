package com.smartit.talabia.expabdable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOGrandChild implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cat_name")
    @Expose
    private String catName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
