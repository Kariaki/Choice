package com.kariaki.choice.ui.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Time.TimeFactor;

public class ChatMessageListHeader extends ChatMainViewHolder {

    TextView messageDate;
    public ChatMessageListHeader(@NonNull View itemView) {
        super(itemView);
        messageDate=itemView.findViewById(R.id.messageDate);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        Message message=(Message)type;
        long currentTime=System.currentTimeMillis();
        long messageTime=message.getMessageTIME();
        String date= TimeFactor.getDetailedDate(messageTime,currentTime);
        messageDate.setText(date);
    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
