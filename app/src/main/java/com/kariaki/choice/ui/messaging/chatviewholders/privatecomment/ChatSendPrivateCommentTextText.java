package com.kariaki.choice.ui.messaging.chatviewholders.privatecomment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.messaging.chatviewholders.ChatMainViewHolder;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

public class ChatSendPrivateCommentTextText extends ChatMainViewHolder {

    private EmojiTextView sendPrivateCommentHeader,
            sendPrivateCommentPostCaption,sendPrivateCommentTextMessage;
    private TextView sendMessaageTextTime;
    private ImageView send_state;
    public ChatSendPrivateCommentTextText(@NonNull View itemView) {
        super(itemView);
        sendPrivateCommentHeader=itemView.findViewById(R.id.sendPrivateCommentHeader);
        sendPrivateCommentPostCaption=itemView.findViewById(R.id.sendPrivateCommentPostCaption);
        sendPrivateCommentTextMessage=itemView.findViewById(R.id.sendPrivateCommentTextMessage);
        send_state=itemView.findViewById(R.id.send_state);
        sendMessaageTextTime=itemView.findViewById(R.id.sendMessaageTextTime);

    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {

        Message message=(Message)type;
        String[]details=message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
        sendPrivateCommentPostCaption.setText(details[1]);
        sendPrivateCommentTextMessage.setText(details[0]);
        String postOwnerID=details[3];
        UserNaming.getUserName(postOwnerID,sendPrivateCommentHeader);
        String header=sendPrivateCommentHeader.getText().toString()+ " post";
        sendPrivateCommentHeader.setText(header);


        onclickListener.messageIsSeen(getAdapterPosition(),send_state);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(),itemView);
                return false;
            }
        });

        switch (message.getMessageState()) {

            case MessageState.SENT:
                send_state.setImageResource(R.drawable.not_seen_check);

                break;
            case MessageState.SENDING:
                send_state.setImageResource(R.drawable.check_white);
                break;
            case MessageState.SEEN:
                send_state.setImageResource(R.drawable.check);
                break;
            default:
                send_state.setImageResource(R.drawable.uncheck_white);
                break;

        }

        long timeStamp = message.getMessageTIME();
        sendMessaageTextTime.setText(TimeFactor.getDetailedDate(timeStamp, "HH:mm"));


        itemView.setOnClickListener(v->{
            onclickListener.onClickPrivateCommentMessage(getAdapterPosition());
        });

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
