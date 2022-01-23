package com.kariaki.choice.ui.post;

public class PostInfo {


        private long postLifeSpan;
        private boolean postIsOnRepeat;
        private boolean allowPrivateComment;
        public PostInfo(long postLifeSpan, boolean postIsOnRepeat, boolean allowPrivateComment) {
            this.postLifeSpan=postLifeSpan;
            this.postIsOnRepeat=postIsOnRepeat;
            this.allowPrivateComment=allowPrivateComment;
        }
        public PostInfo(){

        }

    public void setPostLifeSpan(long postLifeSpan) {
        this.postLifeSpan = postLifeSpan;
    }

    public void setAllowPrivateComment(boolean allowPrivateComment) {
        this.allowPrivateComment = allowPrivateComment;
    }

    public boolean isAllowPrivateComment() {
        return allowPrivateComment;
    }

    public long getPostLifeSpan() {
            return postLifeSpan;
        }
        public boolean getPostIsOnRepeat(){
            return postIsOnRepeat;
        }



}
