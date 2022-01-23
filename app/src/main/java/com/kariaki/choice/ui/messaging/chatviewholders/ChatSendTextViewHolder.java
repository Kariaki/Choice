package com.kariaki.choice.ui.messaging.chatviewholders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

public class ChatSendTextViewHolder extends ChatMainViewHolder {

    private EmojiTextView messageDisplay, replyContent;
    private TextView timeDisplay;
    private RelativeLayout replyBottomContainer, replyTopContainer;
    private ImageView send_state;

    public ChatSendTextViewHolder(@NonNull View itemView) {
        super(itemView);
        messageDisplay = itemView.findViewById(R.id.send_message_text);
        timeDisplay = itemView.findViewById(R.id.sendMessaageTextTime);
        //messageHolder = itemView.findViewById(R.id.messageHolder);
        send_state = itemView.findViewById(R.id.send_state);
        replyTopContainer = itemView.findViewById(R.id.replyTopContainer);
        replyContent = itemView.findViewById(R.id.replyContent);
        replyBottomContainer = itemView.findViewById(R.id.replyBottomContainer);
        messageDisplay.setEmojiSize(50);
        replyContent.setEllipsize(TextUtils.TruncateAt.END);


    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {
        Message message = (Message) type;

        String messageContent = message.getMessageCONTENT();

        onclickListener.sendMessageText(getAdapterPosition());

        onclickListener.messageIsSeen(getAdapterPosition(), send_state);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);

                return false;
            }
        });
        if (getTheme() == Theme.DEEP) {
            //top is bottom
            //recieveCommentTextMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            messageDisplay.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            replyContent.setTextColor(context.getResources().getColor(R.color.whiteGreen));

        }

        switch (message.getMessageTYPE()) {
            case MessageType.TEXT:
                replyTopContainer.setVisibility(View.GONE);
                break;
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT:

                replyTopContainer.setVisibility(View.VISIBLE);
                String[] newContent = messageContent.split(String.valueOf(message.getMessageTIME()));
                if (newContent.length < 2) {
                    messageContent = newContent[0];
                    replyContent.setText("unidentified type");
                } else {
                    messageContent = newContent[0];
                    replyContent.setText(newContent[1]);
                }

                break;
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT:

                replyTopContainer.setVisibility(View.VISIBLE);
                String[] voiceNoteContent = messageContent.split(String.valueOf(message.getMessageTIME()));
                if (voiceNoteContent.length < 2) {
                    messageContent = "unidentified type";
                    replyContent.setText("unidentified type");
                } else {
                    String duration = TimeFactor.convertMillieToHMmSs(Long.parseLong(voiceNoteContent[1]));
                    replyContent.setText(voiceNoteContent[1]);

                    messageContent = voiceNoteContent[0];
                    String text = "voice note | " + duration;
                    replyContent.setText(text);
                }

                break;

        }

        switch (message.getMessageState()) {

            case MessageState.SENT:
                send_state.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary));
                break;
            case MessageState.SENDING:

                send_state.setImageResource(R.drawable.check_white);
                break;
            case MessageState.SEEN:

                send_state.setBackgroundColor(R.color.send_comment_background);
                send_state.setVisibility(View.INVISIBLE);
                break;
            default:

                send_state.setImageResource(R.drawable.not_seen_check);
                break;

        }


        long timeStamp = message.getMessageTIME();

        timeDisplay.setText(TimeFactor.getDetailedDate(timeStamp, "HH:mm"));

        // timeDisplay.setVisibility(View.VISIBLE);

        // messageHolder.setBackground(context.getResources().getDrawable(R.drawable.send_message_background));
        String concat = messageContent;
        messageDisplay.setText(concat);

        // messageDisplay.setText(messageContent);

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
