package com.kariaki.choice.ui.messaging.commentviewholders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.messaging.chatviewholders.ChatMainViewHolder;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentText extends ChatMainViewHolder {

    private CircleImageView senderImage;
    RelativeLayout repliedTop;
    EmojiTextView repliedText, commentText;
    private TextView replier, replyClick, senderName, commentTime;
    private LinearLayout repliedholder;

    public CommentText(@NonNull View itemView) {
        super(itemView);

        senderImage = itemView.findViewById(R.id.senderImage);
        repliedTop = itemView.findViewById(R.id.repliedTop);
        repliedText = itemView.findViewById(R.id.repliedText);
        senderName = itemView.findViewById(R.id.senderName);
        commentTime = itemView.findViewById(R.id.commentTime);
        commentText = itemView.findViewById(R.id.commentText);
        replyClick = itemView.findViewById(R.id.replyClick);
        replier = itemView.findViewById(R.id.replier);
        repliedholder=itemView.findViewById(R.id.repliedholder);

    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        Message message = (Message) type;
        int messageType = message.getMessageTYPE();

        if (getTheme() == Theme.DEEP) {
            commentText.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
            repliedText.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));

        }
        commentTime.setText(TimeFactor.getDetailedDate(message.getMessageTIME(), System.currentTimeMillis()));
        /*
        review replies will be remove until the comment reply interface have been completely finished

        ........
        /.........

         */
      //  Functions.reviewReplies(replyClick,onclickListener.replyCounter(getAdapterPosition()));
        String[] messageSplit = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));



        if(messageSplit.length>1){
            String messageText = messageSplit[0];
            repliedholder.setVisibility(View.VISIBLE);
            repliedTop.setVisibility(View.VISIBLE);
            String reply_messageID = messageSplit[3];
            String repliedtext = messageSplit[1];
            String replierID=messageSplit[2];
            commentText.setText(messageText);
            repliedText.setText(repliedtext);
            Functions.nameReplier(replier,replierID);


        }else {

            repliedholder.setVisibility(View.GONE);
            String text = message.getMessageCONTENT();
            repliedTop.setVisibility(View.GONE);
            commentText.setText(text);
        }

        switch (messageType) {
            case MessageType.COMMENT_TEXT:
                break;
            case MessageType.COMMENT_TEXT + MessageType.COMMENT_TEXT + MessageType.COMMENT_REPLY + MessageType.COMMENT_TEXT:


                break;

        }

        onclickListener.nameChoiceUser(getAdapterPosition(), senderName, senderImage);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return true;
            }
        });
        onclickListener.setMessageTime(getAdapterPosition(), commentTime);

        itemView.setOnClickListener(v->{
            onclickListener.onMessageClickListener(getAdapterPosition(),itemView);
        });
        replyClick.setOnClickListener(v->{
            onclickListener.onReplyClick(getAdapterPosition());
        });
        senderImage.setOnClickListener(v->{
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });
        senderName.setOnClickListener(v->{
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });
    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {


    }
}
