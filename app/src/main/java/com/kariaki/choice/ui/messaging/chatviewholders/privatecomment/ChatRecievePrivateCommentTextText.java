package com.kariaki.choice.ui.messaging.chatviewholders.privatecomment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.messaging.chatviewholders.ChatMainViewHolder;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecievePrivateCommentTextText extends ChatMainViewHolder {

    CircleImageView recievePrivateCommentSenderImage;
    EmojiTextView receivePrivateCommentHeader, receivePrivateCommentCaption, recievePrivateCommentSenderName, recievePrivateCommentTextMessage;
    RelativeLayout PrivateCommentTextnameHolder, reciveCommentReplyContainter, recievePrivateCommentMessageContainer;
    TextView sendMessaageTextTime;
    private ImageView send_state;
    RelativeLayout bottomContainer;

    public ChatRecievePrivateCommentTextText(@NonNull View itemView) {
        super(itemView);
        EmojiManager.install(new IosEmojiProvider());
        recievePrivateCommentSenderImage = itemView.findViewById(R.id.recievePrivateCommentSenderImage);
        receivePrivateCommentHeader = itemView.findViewById(R.id.receivePrivateCommentHeader);
        receivePrivateCommentCaption = itemView.findViewById(R.id.receivePrivateCommentCaption);
        sendMessaageTextTime = itemView.findViewById(R.id.sendMessaageTextTime);
        send_state = itemView.findViewById(R.id.send_state);
        reciveCommentReplyContainter = itemView.findViewById(R.id.reciveCommentReplyContainter);
        bottomContainer = itemView.findViewById(R.id.bottomContainer);
        recievePrivateCommentMessageContainer = itemView.findViewById(R.id.recievePrivateCommentMessageContainer);
        recievePrivateCommentSenderName = itemView.findViewById(R.id.recievePrivateCommentSenderName);
        recievePrivateCommentTextMessage = itemView.findViewById(R.id.recievePrivateCommentTextMessage);
        PrivateCommentTextnameHolder = itemView.findViewById(R.id.PrivateCommentTextnameHolder);



    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener,
                         int currentlyPlaying) {




        if (getTheme() == Theme.DEEP) {
            //top is bottom
            recievePrivateCommentMessageContainer.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.recieve_message_text_dark));
            bottomContainer.setBackground(itemView.getContext().getDrawable(R.drawable.recieve_bottom_dark));
            //reciveCommentReplyContainter.setBackground(itemView.getContext().getDrawable(R.drawable.recieve_top_background_dark));
            //message.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            receivePrivateCommentHeader.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
            receivePrivateCommentCaption.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
            recievePrivateCommentTextMessage.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));

        }


        Message messsage = (Message) type;


        long timeStamp = messsage.getMessageTIME();

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        sendMessaageTextTime.setText(TimeFactor.getDetailedDate(timeStamp, "HH:mm"));

        onclickListener.setMessageToSeen(getAdapterPosition());
        if (onclickListener.showName()) {
            PrivateCommentTextnameHolder.setVisibility(View.VISIBLE);
            recievePrivateCommentSenderImage.setVisibility(View.VISIBLE);
        } else {
            PrivateCommentTextnameHolder.setVisibility(View.GONE);
            recievePrivateCommentSenderImage.setVisibility(View.GONE);
        }
        itemView.setOnClickListener(v -> {
            onclickListener.onClickPrivateCommentMessage(getAdapterPosition());
        });

        onclickListener.nameChoiceUser(getAdapterPosition(), recievePrivateCommentSenderName, recievePrivateCommentSenderImage);

        String header = "Your Post *";
        receivePrivateCommentHeader.setText(header);
        String[] messageDetail = messsage.getMessageCONTENT().split(String.valueOf(messsage.getMessageTIME()));
        String caption=messageDetail[1];
        receivePrivateCommentCaption.setText("hello hello");
        if(caption.isEmpty()){
            receivePrivateCommentCaption.setVisibility(View.GONE);

        }else {

            receivePrivateCommentCaption.setText(messageDetail[1]);
        }
        recievePrivateCommentTextMessage.setText(messageDetail[0]);
        String postReference = messageDetail[2];


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
