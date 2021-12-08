package com.kariaki.choice.UI.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;

public class GroupChatFirstMessageViewHolder extends ChatMainViewHolder {
    private TextView newGroupChatFirstMessage;
    public GroupChatFirstMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        newGroupChatFirstMessage=itemView.findViewById(R.id.newGroupChatFirstMessage);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {
        Message message=(Message)type;

        newGroupChatFirstMessage.setText(message.getMessageCONTENT());

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
