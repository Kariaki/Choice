package com.kariaki.choice.ui.post;

public class TextPostLastCheck {

    private long time;
    private long numberOfComments;

    public TextPostLastCheck(){

    }

    public TextPostLastCheck(long time, int numberOfComments) {
        this.time = time;
        this.numberOfComments = numberOfComments;
    }

    public long getTime() {
        return time;
    }

    public long getNumberOfComments() {
        return numberOfComments;
    }
}
