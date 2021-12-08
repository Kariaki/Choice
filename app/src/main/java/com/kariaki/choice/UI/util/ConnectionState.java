package com.kariaki.choice.UI.util;

public class ConnectionState {

    public ConnectionState(){

    }
    private long lastSeen;
    private boolean online;

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public ConnectionState(long lastSeen, boolean online) {
        this.lastSeen = lastSeen;
        this.online = online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public long getLastSeen() {
        return lastSeen;
    }
}
