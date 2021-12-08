package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;

public abstract class ChatMainViewHolder extends RecyclerView.ViewHolder {
    public ChatMainViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    int theme;

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getTheme() {
        return theme;
    }

    public abstract void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying);

    public abstract void progressChange(ChatAdapter.onProgressChangeListener listener);

}
