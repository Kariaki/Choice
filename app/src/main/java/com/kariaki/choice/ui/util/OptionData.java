package com.kariaki.choice.ui.util;

public class OptionData {

    private String text;
    private int image;

    public String getText() {
        return text;
    }

    public int getImage() {
        return image;
    }


    public OptionData(String text, int image) {
        this.text = text;
        this.image = image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }
}
