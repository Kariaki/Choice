package com.kariaki.choice.UI.MainPage.Adapters;

import android.text.TextUtils;
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
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListGroupChatViewHolder extends ChatListMainViewHolder {

    CircleImageView profilePicture,userMessagePicture;
    TextView name, chatListNotificationCount;
    EmojiTextView last_message;
    RelativeLayout chatListNotificationCountHolder;
    ImageView messageState;
    DatabaseReference myChats = FirebaseDatabase.getInstance().getReference("users");
    TextView lastMessageTime;
    TashieLoader isTypingLoader;
    ImageView isRecordingMic;
    ImageView newMessageIndicator;
    RelativeLayout rootView;

    public ChatListGroupChatViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.chatListUserName);
        last_message = itemView.findViewById(R.id.chatListLastMessage);
        last_message.setEllipsize(TextUtils.TruncateAt.END);

        profilePicture = itemView.findViewById(R.id.chatListProfilePicture);
        chatListNotificationCountHolder = itemView.findViewById(R.id.chatListNotificationCountHolder);
        chatListNotificationCount = itemView.findViewById(R.id.chatListNotificationCount);
        lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
        messageState = itemView.findViewById(R.id.messageState);
        isRecordingMic = itemView.findViewById(R.id.isRecordingMic);
        newMessageIndicator=itemView.findViewById(R.id.newMessageIndicator);
        userMessagePicture=itemView.findViewById(R.id.userMessagePicture);
        rootView=itemView.findViewById(R.id.rootView);


        isTypingLoader = itemView.findViewById(R.id.isTypingLoader);

    }


    @Override
    void bind(MyChatChannel channelID, FragmentActivity application, int color) {

        last_message = itemView.findViewById(R.id.chatListLastMessage);

        UserInformation information = new UserInformation(itemView.getContext().getApplicationContext());

        name.setTextColor(application.getResources().getColor(color));
        viewModel = ChoiceViewModel.getInstance(application.getApplication());



        myChatWithThisPerson = FirebaseDatabase.getInstance().getReference("users");
        //.child(information.getMainUserID());
        naming = UserNaming.getInstance();


    }


    DatabaseReference myChatWithThisPerson;
    private ChoiceViewModel viewModel;

    UserNaming naming;

    @Override
    void listeners(ChatListAdapter.OnclickChatPersonListener listener, MyChatChannel order) {
        itemView.setOnClickListener(v -> {
            listener.OnclickChatItem(getAdapterPosition(), chatListNotificationCountHolder,name,profilePicture);
        });

        listener.order(getAdapterPosition());
        listener.getLastMessage(profilePicture,getAdapterPosition(),messageState,isRecordingMic,last_message,
                lastMessageTime,isTypingLoader,userMessagePicture,newMessageIndicator);

        listener.notification(getAdapterPosition(),chatListNotificationCountHolder,chatListNotificationCount,rootView);
        if(order.getChannelType()== ChannelTypes.GROUP_CHAT) {
            listener.nameGroupChat(getAdapterPosition(), profilePicture, name);
        }else {
            listener.nameChoiceUser(getAdapterPosition(), profilePicture, name);
            listener.chatActivity(getAdapterPosition(),isTypingLoader,isRecordingMic,messageState,last_message);
        }





    }

}
