package com.smartit.talabia.expabdable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/4/19.
 */

public class EOAllCategoryPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("child")
    @Expose
    private List<EOChild> child = null;

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

    public List<EOChild> getChild() {
        return child;
    }

    public void setChild(List<EOChild> child) {
        this.child = child;
    }
}
