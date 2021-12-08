package com.kariaki.choice.UI.Settings.Entities;

public class PostSettings {


    private boolean allowPrivateComment;

    private boolean alwaysRepeat;

    public PostSettings(boolean allowPrivateComment, boolean alwaysRepeat) {

        this.allowPrivateComment = allowPrivateComment;
        this.alwaysRepeat = alwaysRepeat;

    }

    public PostSettings(boolean allowPrivateComment){

    }
    public PostSettings(){

    }

    public void setAlwaysRepeat(boolean alwaysRepeat) {
        this.alwaysRepeat = alwaysRepeat;
    }

    public boolean isAlwaysRepeat() {
        return alwaysRepeat;
    }

    public boolean isAllowPrivateComment() {
        return allowPrivateComment;
    }

    public void setAllowPrivateComment(boolean allowPrivateComment) {
        this.allowPrivateComment = allowPrivateComment;
    }

}
