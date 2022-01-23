package com.kariaki.choice.ui.messaging.chatviewholders.privatecomment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.messaging.chatviewholders.ChatMainViewHolder;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class ChatSendPrivateCommentImageSingle extends ChatMainViewHolder {
    private EmojiTextView sendPrivateCommentHeader,
            sendPrivateCommentPostCaption,sendPrivateCommentTextMessage;
    private ImageView sendPrivateCommentImageUrl,send_state;
    TextView sendMessaageTextTime;
    private View VIEW;
    DatabaseReference users= FirebaseDatabase.getInstance().getReference("users");
    public ChatSendPrivateCommentImageSingle(@NonNull View itemView) {


        super(itemView);
        EmojiManager.install(new IosEmojiProvider());
        VIEW=itemView;
        sendPrivateCommentHeader=itemView.findViewById(R.id.sendPrivateImageCommentHeader);
        sendPrivateCommentPostCaption=itemView.findViewById(R.id.sendPrivateImageCommentPostCaption);
        sendPrivateCommentTextMessage=itemView.findViewById(R.id.sendPrivateImageCommentTextMessage);
        sendPrivateCommentImageUrl=itemView.findViewById(R.id.sendPrivateCommentImageUrl);
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

        long timeStamp=message.getMessageTIME();
        String time= TimeFactor.getDetailedDate(timeStamp,"HH:mm");
        sendMessaageTextTime.setText(time);
        onclickListener.messageIsSeen(getAdapterPosition(),send_state);

        onclickListener.messageIsSeen(getAdapterPosition(),send_state);

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



        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(),itemView);
                return false;
            }
        });

        itemView.setOnClickListener(v->{
            onclickListener.onClickPrivateCommentMessage(getAdapterPosition());
        });
        UserNaming.getUserName(postOwnerID,sendPrivateCommentHeader);


       Glide.with(context).load(message.getMessageURL()).into(sendPrivateCommentImageUrl);


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
