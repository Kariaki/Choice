package com.kariaki.choice.UI.MainPage.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Choice;
import com.kariaki.choice.MainActivity;
import com.kariaki.choice.Model.Database.ChoiceDatabase;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChatList;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.CloudMessage;
import com.kariaki.choice.Model.Database.Entities.Contact;
import com.kariaki.choice.Model.Database.Entities.GroupChatInformation;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.Database.Entities.MyChatChannel;
import com.kariaki.choice.Model.Database.MessageState;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelActivity;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.Chat.Channels;
import com.kariaki.choice.UI.Chat.ChatPage;
import com.kariaki.choice.UI.Chat.ChatPageBottomFragment;
import com.kariaki.choice.UI.Chat.ChatVideoViewer;
import com.kariaki.choice.UI.MainPage.Adapters.ChatListAdapter;
import com.kariaki.choice.UI.MainPage.BottomFragments.BottomContact;
import com.kariaki.choice.UI.MainPage.ChatFrament;
import com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatSendVoiceNoteViewHolder;
import com.kariaki.choice.UI.Messaging.CommentViewHolders.CommentText;
import com.kariaki.choice.UI.Post.Viewer.PostImageViewer;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    public Chat() {
        // Required empty public constructor
    }

    public interface OpenPageListener {

        void openPage(MyChatChannel chatChannel, Drawable drawable, String chatName);


    }

    public OpenPageListener OnOpenPageListener;

    public void setOnOpenPageListener(OpenPageListener onOpenPageListener) {

        OnOpenPageListener = onOpenPageListener;

    }

    List<MyChatChannel> people = new ArrayList<>();
    private ChatListAdapter adapter;
    private RecyclerView chatList;
    private static final String LIST_OF_USERS = "users";
    DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
    DatabaseReference thisUserChats;
    String phone;
    List<String> peoplesID = new ArrayList<>();
    List<UserDetail> userDetailList = new ArrayList<>();
    ProgressBar chatsLoading;


    LinearLayout addChatHolder;
    UserInformation information;

    private ChoiceViewModel viewModel;
    private List<String> allChannels = new ArrayList<>();


    private ImageView createNewChatImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chats, container, false);

        chatList = view.findViewById(R.id.chatRecyclerView);
        chatsLoading = view.findViewById(R.id.chatsLoading);
        addChatHolder = view.findViewById(R.id.addChatHolder);
        createNewChatImage = view.findViewById(R.id.createNewChatImage);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        chatList.setLayoutManager(manager);

        viewModel = ChoiceViewModel.getInstance(getActivity().getApplication());

        information = new UserInformation(getActivity());

        chatList.setHasFixedSize(true);

        adapter = new ChatListAdapter();

        adapter.setApplication(getActivity());
        // adapter.setUsers(people);

        loadTheme();

        phone = new UserInformation(getActivity()).getMainUserID();

        UserInformation information = new UserInformation(getActivity());
        phone = information.getMainUserID();
        DatabaseReference chats = allUsers.child(phone).child("chats");
        chats.keepSynced(true);
        thisUserChats = chats;

        // adapter.setUsers(people);
        adapterClicks();


        //  adapter.setUsers(people);

        new Thread(new Runnable() {
            @Override
            public void run() {

                loadChats();
            }
        }).start();


        viewModel.getChatList().observe(getActivity(), new Observer<List<MyChatChannel>>() {
            @Override
            public void onChanged(List<MyChatChannel> myChatChannels) {
                people = myChatChannels;
                adapter.setUsers(people);

            }
        });


        chatList.setAdapter(adapter);

        addChatHolder.setOnClickListener(v -> {
            YoYo.with(Techniques.Pulse).duration(150).playOn(createNewChatImage);
            openPoepleList();

        });
        return view;
    }

    private void openPoepleList() {
        BottomContact contactFriends = new BottomContact();
        contactFriends.setOnOpenPageListener(new Chat.OpenPageListener() {
            @Override
            public void openPage(MyChatChannel chatChannel, Drawable drawable, String text) {
                ChatFrament chatFrament = new ChatFrament();


                Intent intent = new Intent(getActivity(), ChatPage.class);

                intent.putExtra("phone number", chatChannel.getChatID());

                intent.putExtra("channelType", chatChannel.getChannelType());
                intent.putExtra("name", text);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_up,0);

            }
        });

        contactFriends.show(getFragmentManager(), null);

    }

    @Override
    public void onStart() {
        super.onStart();

        //loadTheme();


    }

    DatabaseReference myContacts;

    private void adapterClicks() {


        myContacts = FirebaseDatabase.getInstance().getReference("users").
                child(phone).child("people");
        myContacts.keepSynced(true);

        adapter.setOnclickListener(new ChatListAdapter.OnclickChatPersonListener() {
            @Override
            public void order(int position) {

            }

            @Override
            public void OnclickChatItem(int position, View view, TextView name, CircleImageView imageView) {

                MyChatChannel thisChat = people.get(position);
                thisUserChats.child(thisChat.getChatID())
                        .child("notification").removeValue();
                allUsers.child(phone).child("chat_badge").child(thisChat.getChatID()).removeValue();
                String text = thisChat.getName();
                String url = thisChat.getProfileUrl();


                Intent intent = new Intent(getContext(), ChatPage.class);

                intent.putExtra("phone number", thisChat.getChatID());

                intent.putExtra("channelType", thisChat.getChannelType());
                intent.putExtra("name", text);
                intent.putExtra("url", url);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                ChatPageBottomFragment fragment = new ChatPageBottomFragment();
                // fragment.show(getFragmentManager(),null);

                // OnOpenPageListener.openPage(thisChat, null, text);


            }

            @Override
            public void nameChoiceUser(int position, CircleImageView profilePicture, TextView name) {


                MyChatChannel chatList = people.get(position);
                name.setText(chatList.getName());
                Glide.with(getContext().getApplicationContext()).load(chatList.getProfileUrl()).
                        fitCenter().into(profilePicture);

            }

            @Override
            public void nameGroupChat(int position, CircleImageView profilePicture, TextView name) {

                MyChatChannel chatList = people.get(position);
                name.setText(chatList.getName());
                Glide.with(getContext().getApplicationContext()).load(chatList.getProfileUrl()).
                        fitCenter().into(profilePicture);


            }

            @Override
            public void getLastMessage(CircleImageView imageView, int position, ImageView messageState,
                                       ImageView isRecordingMic, EmojiTextView last_message, TextView lastMessageTime,
                                       TashieLoader isTypingLoader, CircleImageView senderImage, ImageView indicator) {
                MyChatChannel thisChat = people.get(position);

                // String key = allChannels.get(position);

                Message updateLastmessage = new Message(null, null, thisChat.getMessageType(), thisChat.getMessageOwnerID(),
                        thisChat.getLastMessageTime(), thisChat.getMessageContent(),
                        null, thisChat.getMessageType(), thisChat.getMessageState());

                if (getMessgeType(updateLastmessage.getMessageTYPE())) {
                    if (updateLastmessage.getMessageState() != MessageState.SEEN) {
                        indicator.setVisibility(View.VISIBLE);

                        last_message.setTextColor(getResources().getColor(R.color.whiteGreen));
                        YoYo.with(Techniques.Pulse).duration(3000).repeat(YoYo.INFINITE).playOn(indicator);

                    } else {
                        indicator.setVisibility(View.INVISIBLE);
                        last_message.setTextColor(getResources().getColor(R.color.textContext));
                    }
                }else {
                    indicator.setVisibility(View.INVISIBLE);
                    last_message.setTextColor(getResources().getColor(R.color.textContext));


                }

                if (updateLastmessage != null) {
                    updateLastMessage(imageView, updateLastmessage, getContext(), messageState, isRecordingMic, last_message, lastMessageTime,
                            isTypingLoader, senderImage);
                }

            }

            @Override
            public void chatActivity(int position, TashieLoader loader, ImageView mic,
                                     ImageView messageState, EmojiTextView last_message) {


            }

            @Override
            public void notification(int position, RelativeLayout chatListNotificationCountHolder,
                                     TextView chatListNotificationCount, RelativeLayout rootView) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("notification", MODE_PRIVATE);
                boolean badgeCounter = sharedPreferences.getBoolean("badge counter", true);

                if (badgeCounter) {
                    MyChatChannel channel = people.get(position);
                    if (channel.getBadgeCount() > 0) {
                        chatListNotificationCountHolder.setVerticalGravity(View.VISIBLE);
                        chatListNotificationCount.setText(String.valueOf(channel.getBadgeCount()));
                    } else {
                        chatListNotificationCountHolder.setVerticalGravity(View.GONE);
                    }
                }
            }
        });

    }

    private void loadChats() {


        thisUserChats
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot upperSnaption) {
                        if (upperSnaption.exists()) {

                            //people.clear();
                            for (DataSnapshot chats : upperSnaption.getChildren()) {
                                Channels channels = chats.getValue(Channels.class);
                                if (channels.getChannelType() == ChannelTypes.ONE_TO_ONE_CHAT) {
                                    loadUserChat(chats, channels);
                                } else if (channels.getChannelType() == ChannelTypes.GROUP_CHAT) {
                                    DatabaseReference groupChatFolder = FirebaseDatabase.getInstance().getReference("GroupChats");

                                    groupChatFolder.child(chats.getKey())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        GroupChatInformation information = snapshot.getValue(GroupChatInformation.class);
                                                        myContacts.child(chats.getKey()).child("messages")
                                                                .limitToLast(1)
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            CloudMessage lastMessage = snapshot.getValue(CloudMessage.class);

                                                                            MyChatChannel myChatChannel = new MyChatChannel(chats.getKey(), channels.getChatChannelID(),
                                                                                    lastMessage.getMessageTIME(), channels.getChannelType(), channels.isMute(), channels.isBlockUser()
                                                                                    , information.getGroupChatName(),
                                                                                    information.getImageUrl(), lastMessage.getMessageCONTENT(),
                                                                                    lastMessage.getMessageOwnerID(), lastMessage.getMessageTYPE(), lastMessage.getMessageState(), 0);
                                                                            updateBadge(myChatChannel, chats.getRef());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    public void loadUserChat(DataSnapshot chats, Channels channels) {
        myContacts.child(chats.getKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ChoiceUser choiceUser = snapshot.getValue(ChoiceUser.class);
                            if (choiceUser != null) {
                                thisUserChats.child(choiceUser.getUserPhoneNumber()).child("messages")
                                        .limitToLast(1)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        CloudMessage lastMessage = dataSnapshot.getValue(CloudMessage.class);


                                                        MyChatChannel myChatChannel = new MyChatChannel(chats.getKey(), channels.getChatChannelID(),
                                                                lastMessage.getMessageTIME(), channels.getChannelType(), channels.isMute(), channels.isBlockUser()
                                                                , choiceUser.getUserContactName(),
                                                                choiceUser.getUserImageUrl(), lastMessage.getMessageCONTENT(),
                                                                lastMessage.getMessageOwnerID(), lastMessage.getMessageTYPE(), lastMessage.getMessageState(), 0);
                                                        updateBadge(myChatChannel, chats.getRef());
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        } else {

                            allUsers.child(chats.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                UserDetail detail = snapshot.getValue(UserDetail.class);
                                                thisUserChats.child(detail.getPhone()).child("messages")
                                                        .limitToLast(1)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                        CloudMessage lastMessage = dataSnapshot.getValue(CloudMessage.class);

                                                                        MyChatChannel myChatChannel = new MyChatChannel(chats.getKey(), channels.getChatChannelID(),
                                                                                lastMessage.getMessageTIME(), channels.getChannelType(), channels.isMute(), channels.isBlockUser()
                                                                                , detail.getUsername(),
                                                                                detail.getProfileURL(), lastMessage.getMessageCONTENT(),
                                                                                lastMessage.getMessageOwnerID(), lastMessage.getMessageTYPE(), lastMessage.getMessageState(), 0);
                                                                        updateBadge(myChatChannel, chats.getRef());
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void updateBadge(MyChatChannel channel, DatabaseReference reference) {
        MyChatChannel thisChannel = channel;
        reference.child("notification")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {


                            long counter = snapshot.getChildrenCount();
                            thisChannel.setBadgeCount((int) counter);
                            viewModel.insertChat(thisChannel);

                        } else {
                            thisChannel.setBadgeCount(0);
                            viewModel.insertChat(thisChannel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadTheme();
        //loadChats();
    }


    public void setTextColors(int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                adapter.setColor(R.color.textColor);

                break;
            case Theme.DEEP:
                adapter.setColor(R.color.whiteGreen);
                break;

        }
    }

    private void loadTheme() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);
        adapter.notifyDataSetChanged();

    }

    private void updateLastMessage(CircleImageView imageView, Message lastMessage, Context activity, ImageView messageState,
                                   ImageView isRecordingMic, EmojiTextView last_message, TextView lastMessageTime,
                                   TashieLoader isTypingLoader, CircleImageView userMessagePicture) {


        if (lastMessage.getMessageOwnerID() != null) {
            updateMessageTime(lastMessageTime, lastMessage.getMessageTIME());

            if (lastMessage.getMessageOwnerID().equals(new UserInformation(activity).getMainUserID())) {
                messageState.setVisibility(View.VISIBLE);
                isRecordingMic.setVisibility(View.GONE);
                updateMessageState(lastMessage.getMessageState(), messageState);
            } else {
                messageState.setVisibility(View.INVISIBLE);


            }

            int messageType = lastMessage.getMessageTYPE();
            String messageContent = "";
            if (messageType == MessageType.PRIVATE_COMMENT_IMAGE_SINGLE || messageType == MessageType.PRIVATE_COMMENT_TEXT ||
                    messageType == MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED || messageType == MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED) {


                String[] mSplit = lastMessage.getMessageCONTENT().split(String.valueOf(lastMessage.getMessageTIME()));

                messageContent = mSplit[0];

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);

            } else if (messageType == MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT ||
                    messageType == MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT
                    || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT
                    || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED
                    || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT
                    || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED) {
                messageContent = lastMessage.getMessageCONTENT().split(String.valueOf(lastMessage.getMessageTIME()))[0];
                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);


            } else if (messageType == MessageType.TEXT ||
                    messageType == MessageType.TEXT + MessageType.RECIEVED) {

                messageContent = lastMessage.getMessageCONTENT();
                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);

            } else if (messageType == MessageType.VOICE_NOTE + MessageType.RECIEVED ||
                    messageType == MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED ||
                    messageType == MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED
                    || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE
                    || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED
                    || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE
                    || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED) {


                String timeDuration = (lastMessage.getMessageCONTENT()).split(String.valueOf(lastMessage.getMessageTIME()))[0];
                messageContent = TimeFactor.convertMillieToHMmSs(Long.parseLong(timeDuration));
                messageContent = "voice note : " + messageContent;

            } else if (messageType == MessageType.VOICE_NOTE ||
                    messageType == MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE ||
                    messageType == MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE) {
                String timeDuration = (lastMessage.getMessageCONTENT()).split(String.valueOf(lastMessage.getMessageTIME()))[0];

                messageContent = TimeFactor.convertMillieToHMmSs(Long.parseLong(timeDuration));
                messageContent = "voice note : " + messageContent;

            } else if (messageType == MessageType.IMAGE) {
                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);
                messageContent = " sent a photo";

            } else if (messageType == MessageType.IMAGE + MessageType.RECIEVED) {
                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);
                messageContent = " received a photo";
            } else if (messageType == MessageType.VIDEO) {

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);
                messageContent = " sent a video";
            } else if (messageType == MessageType.VIDEO + MessageType.RECIEVED) {

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);

                messageContent = " received a video";
            } else if (messageType == MessageType.GROUP_CHAT_INVITE + MessageType.RECIEVED) {

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);

                messageContent = " group chat invite";
            } else if (messageType == MessageType.GROUP_CHAT_FIRST_MESSAGE) {

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);

                messageContent = "you created a new group chat";
            } else {

                isRecordingMic.setVisibility(View.GONE);
                isTypingLoader.setVisibility(View.GONE);
                messageContent = " unidentified message type";

            }
            last_message.setText(messageContent);

        }

    }

    private boolean getMessgeType(int messageType) {
        if (messageType == MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED || messageType == MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED
                || messageType == MessageType.GROUP_CHAT_INVITE + MessageType.RECIEVED ||
                messageType == MessageType.VIDEO + MessageType.RECIEVED || messageType == MessageType.IMAGE + MessageType.RECIEVED
                || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED

                || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED ||
                messageType == MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED ||

                messageType == MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED ||
                messageType == MessageType.VOICE_NOTE + MessageType.RECIEVED || messageType == MessageType.TEXT + MessageType.RECIEVED

                || messageType == MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED

                || messageType == MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED


        ) {
            return true;
        } else {
            return false;
        }
    }

    private void updateMessageState(int sate, ImageView messageState) {

        switch (sate) {

            case MessageState.SENT:

                messageState.setImageResource(R.drawable.not_seen_check);
                break;
            case MessageState.SENDING:
                messageState.setImageResource(R.drawable.check_white);
                break;
            case MessageState.SEEN:
                messageState.setImageResource(R.drawable.check);
                break;
            default:
                messageState.setImageResource(R.drawable.uncheck_white);
                break;

        }
    }


    private void updateMessageTime(TextView lastMessageTime, long timestamp) {
        String splitter = " :";
        lastMessageTime.setText(splitter + TimeFactor.getDetailedDate(timestamp, System.currentTimeMillis()));

    }
}
