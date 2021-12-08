package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;

public class ChatDefaultViewHolder extends ChatMainViewHolder {
    public ChatDefaultViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
