package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;


public class ChatInviteRequestViewHolder extends ChatMainViewHolder {
    public ChatInviteRequestViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaing) {

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
