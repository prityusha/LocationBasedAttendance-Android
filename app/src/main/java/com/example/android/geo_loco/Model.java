package com.example.android.geo_loco;

public class Model {
    private String imgUrl;

    public Model(){

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Model(String imgUrl){
        this.imgUrl = imgUrl;
    }
}
