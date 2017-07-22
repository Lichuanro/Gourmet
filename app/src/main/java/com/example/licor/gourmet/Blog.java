package com.example.licor.gourmet;

import java.io.Serializable;

/**
 * Created by Licor on 2017/6/3.
 */

public class Blog implements Serializable {
    private String title;
    private String content;
    private String imageUrl;
    private String detailImg;
    private String url;



    public Blog(String title, String content, String imageUrl,String detailImg,String url){
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.detailImg = detailImg;
        this.url = url;

    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {return url;}

    public String getDetailImg() {return detailImg;}

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDetailImg(String detailImg) {this.detailImg = detailImg;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {this.url = url;}

}