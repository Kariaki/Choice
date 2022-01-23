package com.kariaki.choice.ui.messaging.chatviewholders;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.messaging.adapter.ChatAdapter;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecieveTextViewHolder extends ChatMainViewHolder {

    private CircleImageView profile;
    private EmojiTextView message, replyContent;
    private TextView name, time, replied;
    private static final String LIST_OF_USERS = "users";
    private RelativeLayout TextnameHolder, recieveCommentTextMessageContainer;
    private LinearLayout repliedholder;

    private DatabaseReference userList = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);


    public ChatRecieveTextViewHolder(@NonNull View itemView) {
        super(itemView);
        EmojiManager.install(new IosEmojiProvider());

        profile = itemView.findViewById(R.id.recieveMessageSenderImage);
        repliedholder = itemView.findViewById(R.id.repliedholder);
        replied = itemView.findViewById(R.id.replied);
        name = itemView.findViewById(R.id.recieveCommentSenderName);
        replyContent = itemView.findViewById(R.id.replyContent);
        message = itemView.findViewById(R.id.recieveMessageMessage);
        recieveCommentTextMessageContainer = itemView.findViewById(R.id.recieveCommentTextMessageContainer);
        time = itemView.findViewById(R.id.receiveMessaageTextTime);
        TextnameHolder = itemView.findViewById(R.id.TextnameHolder);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        if (onclickListener.showName()) {
            TextnameHolder.setVisibility(View.VISIBLE);
            profile.setVisibility(View.VISIBLE);
        } else {
            TextnameHolder.setVisibility(View.GONE);
            profile.setVisibility(View.GONE);

        }

        if (getTheme() == Theme.DEEP) {
            //top is bottom
          /*  recieveCommentTextMessageContainer.setBackground(context.getDrawable(R.drawable.recieve_message_text_dark));
            replyBottomContainer.setBackground(context.getDrawable(R.drawable.recieve_top_background_dark));
            replyTopContainer.setBackground(context.getDrawable(R.drawable.recieve_bottom_dark));

           */
            message.setTextColor(context.getResources().getColor(R.color.whiteGreen));
            //replyContent.setTextColor(context.getResources().getColor(R.color.whiteGreen));

        }


        Message theMessage = (Message) type;
        String messageContent = theMessage.getMessageCONTENT();

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                onclickListener.onLongClickMessage(getAdapterPosition(), itemView);

                return false;
            }
        });
        switch (theMessage.getMessageTYPE()) {
            case MessageType.TEXT:
                repliedholder.setVisibility(View.GONE);
                break;
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:

                repliedholder.setVisibility(View.VISIBLE);
                String[] newContent = messageContent.split(String.valueOf(theMessage.getMessageTIME()));
                if (newContent.length < 2) {
                    messageContent = "unidentified type";
                    replyContent.setText("unidentified type");
                } else {
                    messageContent = newContent[0];
                    replyContent.setText(newContent[1]);
                }
                if (onclickListener.showName()) {
                    repliedholder.setVisibility(View.VISIBLE);
                    String userID = newContent[2];
                    if (userID.equals(new UserInformation(context).getMainUserID())) {
                        replied.setText("You");
                    } else {

                        UserNaming.getUserName(userID, replied);
                    }
                    String repliedText = "replied " + replied.getText().toString();
                    replied.setText(repliedText);
                }

                //name user that was replied

                break;
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:

                repliedholder.setVisibility(View.VISIBLE);

                String[] voiceNoteContent = messageContent.split(String.valueOf(theMessage.getMessageTIME()));
                String duration = TimeFactor.convertMillieToHMmSs(Long.parseLong(voiceNoteContent[1]));
                messageContent = voiceNoteContent[0];
                String text = "voice note | " + duration;
                replyContent.setText(text);
                //name user that was replied
                if (onclickListener.showName()) {
                    String userID = voiceNoteContent[2];
                    repliedholder.setVisibility(View.VISIBLE);

                    if (userID.equals(new UserInformation(context).getMainUserID())) {
                        replied.setText("You");
                    } else {

                        UserNaming.getUserName(userID, replied);

                    }
                    String repliedText = "replied " + replied.getText().toString();
                    replied.setText(repliedText);
                }


                break;

        }

        message.setText(messageContent);


        long timeStamp = theMessage.getMessageTIME();

        long now = TimeFactor.now2(timeStamp);
        time.setText(TimeFactor.getDetailedDate(now, "HH:mm"));

        onclickListener.nameChoiceUser(getAdapterPosition(), name, profile);

        onclickListener.setMessageToSeen(getAdapterPosition());

        profile.setOnClickListener(v -> {
            onclickListener.OnclickChatProfileImage(getAdapterPosition());
        });


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }

}
