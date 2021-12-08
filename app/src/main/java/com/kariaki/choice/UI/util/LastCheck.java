package com.kariaki.choice.UI.util;

public class LastCheck {
    private long time;
    private long numberOfComments;

    public LastCheck(){

    }

    public LastCheck(long time, int numberOfComments) {
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
