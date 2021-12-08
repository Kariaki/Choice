package com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatInvite;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatMainViewHolder;


public class GroupChatInvite extends ChatMainViewHolder {

    TextView groupInviteGroupChatName,groupInviteAccept;
    public GroupChatInvite(@NonNull View itemView) {
        super(itemView);
        groupInviteAccept=itemView.findViewById(R.id.groupInviteAccept);
        groupInviteGroupChatName=itemView.findViewById(R.id.groupInviteGroupChatName);

    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
