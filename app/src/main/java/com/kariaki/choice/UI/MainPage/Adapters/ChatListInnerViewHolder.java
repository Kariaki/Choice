package com.kariaki.choice.UI.MainPage.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChatList;
import com.kariaki.choice.Model.Database.Entities.MyChatChannel;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListInnerViewHolder extends ChatListMainViewHolder {

    CircleImageView profilePicture;
    TextView name, chatListNotificationCount, lastMessageTime;
    EmojiTextView last_message;
    RelativeLayout chatListNotificationCountHolder;
    ImageView isRecordingMic, messageState;
    TashieLoader isTypingLoader;
    DatabaseReference myChats = FirebaseDatabase.getInstance().getReference("users");

    public ChatListInnerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.chatListUserName);
        last_message = itemView.findViewById(R.id.chatListLastMessage);
        profilePicture = itemView.findViewById(R.id.chatListProfilePicture);
        isTypingLoader = itemView.findViewById(R.id.isTypingLoader);
        lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
        messageState = itemView.findViewById(R.id.messageState);
        chatListNotificationCountHolder = itemView.findViewById(R.id.chatListNotificationCountHolder);
        chatListNotificationCount = itemView.findViewById(R.id.chatListNotificationCount);

        isRecordingMic = itemView.findViewById(R.id.isRecordingMic);


    }

    @Override
    void listeners(ChatListAdapter.OnclickChatPersonListener listener, MyChatChannel order) {

       /* itemView.setOnClickListener(v -> {
            listener.OnclickChatItem(getAdapterPosition(), chatListNotificationCountHolder);
        });

        */

        listener.nameChoiceUser(getAdapterPosition(), profilePicture, name);
       // listener.getLastMessage(profilePicture,getLayoutPosition(),messageState,isRecordingMic,last_message,lastMessageTime,isTypingLoader);


    }


    DatabaseReference myChatWithThisPerson;
    private ChoiceViewModel viewModel;

    UserNaming naming;

    @Override
    void bind(MyChatChannel postTypes, FragmentActivity application, int color) {


        last_message = itemView.findViewById(R.id.chatListLastMessage);


        name.setTextColor(application.getApplicationContext().getResources().getColor(color));




    }


}

