package com.kariaki.choice.ui.Chat;

public class ChannelActivity {

    public ChannelActivity(){

    }
    private boolean typing,recording;

    public ChannelActivity(boolean typing, boolean recording) {
        this.typing = typing;
        this.recording = recording;
    }

    public boolean isTyping() {
        return typing;
    }

    public boolean isRecording() {
        return recording;
    }
}
