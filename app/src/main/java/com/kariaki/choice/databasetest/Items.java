package com.kariaki.choice.databasetest;

public class Items {

    String url;
    String caption;

    public String getCaption() {
        return caption;
    }

    public Items() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Items(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }
}
