package com.kariaki.choice.Model;

public interface MessageType {

    int VIDEO=2;
    int TEXT=3;
    int IMAGE=4;
    int STATE_SEND =11;
    int STATE_RECEIVED =12;
    int REPLY_TEXT=15;
    int VOICE_NOTE=20;
    int PRIVATE_COMMENT_IMAGE_SINGLE =17;
    int PRIVATE_COMMENT_IMAGE_MERGED=19;
    int PRIVATE_COMMENT_TEXT=100;
    int CHAT_INVITE_REQUEST=1000;
    int GROUP_CHAT_FIRST_MESSAGE=150;
    int GROUP_CHAT_INVITE=6;

    int RECIEVED=200;

    int COMMENT_TEXT=6060;

    int COMMENT_VOICE_NOTE=6061;
    int COMMENT_REPLY=606;

    int REPLY=120;
    int TEXT_TEXT=121;
    int TEXT_VOICE_NOTE=122;
    int TEXT_IMAGE=123;
    int VOICE_NOTE_TEXT=124;
    int VOICE_NOTE_VOICE_NOTE=125;
    int VOICE_NOTE_IMAGE=126;
    int TEXT_VIDEO=127;
    int VOICE_NOTE_VIDEO=129;
    int HEADER=175;

    String contentSplit="<CH>";




    int getMessageType();
}
