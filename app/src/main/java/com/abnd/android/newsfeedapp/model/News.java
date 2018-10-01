package com.abnd.android.newsfeedapp.model;

import android.graphics.Bitmap;

public class News {

    private String title;
    private String date;
    private String webUrl;
    private String section;
    private String pillar;
    private Bitmap thumbnail;
    private String author;

    public News(String title, String date, String webUrl, String section, String pillar, Bitmap thumbnail, String author) {
        this.title = title;
        this.date = date;
        this.webUrl = webUrl;
        this.section = section;
        this.pillar = pillar;
        this.thumbnail = thumbnail;
        this.author = author;
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

    public String getSection() {
        return section;
    }

    public String getPillar() {
        return pillar;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getAuthor() {
        return author;
    }
}
