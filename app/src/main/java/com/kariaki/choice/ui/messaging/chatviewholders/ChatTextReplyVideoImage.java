package com.kariaki.choice.ui.messaging.chatviewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

public class ChatTextReplyVideoImage extends ChatMainViewHolder {
    ImageView repliedImagePreview;
    EmojiTextView send_message_text;
    TextView sendMessaageTextTime;

    ImageView send_state;
    public ChatTextReplyVideoImage(@NonNull View itemView) {
        super(itemView);
        send_message_text=itemView.findViewById(R.id.send_message_text);
        repliedImagePreview=itemView.findViewById(R.id.repliedImagePreview);
        sendMessaageTextTime=itemView.findViewById(R.id.sendMessaageTextTime);
         send_state=itemView.findViewById(R.id.send_state);

    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {
        Message message=(Message)type;
        String []contentArray=message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
        send_message_text.setText(contentArray[0]);
        String url=contentArray[1];
        Glide.with(context).load(url).into(repliedImagePreview);
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

        sendMessaageTextTime.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
