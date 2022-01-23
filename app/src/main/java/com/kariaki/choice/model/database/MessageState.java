package com.kariaki.choice.model.database;

public interface MessageState {

    int SENDING=0;
    int SENT=1;
    int SEEN=2;
    int RECEIVED=3;
    int FAILED=4;
    int COMMENT=-1;
    int HIDDEN=5;


}
