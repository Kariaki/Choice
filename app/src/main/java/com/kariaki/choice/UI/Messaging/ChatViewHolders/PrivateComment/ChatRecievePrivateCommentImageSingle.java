package com.kariaki.choice.UI.Messaging.ChatViewHolders.PrivateComment;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatMainViewHolder;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecievePrivateCommentImageSingle extends ChatMainViewHolder {

    ImageView recieveprivateCommentSinglePostImage;
    TextView recievePrivateCommentSenderName, sendMessaageTextTime;
    CircleImageView recievePrivateCommentSenderImage;

    EmojiTextView recievePrivateCommentTextMessage, receivePirvateCommentSingleimageCaption;
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private RelativeLayout PrivateCommentSingleImagenameHolder;
    RelativeLayout reciveCommentReplyContainter, bottomContainer, recievePrivateCommentMessageContainer;

    public ChatRecievePrivateCommentImageSingle(@NonNull View itemView) {
        super(itemView);
        EmojiManager.install(new IosEmojiProvider());
        recievePrivateCommentSenderName = itemView.findViewById(R.id.recievePrivateCommentSenderName);
        recievePrivateCommentTextMessage = itemView.findViewById(R.id.recievePrivateCommentTextMessage);
        recieveprivateCommentSinglePostImage = itemView.findViewById(R.id.recieveprivateCommentSinglePostImage);
        receivePirvateCommentSingleimageCaption = itemView.findViewById(R.id.receivePirvateCommentSingleimageCaption);
        recievePrivateCommentSenderImage = itemView.findViewById(R.id.recievePrivateCommentSenderImage);
        reciveCommentReplyContainter = itemView.findViewById(R.id.reciveCommentReplyContainter);
        recievePrivateCommentMessageContainer = itemView.findViewById(R.id.recievePrivateCommentMessageContainer);
        bottomContainer = itemView.findViewById(R.id.bottomContainer);
        PrivateCommentSingleImagenameHolder = itemView.findViewById(R.id.PrivateCommentSingleImagenameHolder);
        sendMessaageTextTime = itemView.findViewById(R.id.sendMessaageTextTime);
    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {

        if (getTheme() == Theme.DEEP) {
            //top is bottom
            recievePrivateCommentMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            reciveCommentReplyContainter.setBackground(context.getDrawable(R.drawable.recieve_top_background_dark));
            bottomContainer.setBackground(context.getDrawable(R.drawable.recieve_bottom_dark));
            recievePrivateCommentTextMessage.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            receivePirvateCommentSingleimageCaption.setTextColor(context.getResources().getColor(R.color.whiteGreen));

        }

        Message message = (Message) type;
        String[] details = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()));
        String caption = details[1];
        if (caption.isEmpty()) {
            receivePirvateCommentSingleimageCaption.setVisibility(View.GONE);
        } else {

            receivePirvateCommentSingleimageCaption.setText(details[1]);
        }
        recievePrivateCommentTextMessage.setText(details[0]);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });


        Glide.with(context).load(message.getMessageURL()).into(recieveprivateCommentSinglePostImage);

        long timestamp = message.getMessageTIME();
        String time = TimeFactor.getDetailedDate(timestamp, "HH:mm");
        sendMessaageTextTime.setText(time);
        onclickListener.setMessageToSeen(getAdapterPosition());

        itemView.setOnClickListener(v -> {
            onclickListener.onClickPrivateCommentMessage(getAdapterPosition());
        });

        if (onclickListener.showName()) {
            PrivateCommentSingleImagenameHolder.setVisibility(View.VISIBLE);
            recievePrivateCommentSenderImage.setVisibility(View.VISIBLE);
        } else {
            PrivateCommentSingleImagenameHolder.setVisibility(View.GONE);
            recievePrivateCommentSenderImage.setVisibility(View.GONE);
        }

        onclickListener.nameChoiceUser(getAdapterPosition(), recievePrivateCommentSenderName, recievePrivateCommentSenderImage);


        recievePrivateCommentSenderImage.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
