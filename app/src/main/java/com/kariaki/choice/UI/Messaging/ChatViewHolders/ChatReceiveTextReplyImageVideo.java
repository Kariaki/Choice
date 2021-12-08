package com.kariaki.choice.UI.Messaging.ChatViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatReceiveTextReplyImageVideo extends ChatMainViewHolder {

    private ImageView repliedImagePreview;
    private EmojiTextView recieveMessageMessage;
    private CircleImageView recieveMessageSenderImage;
    LinearLayout repliedholder;
    private TextView receiveMessaageTextTime, recieveCommentSenderName, replied;
    private RelativeLayout TextnameHolder;
    FrameLayout recieve_top_background;
    RelativeLayout replyTopContainer,recieveCommentTextMessageContainer;

    public ChatReceiveTextReplyImageVideo(@NonNull View itemView) {
        super(itemView);
        repliedImagePreview = itemView.findViewById(R.id.repliedImagePreview);
        receiveMessaageTextTime = itemView.findViewById(R.id.receiveMessaageTextTime);
        recieveMessageMessage = itemView.findViewById(R.id.recieveMessageMessage);
        recieveCommentSenderName = itemView.findViewById(R.id.recieveCommentSenderName);
        TextnameHolder = itemView.findViewById(R.id.TextnameHolder);
        recieve_top_background=itemView.findViewById(R.id.replyBottomContainer);
        replyTopContainer=itemView.findViewById(R.id.replyTopContainer);
        recieveCommentTextMessageContainer=itemView.findViewById(R.id.recieveCommentTextMessageContainer);

        replied = itemView.findViewById(R.id.replied);
        repliedholder = itemView.findViewById(R.id.repliedholder);
        recieveMessageSenderImage = itemView.findViewById(R.id.recieveMessageSenderImage);




    }

    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {
        if (getTheme() == Theme.DEEP) {
            //top is bottom
            recieveCommentTextMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            recieve_top_background.setBackground(context.getDrawable(R.drawable.recieve_top_background_dark));
            replyTopContainer.setBackground(context.getDrawable(R.drawable.recieve_bottom_dark));
            recieveMessageMessage.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            // replyContent.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
        }


        if (onclickListener.showName()) {
            TextnameHolder.setVisibility(View.VISIBLE);
            repliedholder.setVisibility(View.VISIBLE);
        } else {
            TextnameHolder.setVisibility(View.GONE);
            repliedholder.setVisibility(View.GONE);
        }

        Message theMessage = (Message) type;

        String[] contentArray = theMessage.getMessageCONTENT().split(String.valueOf(theMessage.getMessageTIME()));
        String url = contentArray[1];
        Glide.with(context).load(url).into(repliedImagePreview);
        recieveMessageMessage.setText(contentArray[0]);

        if (onclickListener.showName()) {
            String userID = contentArray[2];
            if(userID.equals(new UserInformation(context).getMainUserID())){
                replied.setText("You");
            }else {

                UserNaming.getUserName(userID, replied);
            }
            String repliedText = "replied " + replied.getText().toString();
            replied.setText(repliedText);
        }


        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);
                return false;
            }
        });

        long timeStamp = theMessage.getMessageTIME();

        long now = timeStamp;
        receiveMessaageTextTime.setText(TimeFactor.getDetailedDate(now, "HH:mm"));

        onclickListener.nameChoiceUser(getAdapterPosition(), recieveCommentSenderName, recieveMessageSenderImage);

        onclickListener.setMessageToSeen(getAdapterPosition());

        recieveMessageSenderImage.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });

    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }
}
