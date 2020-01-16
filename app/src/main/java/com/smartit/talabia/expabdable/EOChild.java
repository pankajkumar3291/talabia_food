package com.smartit.talabia.expabdable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOChild implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("grandchild")
    @Expose
    private List<EOGrandChild> grandchild = null;

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

    public List<EOGrandChild> getGrandchild() {
        return grandchild;
    }

    public void setGrandchild(List<EOGrandChild> grandchild) {
        this.grandchild = grandchild;
    }
}
