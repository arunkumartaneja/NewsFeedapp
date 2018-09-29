package com.abnd.android.newsfeedapp.model;

import android.graphics.Bitmap;

public class News {

    private String title;
    private String date;
    private String webUrl;
    private String section;
    private String pillar;
    private Bitmap thumbnail;

    public News(String title, String date, String webUrl, String section, String pillar, Bitmap thumbnail) {
        this.title = title;
        this.date = date;
        this.webUrl = webUrl;
        this.section = section;
        this.pillar = pillar;
        this.thumbnail = thumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
