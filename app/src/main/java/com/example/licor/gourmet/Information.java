package com.example.licor.gourmet;

import java.io.Serializable;

/**
 * Created by Licor on 2017/5/30.
 */

public class Information implements Serializable {
    private String name;
    private String photoUrl;
    private String description;

    public Information(String name, String photoUrl, String description) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUri(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
