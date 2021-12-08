package com.kariaki.choice.UI.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.BounceLoader;
import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.CloudMessage;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.Database.Entities.MyChatChannel;
import com.kariaki.choice.Model.Database.MessageState;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.Post;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Comment.CommentPage;
import com.kariaki.choice.UI.DialogBox.ChoiceDialogBox;
import com.kariaki.choice.UI.MainPage.Adapters.MakePostAdapter;
import com.kariaki.choice.UI.MainPage.BottomFragments.BottomMakePost;
import com.kariaki.choice.UI.MakePost.GalleryItem;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Messaging.SwipeReply;
import com.kariaki.choice.UI.Post.PostInfo;
import com.kariaki.choice.UI.Profiles.PartialProfile;
import com.kariaki.choice.UI.Profiles.SocialProfile;
import com.kariaki.choice.UI.util.ConnectionState;
import com.kariaki.choice.UI.util.Functions;
import com.kariaki.choice.UI.util.Gallery;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPage extends FragmentActivity {

    private EmojiEditText writeMessage;
    private ImageButton camera, chatGallery, send;
    ImageView mic, scrollDown;
    ImageView chatPageUserInfo, isRecordingMic;
    private TextView chatPageUerName, chatPagePartialTime, chatPageLastSeen;
    private MediaRecorder recorder;
    private RecyclerView messageContainer;
    ChatAdapter adapter;
    private ImageButton emoji, keyboard;
    private ImageView back;
    private CircleImageView chatpageProfilePic;
    List<Message> messages = new ArrayList<>();
    RelativeLayout rootView;
    EmojiPopup emojiPopup;
    SharedPreferences preferences;
    private final String shareDefaul = "no user";
    private final String sharedKey = "userDetail";
    private Chronometer chatVoiceNoteCounter;
    String outputFile;
    long startTime = 0;
    int count = 0;
    String path = "";
    long endTime = 0;
    private AppBarLayout chatPageAppBar;

    String chatID;
    String myPhoneNumber;
    Intent chatIntent;
    int chat_type;
    Context context;

    private DatabaseReference userToChatWith;
    private DatabaseReference chatChannels;
    private DatabaseReference myChats;
    DatabaseReference chatFolder;
    DatabaseReference otherChatFolder;
    DatabaseReference reference;
    StorageReference chatFiles;
    DatabaseReference realtimeUser = FirebaseDatabase.getInstance().getReference("users");

    boolean showName = false;

    private DatabaseReference channelActivity;
    boolean isInviteChat = false;
    Message messageToReply;
    int generalMessageType = 0;
    int currentlyPlaying;
    List<GalleryItem> images = new ArrayList<>();
    ImageView chatPageExtension;
    File privateFile;
    String privatepath;

    TextView aboutText;
    ImageView onlineStatus;
    //String chatURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        viewByID();
        chatPageExtension = findViewById(R.id.chatPageExtension);
        context = this;
        chatIntent = getIntent();
        chat_type = chatIntent.getIntExtra("channelType", ChannelTypes.ONE_TO_ONE_CHAT);
        chatID = chatIntent.getStringExtra("phone number");
        // chatURL = chatIntent.getStringExtra("chat url");
        myPhoneNumber = new UserInformation(this).getMainUserID();
        databaseInitialization();
        UserNaming naming = UserNaming.getInstance();
        privateFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        aboutText = findViewById(R.id.aboutText);
        onlineStatus = findViewById(R.id.onlineStatus);
        privatepath = privateFile.getAbsolutePath();


        viewModel = ChoiceViewModel.getInstance(getApplication());

        setBlockedUI();
        adapter = new ChatAdapter(context);


        preferences = getSharedPreferences(sharedKey, MODE_PRIVATE);
        chatSettings = getSharedPreferences("chatSettings", MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestNeededPermision();
        }

        outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/choice";

        aboutText.setMarqueeRepeatLimit(1);
        aboutText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        aboutText.setSelected(true);

        String name = chatIntent.getStringExtra("name");
        String url = chatIntent.getStringExtra("url");
        if (url == null || url.isEmpty()) {
            if (chat_type == ChannelTypes.ONE_TO_ONE_CHAT) {
                UserNaming.getInstance().loadProfilePicture(chatID, chatpageProfilePic, this);
            } else if (chat_type == ChannelTypes.GROUP_CHAT) {
                UserNaming.getInstance().nameThisGroupChat(name, this, chatPageUerName, chatpageProfilePic);

            }
        } else {
            Glide.with(getApplicationContext()).load(url).into(chatpageProfilePic);
        }
        chatPageUerName.setText(name);
        switch (chat_type) {
            case ChannelTypes.ONE_TO_ONE_CHAT:
                chatPageLastSeen.setVisibility(View.VISIBLE);
                checkConnectionForActivity();
                showName = true;

                checkOnLine();
                break;
            case ChannelTypes.GROUP_CHAT:
                showName = true;
                isInviteChat = false;
                chatPageLastSeen.setVisibility(View.INVISIBLE);

                break;


        }

        SwipeReply swipeReply = new SwipeReply(this) {
            @Override
            public void swipeAction(int position) {
                Message message = messages.get(position);
                messageToReply = message;

                processMessageToReply(message);

            }
        };
        currentlyPlaying = -1;

        Gallery gallery = new Gallery(this);
        images = gallery.getImages();


        File file = new File(outputFile);

        if (!file.exists()) {
            boolean success = file.mkdir();
        }

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(writeMessage);

        writeMessage.setEmojiSize(50);


        new ItemTouchHelper(swipeReply)

                .attachToRecyclerView(messageContainer);


        loadTheme();
        initializeChannelActivity();
        onclickAndChange();

        // loadWithLocalDb();

        adapterData();


        loadWithLocalDb();

        loadOnScroll();
        // getWindow().setWindowAnimations(0);

        ImageView chatPageSend = findViewById(R.id.chatPageSend);
        chatPageSend.setOnClickListener(this::sendMessage);
        scrollDown.setOnClickListener(this::scrollToBottm);

        //loading new messages to chat
        loadNewMessages();
        replyMessageHolder.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void requestNeededPermision() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        }
    }

    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        if (timer != null) {
            timer = null;
        }
        amRecording(false);
        amTyping(false);
        if (m != null) {
            m.stop();
            m.release();
            m = null;
        }
        if (thisChatChannel != null) {
            reference
                    .child("notification").removeValue();
            realtimeUser.child(myPhoneNumber).child("chat_badge").child(chatID).removeValue();
        }

    }

    public void scrollToBottm(View view) {
        messageContainer.smoothScrollToPosition(adapter.getItemCount());
        manager.scrollToPosition(adapter.getItemCount());
        view.setVisibility(scrollDown.GONE);
    }

    public void backPress(View view) {
        onBackPressed();
    }

    private void isTyping() {

        if (chat_type == ChannelTypes.ONE_TO_ONE_CHAT) {

            DatabaseReference myChat = realtimeUser.child(myPhoneNumber).child("chats").child(chatID);
            myChat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Channels chatChannel = dataSnapshot.getValue(Channels.class);
                        if (!chatChannel.isBlockUser()) {
                            thisUserActivity = FirebaseDatabase.getInstance().getReference("ChatChannels")
                                    .child(chatChannel.getChatChannelID()).child(chatID);
                            thisUserActivity.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot activitySnapShot) {
                                    if (activitySnapShot.exists()) {
                                        ChannelActivity userActivity = activitySnapShot.getValue(ChannelActivity.class);


                                        if (userActivity.isTyping() || userActivity.isRecording()) {
                                            chatActivityHolder.setVisibility(View.VISIBLE);
                                            isTypingLoader.setVisibility(View.VISIBLE);
                                            UserNaming naming = UserNaming.getInstance();
                                            naming.loadProfilePicture(chatID, chatActivityProfilePicture, context);

                                        } else {
                                            isTypingLoader.setVisibility(View.INVISIBLE);
                                            isRecordingMic.setVisibility(View.INVISIBLE);
                                            chatActivityHolder.setVisibility(View.GONE);
                                        }

                                        if (userActivity.isRecording()) {
                                            YoYo.with(Techniques.Pulse).duration(500).repeat(YoYo.INFINITE).playOn(isRecordingMic);
                                            isRecordingMic.setVisibility(View.VISIBLE);
                                            chatActivityProfilePicture.setVisibility(View.GONE);

                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }


    private void checkOnLine() {

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean amOnline = (dataSnapshot.getValue(Boolean.class));
                if (amOnline) {

                    Functions.onLineCheck(ChatPage.this, chatID, chatPageLastSeen, System.currentTimeMillis());

                } else {
                    chatPageLastSeen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    TashieLoader isTypingLoader;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    RelativeLayout replyHolder, messageOptionsHolder, replyContentHolder;
    ImageView replyCancelButton;
    ImageView replyPhotoPreview;
    TextView replyMessageHolder, optionDeleteMessageText, optionCopyMessageText, optionReplyMessageText, optionForwardMessageText;

    FloatingActionButton micFloat;
    SwipeRefreshLayout messageRefresh;

    RelativeLayout unblockUser, editChatBottom;
    DatabaseReference messageCopy;


    DatabaseReference thisUserActivity;
    Channels thisChatChannel;

    private void checkConnectionForActivity() {
        realtimeUser.child(chatID).child("Connection")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ConnectionState connectionState = dataSnapshot.getValue(ConnectionState.class);
                            if (!connectionState.isOnline()) {
                                chatActivityHolder.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initializeChannelActivity() {

        DatabaseReference myChat = realtimeUser.child(myPhoneNumber).child("chats").child(chatID);

        myChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Channels chatChannel = dataSnapshot.getValue(Channels.class);
                    thisChatChannel = chatChannel;
                    chatChannelID = thisChatChannel.getChatChannelID();
                    channelActivity = FirebaseDatabase.getInstance().getReference("ChatChannels").child(chatChannel.getChatChannelID())
                            .child(myPhoneNumber);
                    thisUserActivity = FirebaseDatabase.getInstance().getReference("ChatChannels")
                            .child(chatChannel.getChatChannelID()).child(chatID);
                    channelActivity.setValue(new ChannelActivity(false, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void processMessageToReply(Message message) {

        if (message.getMessageState() != MessageState.SENT ||
                message.getMessageState() != MessageState.SEEN) {


            String messageContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
            switch (message.getMessageTYPE()) {
                case MessageType.TEXT:
                case MessageType.TEXT + MessageType.RECIEVED:
                case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT:
                case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT:
                case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
                case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
                case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT:
                case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT:
                case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
                case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
                case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE:
                case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED:
                case MessageType.PRIVATE_COMMENT_TEXT:
                case MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED:

                    replyPhotoPreview.setVisibility(View.GONE);
                    generalMessageType = MessageType.TEXT + MessageType.TEXT + MessageType.REPLY;

                    replyHolder.setVisibility(View.VISIBLE);
                    replyContentHolder.setVisibility(View.VISIBLE);
                    break;

                case MessageType.VOICE_NOTE:
                case MessageType.VOICE_NOTE + MessageType.RECIEVED:
                case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE:
                case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE:
                case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE:
                case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE:
                case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
                case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:

                    long duration = Long.parseLong(messageContent);
                    messageContent = "voice note |" + TimeFactor.convertMillieToHMmSs(duration);
                    generalMessageType = MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY;
                    replyPhotoPreview.setVisibility(View.GONE);

                    replyHolder.setVisibility(View.VISIBLE);
                    replyContentHolder.setVisibility(View.VISIBLE);

                    break;
                case MessageType.IMAGE:
                case MessageType.IMAGE + MessageType.RECIEVED:
                    replyPhotoPreview.setVisibility(View.GONE);
                    messageContent = "Replying a photo";
                    // generalMessageType = MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY;
                    generalMessageType = 0;

                    replyHolder.setVisibility(View.GONE);
                    replyContentHolder.setVisibility(View.GONE);
                    break;
                case MessageType.VIDEO:
                case MessageType.IMAGE + MessageType.VIDEO:
                    replyPhotoPreview.setVisibility(View.GONE);
                    messageContent = "Replying a video";

                    replyHolder.setVisibility(View.GONE);
                    replyContentHolder.setVisibility(View.GONE);
                    //generalMessageType = MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY;
                    generalMessageType = 0;
                    break;

            }
            replyMessageHolder.setText(messageContent);


        }


    }


    private boolean textCheck(Message message) {

        boolean output = false;
        String messageContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
        switch (message.getMessageTYPE()) {
            case MessageType.TEXT:
            case MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.TEXT + MessageType.RECIEVED:
            case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE:
            case MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED:
            case MessageType.PRIVATE_COMMENT_TEXT:
            case MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED:


                replyPhotoPreview.setVisibility(View.GONE);
                generalMessageType = MessageType.TEXT + MessageType.TEXT + MessageType.REPLY;

                output = true;
                break;

            case MessageType.VOICE_NOTE:
            case MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.TEXT + MessageType.TEXT + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE:
            case MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:
            case MessageType.VIDEO + MessageType.VIDEO + MessageType.REPLY + MessageType.VOICE_NOTE + MessageType.RECIEVED:

                long duration = Long.parseLong(messageContent);
                messageContent = "voice note |" + TimeFactor.convertMillieToHMmSs(duration);
                generalMessageType = MessageType.VOICE_NOTE + MessageType.VOICE_NOTE + MessageType.REPLY;
                replyPhotoPreview.setVisibility(View.GONE);

                output = false;
            case MessageType.IMAGE:
            case MessageType.IMAGE + MessageType.RECIEVED:
                replyPhotoPreview.setVisibility(View.VISIBLE);
                messageContent = "Replying a photo";
                generalMessageType = MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY;
                break;
            case MessageType.VIDEO:
            case MessageType.IMAGE + MessageType.VIDEO:
                replyPhotoPreview.setVisibility(View.VISIBLE);
                messageContent = "Replying a video";
                generalMessageType = MessageType.IMAGE + MessageType.IMAGE + MessageType.REPLY;
                output = false;

        }


        return output;


    }

    private void databaseInitialization() {
        realtimeUser = FirebaseDatabase.getInstance().getReference("users");
        userToChatWith = realtimeUser.child(chatID);
        chatChannels = FirebaseDatabase.getInstance().getReference("ChatChannels");
        myChats = realtimeUser.child(myPhoneNumber);
        chatFolder = myChats.child("chats");
        otherChatFolder = userToChatWith.child("chats");
        reference = chatFolder.child(chatID);
        chatFiles = FirebaseStorage.getInstance().getReference("ChatFiles");
        messageCopy = chatFolder.child(chatID).child("messages");

    }

    BounceLoader messageLoading;

    CircleImageView chatActivityProfilePicture;
    LinearLayout chatActivityHolder;

    private void viewByID() {
        chatpageProfilePic = findViewById(R.id.chatpageProfilePic);
        emoji = findViewById(R.id.chatPageEmoji);
        replyContentHolder = findViewById(R.id.replyContentHolder);
        messageContainer = findViewById(R.id.chatMessagesContainer);
        micFloat = findViewById(R.id.micFloat);
        chatActivityHolder = findViewById(R.id.chatActivityHolder);
        messageLoading = findViewById(R.id.messageLoading);
        chatActivityProfilePicture = findViewById(R.id.chatActivityProfilePicture);
        messageRefresh = findViewById(R.id.messageRefresh);
        replyPhotoPreview = findViewById(R.id.replyPhotoPreview);
        rootView = findViewById(R.id.rootView);
        replyCancelButton = findViewById(R.id.replyCancelButton);
        replyHolder = findViewById(R.id.replyHolder);
        replyMessageHolder = findViewById(R.id.replyMessageHolder);
        isRecordingMic = findViewById(R.id.isRecordingMic);
        isTypingLoader = findViewById(R.id.isTypingLoader);
        chatPageLastSeen = findViewById(R.id.chatPageLastSeen);
        back = findViewById(R.id.chatPageBack);
        chatPageUerName = findViewById(R.id.chatPageUerName);
        writeMessage = findViewById(R.id.write_chat);
        mic = findViewById(R.id.chatPageMic);
        chatPagePartialTime = findViewById(R.id.chatPagePartialTime);
        camera = findViewById(R.id.chatPageCamera);
        chatPageAppBar = findViewById(R.id.chatPageAppBar);
        chatGallery = findViewById(R.id.chatPageExtension);
        messageContainer = findViewById(R.id.chatMessagesContainer);
        send = findViewById(R.id.chatPageSend);
        scrollDown = findViewById(R.id.scrollDown);
        keyboard = findViewById(R.id.chatPageKeyboard);
        chatPageUserInfo = findViewById(R.id.chatPageUserInfo);
        editChatBottom = findViewById(R.id.editChatBottom);
        chatVoiceNoteCounter = findViewById(R.id.chatVoiceNoteCounter);
        unblockUser = findViewById(R.id.unblockUser);

    }

    private void setBlockedUI() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MyChatChannel channel = dataSnapshot.getValue(MyChatChannel.class);
                    if (channel.isBlockUser()) {
                        unblockUser.setVisibility(View.VISIBLE);
                        editChatBottom.setVisibility(View.INVISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage(View view) {

        String text = writeMessage.getText().toString();
        long messageTime = System.currentTimeMillis();
        if (!text.isEmpty()) {

            if (emojiPopup.isShowing()) {
                emojiPopup.toggle();
            }

            if (messageToReply != null) {
                int messageType = messageToReply.getMessageTYPE();
                if (messageType == MessageType.VIDEO || messageType == MessageType.IMAGE || messageType == MessageType.VIDEO + MessageType.RECIEVED
                        || messageType == MessageType.IMAGE + MessageType.RECIEVED) {
                    String replyContent = messageToReply.getMessageURL().split(",")[1];
                    text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID();
                } else {
                    String replyContent = messageToReply.getMessageCONTENT().split(String.valueOf(messageToReply.getMessageTIME()))[0];
                    text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID();

                }
            }


            CloudMessage message = new CloudMessage(generalMessageType + MessageType.TEXT, myPhoneNumber, messageTime,
                    text, "", MessageState.SENT);

            Message absoluteMessage = new Message("", chatID, message.getMessageTYPE(),
                    message.getMessageOwnerID(),
                    message.getMessageTIME(), message.getMessageCONTENT(),
                    message.getMessageURL(), chat_type, MessageState.SEEN);

            messages.add(absoluteMessage);
            adapter.notifyItemInserted(messages.size());
            messageContainer.smoothScrollToPosition(messages.size());
            writeMessage.setText("");
            amTyping(false);
            // playSound();
            replyHolder.setVisibility(View.GONE);

            uploadMessage(message, absoluteMessage);


        }

    }


    List<String> messageIDs = new ArrayList<>();
    String CHANNEL_ID;

    private void uploadMessage(CloudMessage message, Message absoluteMessage) {


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (!dataSnapshot.exists()) {
                    DatabaseReference ourChat = chatChannels.push();
                    DatabaseReference sendMessage = ourChat.child("messages");
                    String channelID = ourChat.getKey();
                    Channels yours = new Channels(channelID, System.currentTimeMillis(), ChannelTypes.ONE_TO_ONE_CHAT, false, false);
                    Channels mine = new Channels(channelID, System.currentTimeMillis(), ChannelTypes.ONE_TO_ONE_CHAT, false, false);
                    chatFolder.child(chatID).setValue(mine);
                    otherChatFolder.child(myPhoneNumber).setValue(yours);

                    DatabaseReference messageRef = sendMessage.push();
                    absoluteMessage.setMessageID(messageRef.getKey());
                    messageRef.setValue(message);
                    messageCopy.child(messageRef.getKey()).setValue(message);
                    messageIDs.add(messageRef.getKey());
                    CHANNEL_ID = channelID;
                    viewModel.insertMessage(absoluteMessage);

                } else {

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Channels channel = dataSnapshot.getValue(Channels.class);


                            DatabaseReference messageToSend = FirebaseDatabase.getInstance().
                                    getReference("ChatChannels").child(channel.getChatChannelID())
                                    .child("messages").push();

                            createMessage(message, messageToSend, absoluteMessage);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void createMessage(CloudMessage message, DatabaseReference messageToSend, Message absoluteMessage) {

        messageIDs.add(messageToSend.getKey());
        messageCopy.child(messageToSend.getKey()).setValue(message);
        messageToSend.setValue(message);
        absoluteMessage.setMessageID(messageToSend.getKey());
        viewModel.insertMessage(absoluteMessage);

    }


    int messageLimit = 15;

    ChoiceViewModel viewModel;

    private boolean checkForHeader(long messageTime1, long messageTime2) {
        Date initialDate = TimeFactor.getDateDate(messageTime1, "yyy:MM:dd");
        Date currentDate = TimeFactor.getDateDate(messageTime2, "yyy:MM:dd");
        if (TimeFactor.getDetailedDate(messageTime1).equals(TimeFactor.getDetailedDate(messageTime2))) {

            return false;
        } else {
            return true;
        }

    }

    private void loadOnScroll() {

        messageRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageLimit += 20;
                //fetchMessage();
            }
        });


    }

    private void loadWithLocalDb() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                messages = viewModel.getChannelMessage(chatID);

                if (messages.isEmpty()) {
                    messageCopy.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                long messageCount = snapshot.getChildrenCount();
                                if (messageCount > 0) {
                                    for (DataSnapshot messageCopy : snapshot.getChildren()) {
                                        CloudMessage message = messageCopy.getValue(CloudMessage.class);

                                        Message absoluteMessage =
                                                new Message(messageCopy.getKey(), chatID,
                                                        message.getMessageTYPE(),
                                                        message.getMessageOwnerID(),
                                                        message.getMessageTIME(), message.getMessageCONTENT(),
                                                        message.getMessageURL(), chat_type, message.getMessageState());


                                        if (!message.getMessageOwnerID().equals(myPhoneNumber)) {
                                            absoluteMessage.setMessageTYPE(absoluteMessage.getMessageTYPE() + MessageType.RECIEVED);

                                        } else {

                                        }
                                        messages.add(absoluteMessage);
                                        adapter.notifyDataSetChanged();
                                        viewModel.insertMessage(absoluteMessage);

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


                adapter.setMessage(messages);
            }
        }).start();


    }

    private void loadNewMessages() {

        messageCopy.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot NmessageCopy, @Nullable String previousChildName) {
                if (NmessageCopy.exists()) {
                    CloudMessage message = NmessageCopy.getValue(CloudMessage.class);

                    Message absoluteMessage =
                            new Message(NmessageCopy.getKey(), chatID,
                                    message.getMessageTYPE(),
                                    message.getMessageOwnerID(),
                                    message.getMessageTIME(), message.getMessageCONTENT(),
                                    message.getMessageURL(), chat_type, message.getMessageState());



                    viewModel.insertMessage(absoluteMessage);
                    if (absoluteMessage.getMessageState() != MessageState.SEEN) {
                        if (!message.getMessageOwnerID().equals(myPhoneNumber)) {
                            absoluteMessage.setMessageTYPE(absoluteMessage.getMessageTYPE() + MessageType.RECIEVED);
                            //messageIDs.add(messageCopy.getKey());
                            messages.add(absoluteMessage);
                            adapter.notifyItemInserted(adapter.getItemCount()+1);

                            messageContainer.smoothScrollToPosition(adapter.getItemCount());
                            manager.scrollToPosition(adapter.getItemCount());

                        }
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchMessage() {
        messageCopy.keepSynced(true);
        messageLimit += 20;

        messageCopy
                .orderByChild("messageTIME")
                .limitToLast(messageLimit)
                .addValueEventListener
                        (new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot messageCopy : dataSnapshot.getChildren()) {
                                        if (!messageIDs.contains(messageCopy.getKey())) {
                                            messageLoading.setVisibility(View.VISIBLE);
                                            CloudMessage message = messageCopy.getValue(CloudMessage.class);

                                            Message absoluteMessage =
                                                    new Message(dataSnapshot.getKey(), chatID,
                                                            message.getMessageTYPE(),
                                                            message.getMessageOwnerID(),
                                                            message.getMessageTIME(), message.getMessageCONTENT(),
                                                            message.getMessageURL(), chat_type, message.getMessageState());


                                            if (!message.getMessageOwnerID().equals(myPhoneNumber)) {
                                                absoluteMessage.setMessageTYPE(absoluteMessage.getMessageTYPE() + MessageType.RECIEVED);
                                                messageIDs.add(messageCopy.getKey());
                                                messages.add(absoluteMessage);
                                                adapter.notifyDataSetChanged();


                                            } else {

                                                messageIDs.add(messageCopy.getKey());
                                                messages.add(absoluteMessage);
                                                adapter.notifyItemInserted(messages.size());


                                            }
                                            // viewModel.insertMessage(absoluteMessage);
                                        }

                                    }
                                    messageRefresh.setRefreshing(false);
                                } else {
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


    }

    public void unblockUser(View view) {


        Map<String, Object> update = new HashMap<>();
        update.put("blockUser", false);
        editChatBottom.setVisibility(View.VISIBLE);
        unblockUser.setVisibility(View.INVISIBLE);
        reference.updateChildren(update);


    }

    RelativeLayout chatEditTextArea;
    int theme;

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                rootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                replyHolder.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

                chatPageAppBar.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                theme = Theme.LIGHT;

                writeMessage.setTextColor(getResources().getColor(android.R.color.background_dark));
                // chatEditTextArea.setBackground(getResources().getDrawable(R.drawable.chat_comment_background));
                break;
            case Theme.DEEP:
                theme = Theme.DEEP;
                changeTextColors(text, R.color.whiteGreen);
                replyHolder.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

                writeMessage.setTextColor(getResources().getColor(R.color.whiteGreen));

                replyHolder.setBackgroundColor(getResources().getColor(R.color.replycolor));


                //chatEditTextArea.setBackground(getResources().getDrawable(R.drawable.chat_comment_backgrou_dark_mode));
                rootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                chatPageAppBar.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    LinearLayoutManager manager;

    MediaPlayer m;
    List<Integer> currentlyPlayingVoiceNote = new ArrayList<>();

    private void copyText(String textToCopy) {

        setClipboard(context, textToCopy);
    }


    private void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "text copied", Toast.LENGTH_SHORT).show();

    }

    private MessageOptions.optionClickListeners optionProcessor(Message message, int position) {

        MessageOptions.optionClickListeners optionClickListeners =
                new MessageOptions.optionClickListeners() {
                    @Override
                    public void onCopyMessage() {
                        generalMessageType = 0;
                        String copiedText = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];

                        copyText(copiedText);

                    }

                    @Override
                    public void onReplyMessage() {

                        messageToReply = message;
                        processMessageToReply(message);


                    }

                    @Override
                    public void onDeleteMessage() {

                        ChoiceDialogBox dialogBox = new ChoiceDialogBox();
                        dialogBox.setTittle("Delete message?");
                        dialogBox.setMessage("Are you sure you want to delete this message?");
                        dialogBox.setListeners(new ChoiceDialogBox.dialogButtons() {
                            @Override
                            public void onClickPositiveButton() {
                                deleteForMe(message, position);

                            }

                            @Override
                            public void onClickNegativeButton() {
                                dialogBox.dismiss();
                            }
                        });
                        dialogBox.show(getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
                    }

                    @Override
                    public void onForwardMessage() {

                    }
                };
        return optionClickListeners;

    }

    private void deleteForMe(Message message, int position) {
        messageCopy.child(message.getMessageID())
                .removeValue();
        messages.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void adapterData() {

        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        adapter = new ChatAdapter(context);
        //adapter.setLocation(UILocations.CHAT_PAGE);
        adapter.setTheme(currentTheme);
        adapter.notifyDataSetChanged();

        currentlyPlayingVoiceNote.add(9 - 1);
        adapter.setCurrentlyPlaying(currentlyPlayingVoiceNote);

        adapter.setOnclickListener(new ChatAdapter.OnclickListener() {
            @Override
            public DatabaseReference replyCounter(int position) {
                return null;

            }

            @Override
            public void onMessageClickListener(int position, View itemView) {


            }

            @Override
            public void setMessageTime(int position, TextView view) {

            }

            @Override
            public void onReplyClick(int position) {

            }

            @Override
            public void sendMessageText(int position) {

                Message message = messages.get(position);


            }

            @Override
            public void onSeekBarChange(int position, int progress) {
                if (m != null && m.isPlaying()) {
                    int duration = m.getDuration();
                    //int currentposition=m.getCurrentPosition();
                    int seekTo = (duration * progress) / 100;
                    m.seekTo(seekTo);
                }
            }

            @Override
            public void onLongClickMessage(int position, View itemView) {
                MessageOptions messageOptions = new MessageOptions();
                Message message = messages.get(position);

                messageOptions.setMyComment(textCheck(message));

                if (message.getMessageState() != MessageState.SENDING) {
                    messageOptions.setOptionClickListeners(optionProcessor(message, position));
                    messageOptions.show(getSupportFragmentManager(), "tag tag");
                }


            }

            @Override
            public void setMessageToSeen(int position) {
                Message message = messages.get(position);
                if (message.getMessageState() != MessageState.SEEN) {
                    updateMessagesIveSeen(message.getMessageID());
                    message.setMessageState(MessageState.SEEN);
                    viewModel.updateMessage(message);
                }
            }

            @Override
            public void uploadFiles(int position, ProgressBar uploadProgress, ImageView playButton,
                                    ImageView send_state) {
                Message message = messages.get(position);

                if (message.getMessageState() != MessageState.SENT || message.getMessageState() != MessageState.SEEN) {


                    StorageReference voicenoteFile = chatFiles.child(System.currentTimeMillis() + "." + Functions.fileExtension((message.getMessageURL())));

                    // Uri uri=Uri.parse(message.getMessageURL());

                    voicenoteFile.putFile(Uri.fromFile(new File(message.getMessageURL())))
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    send_state.setImageResource(R.drawable.check_white);
                                    int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    uploadProgress.setVisibility(View.GONE);
                                    // adapter.notifyItemChanged(position);
                                    voicenoteFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri recordUri) {
                                            CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(), myPhoneNumber,
                                                    message.getMessageTIME(),
                                                    message.getMessageCONTENT(), recordUri.toString(), MessageState.SENT);
                                            String url = recordUri.toString();
                                            url = recordUri.toString();
                                            message.setMessageURL(url);
                                            message.setMessageState(MessageState.SENT);
                                            send_state.setImageResource(R.drawable.not_seen_check);
                                            viewModel.updateMessage(message);
                                           /* DatabaseReference sendMessageRef = chatChannels.child(thisChatChannel.getChatChannelID())
                                                    .child("messages").child(message.getMessageID());
                                            sendMessageRef.setValue(cloudMessage);

                                            */

                                            uploadGenerally(cloudMessage, message.getMessageID());


                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    message.setMessageState(MessageState.FAILED);
                                    viewModel.updateMessage(message);
                                    send_state.setImageResource(R.drawable.check_red);
                                }
                            });


                }
            }

            @Override
            public void uploadVoiceNote(int position, ProgressBar uploadProgress, ImageView playButton,
                                        ImageView send_state) {
                Message message = messages.get(position);

                if (message.getMessageState() != MessageState.SENT || message.getMessageState() != MessageState.SEEN) {

                    StorageReference voicenoteFile = chatFiles.child(System.currentTimeMillis() + ".3gp");

                    voicenoteFile.putFile(Uri.fromFile(new File(message.getMessageURL())))
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                    send_state.setImageResource(R.drawable.check_white);

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Toast.makeText(ChatPage.this, "Upload success", Toast.LENGTH_SHORT).show();


                                    message.setMessageState(MessageState.SENT);
                                    send_state.setImageResource(R.drawable.not_seen_check);


                                    // adapter.notifyItemChanged(position);
                                    voicenoteFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri recordUri) {
                                            CloudMessage cloudMessage = new CloudMessage(message.getMessageTYPE(), myPhoneNumber,
                                                    message.getMessageTIME(),
                                                    message.getMessageCONTENT(), recordUri.toString(), MessageState.SENT);

                                            message.setMessageState(MessageState.SENT);
                                            viewModel.updateMessage(message);
                                            uploadGenerally(cloudMessage, message.getMessageID());
                                            //String newURL=message.getMessageURL()+","+recordUri.toString();

                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    message.setMessageState(MessageState.FAILED);
                                    viewModel.updateMessage(message);
                                    send_state.setImageResource(R.drawable.check_red);

                                }
                            });


                }


            }

            @Override
            public void messageIsSeen(int position, ImageView indicator) {

                Message message = messages.get(position);
                if (message.getMessageState() != MessageState.SEEN) {
                    updateMessageAsSeen(message, indicator);
                }


            }


            @Override
            public void downloadVoiceNote(int position, ProgressBar progressBar,
                                          ImageView downloadButton, ImageView replaceButton) {
                Message message = messages.get(position);


                File downloadedFile = new File(message.getMessageURL());
                if (!downloadedFile.exists()) {
                    String messageURL = message.getMessageURL();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    DatabaseReference channels = FirebaseDatabase.getInstance().getReference("ChatChannels")
                            .child(thisChatChannel.getChatChannelID()).child("messages").child(message.getMessageID());
                    channels.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                CloudMessage cloudMessage = dataSnapshot.getValue(CloudMessage.class);
                                StorageReference storageRef = storage.getReferenceFromUrl(cloudMessage.getMessageURL());

                                String destination = privatepath + "/recording" + System.currentTimeMillis() + ".3gp";
                                File voiceFile = null;
                                // File voiceFile=File.createTempFile(String.valueOf(System.currentTimeMillis()),".3gp",new File(outputFile));
                                try {
                                    voiceFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".3gp", new File(outputFile));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                File finalVoiceFile = voiceFile;

                                storageRef.getFile(voiceFile)
                                        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                int progress = (int) (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount());
                                                progressBar.setVisibility(View.VISIBLE);
                                                downloadButton.setVisibility(View.GONE);
                                                progressBar.setProgress(progress);


                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                replaceButton.setVisibility(View.VISIBLE);
                                                downloadButton.setVisibility(View.INVISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                progressBar.setProgress(0);

                                                message.setMessageURL(finalVoiceFile.getAbsolutePath());
                                                message.setMessageState(MessageState.SEEN);
                                                String newUrl = finalVoiceFile.getAbsolutePath();
                                                messages.set(position, message);
                                                adapter.notifyItemChanged(position);

                                                viewModel.updateMessage(message);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                replaceButton.setVisibility(View.INVISIBLE);
                                                downloadButton.setVisibility(View.VISIBLE);
                                                message.setMessageState(MessageState.FAILED);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                //  viewModel.updateMessage(message);

                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }


            @Override
            public void OnclickChatProfileImage(int position) {
                Message message = (Message) messages.get(position);
                PartialProfile partialProfile = PartialProfile.getInstance(message.getMessageOwnerID());
                partialProfile.setFromChat(true);
                partialProfile.setOnclickMessageBox(new PartialProfile.OnClickMessageBox() {
                    @Override
                    public void OnClickMessage() {
                        if (message.getMessageOwnerID().equals(chatID)) {
                            partialProfile.dismiss();
                        } else {

                            Intent intent = new Intent();

                            realtimeUser.child(message.getMessageOwnerID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                                            intent.putExtra("username", userDetail.getUsername());
                                            intent.putExtra("profile picture", userDetail.getProfileURL());
                                            intent.putExtra("phone number", userDetail.getPhone());


                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            setIntent(intent);
                                            recreate();
                                            partialProfile.dismiss();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        }
                    }
                });
                partialProfile.show(getSupportFragmentManager(), "partial profile");

            }

            @Override
            public void nameChoiceUser(int position, TextView nameTextView, CircleImageView profileImageView) {
                Message message = messages.get(position);

                profileImageView.setVisibility(View.GONE);

                if (chat_type == ChannelTypes.GROUP_CHAT) {

                    realtimeUser.child(new UserInformation(ChatPage.this).getPhoneNumber())
                            .child("people").child(message.getMessageOwnerID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);

                                        nameTextView.setText(contact.getUserContactName());
                                      /*  Glide.with(getApplicationContext()).load(contact.getUserImageUrl())
                                                .thumbnail(0.4f).into(profileImageView);

                                       */

                                    } else {

                                        UserNaming userNaming = UserNaming.getInstance();
                                        userNaming.nameThisUser(message.getMessageOwnerID(), ChatPage.this, nameTextView, profileImageView);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                } else {
                    nameTextView.setVisibility(View.GONE);
                  /*  Glide.with(getApplicationContext())
                            .load(chatpageProfilePic.getDrawable())
                            .into(profileImageView);

                   */
                }


            }

            @Override
            public int currentlyPlaying() {

                return currentlyPlaying;
            }

            @Override
            public void playPauseButton(int position, ImageView playPauseButton, ProgressBar voiceNoteProgress, TextView durationCount
                    , String path) throws IOException {

                Message message = messages.get(position);
                String messageContent = message.getMessageCONTENT().split(String.valueOf(message.getMessageTIME()))[0];
                if (m == null) {

                    streamAudio(message.getMessageURL(), voiceNoteProgress, playPauseButton, durationCount);
                    currentlyPlaying = position;


                } else {
                    if (currentlyPlaying == position && m.isPlaying()) {
                        m.pause();

                        setPauseProgress(voiceNoteProgress, durationCount, message.getMessageCONTENT());
                        playPauseButton.setImageResource(R.drawable.play_filled);
                        currentlyPlaying = position;
                    } else if (currentlyPlaying == position && !m.isPlaying()) {

                        m.start();
                        playPauseButton.setImageResource(R.drawable.pause_filled);


                        currentlyPlaying = position;

                    } else if (currentlyPlaying != position) {
                        m.release();
                        timer.cancel();
                        voiceNoteProgress.setProgress(0);

                        streamAudio(message.getMessageURL(), voiceNoteProgress, playPauseButton, durationCount);
                        adapter.setStopPlaying(currentlyPlaying);
                        adapter.notifyItemChanged(currentlyPlaying);
                        adapter.notifyDataSetChanged();
                        currentlyPlaying = position;


                    }

                }


            }


            @Override
            public boolean showName() {
                return showName;
            }

            @Override
            public void onClickPrivateCommentMessage(int position) {
                if (replyHolder.getVisibility() != View.VISIBLE) {
                    Message privateCommentMessage = messages.get(position);
                    if (privateCommentMessage.getMessageTYPE() == MessageType.PRIVATE_COMMENT_IMAGE_SINGLE ||
                            privateCommentMessage.getMessageTYPE() == MessageType.PRIVATE_COMMENT_TEXT
                            || privateCommentMessage.getMessageTYPE() == MessageType.PRIVATE_COMMENT_IMAGE_SINGLE + MessageType.RECIEVED ||
                            privateCommentMessage.getMessageTYPE() == MessageType.PRIVATE_COMMENT_TEXT + MessageType.RECIEVED) {


                        String[] content = privateCommentMessage.getMessageCONTENT().split(String.valueOf(privateCommentMessage.getMessageTIME()));
                        String postID = content[2];
                        //Toast.makeText(ChatPage.this, postID, Toast.LENGTH_SHORT).show();
                        DatabaseReference privateCommentPost = FirebaseDatabase.getInstance().getReference("post");
                        privateCommentPost.child(postID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);

                                            dataSnapshot.getRef().child("postData")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            PostInfo data = dataSnapshot.getValue(PostInfo.class);
                                                            Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(),
                                                                    cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME(),
                                                                    data.getPostIsOnRepeat(), myPhoneNumber, data.isAllowPrivateComment());


                                                            Intent intent = new Intent(ChatPage.this, CommentPage.class);
                                                            intent.putExtra("postData", post);
                                                            startActivity(intent);


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(ChatPage.this, "This post is no longer Available",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }
                }

            }

            @Override
            public void downloadFiles(int position, ProgressBar progressBar,
                                      ImageView downloadButton, ImageView imagePreview) {

                Message message = messages.get(position);
                //imagePreview.setImageDrawable(getResources().getDrawable(R.drawable.image_thumnail));

                FirebaseStorage storage = FirebaseStorage.getInstance();
                String[] splitted = message.getMessageURL().split(",");

                StorageReference storageRef = storage.getReferenceFromUrl(splitted[splitted.length - 1]);


                File voiceFile = null;
                try {
                    String[] contentsplit = message.getMessageCONTENT().split(",");
                    voiceFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), contentsplit[0], new File(outputFile));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                File finalVoiceFile = voiceFile;

                storageRef.getFile(voiceFile)
                        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int) (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress(progress);
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                downloadButton.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                Glide.with(getApplicationContext()).load(finalVoiceFile.getAbsoluteFile()).placeholder(R.drawable.image_thumnail)
                                        .into(imagePreview);

                                String finalUrl = finalVoiceFile.getAbsolutePath() + "," + splitted[splitted.length - 1];

                                message.setMessageURL(finalUrl);
                                message.setMessageState(MessageState.SEEN);


                                messageCopy.child(message.getMessageID());


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                downloadButton.setVisibility(View.VISIBLE);
                                message.setMessageState(MessageState.FAILED);
                                progressBar.setVisibility(View.INVISIBLE);
                                //viewModel.updateMessage(message);


                            }
                        });


            }

            @Override
            public void onClickImageMessage(int position) {

                if (replyHolder.getVisibility() != View.VISIBLE) {
                    Message message = messages.get(position);

                    if (message.getMessageTYPE() == MessageType.IMAGE || message.getMessageTYPE() == MessageType.IMAGE + MessageType.RECIEVED) {


                        String url = message.getMessageURL().split(",")[0];
                        Intent intent = new Intent(ChatPage.this, ImageViewer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("url", url);
                        intent.putExtra("owner", message.getMessageOwnerID());
                        intent.putExtra("time", message.getMessageTIME());
                        intent.putExtra("state", message.getMessageState());
                        intent.putExtra("chat id", chatID);
                        intent.putExtra("message content", message.getMessageCONTENT());
                        intent.putExtra("message content", message.getMessageCONTENT());
                        intent.putExtra("chat type", chat_type);
                        startActivity(intent);


                    } else if (message.getMessageTYPE() == MessageType.VIDEO ||
                            message.getMessageTYPE() == MessageType.VIDEO + MessageType.RECIEVED) {

                        String url = message.getMessageURL().split(",")[0];
                        Intent intent = new Intent(ChatPage.this, ChatVideoViewer.class);
                        intent.putExtra("url", url);
                        intent.putExtra("state", 1);
                        intent.putExtra("message content", message.getMessageCONTENT().split(",")[0]);
                        startActivity(intent);


                    }


                }
            }


        });
        adapter.setMessage(messages);

        // loadWithLocalDb();
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        messageContainer.setHasFixedSize(true);

        messageContainer.setLayoutManager(manager);

        messageContainer.setAdapter(adapter);

    }

    public void setPauseProgress(ProgressBar progress, TextView durationCount, String duration) {
        int position = (int) (((float) m.getCurrentPosition() / (float) m.getDuration()) * 100);
        progress.setProgress(position);
        durationCount.setText(milliSecondsToTimer(getCurrentDuration((int) progress.getProgress()
                , Long.parseLong(duration) / 1000)));

    }

    private long getCurrentDuration(int position, long duration) {
        return (long) ((float) position * duration / 100);
    }

    private String milliSecondsToTimer(long milliseconds) {
        String timerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (60 * 60));
        int minutes = (int) (milliseconds & (60 * 60)) / (60);
        int seconds = (int) milliseconds % 60;

        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds > 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }


    private Timer timer;

    private void timerCounter(ProgressBar videoviewSeekbar, TextView videViewDuration
            , int audioDuration) {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(videoviewSeekbar, videViewDuration, audioDuration);

                    }
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }

    private void updateUI(ProgressBar videviewSeekbar, TextView videoViewDuration, int audioDuration) {
        if (videviewSeekbar.getProgress() >= 100) {
            timer.cancel();
        }
        if (m != null) {
            int current = m.getCurrentPosition();
            videoViewDuration.setText(TimeFactor.convertMillieToHMmSs(current));
            int progress = current * 100 / audioDuration;
            videviewSeekbar.setProgress(progress);
        }
    }

    public void streamAudio(String path, ProgressBar progressBar, ImageView playButton,
                            TextView playDurationTextView) throws IOException {
        m = new MediaPlayer();
        try {
            m.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playButton.setImageResource(R.drawable.pause_filled);


        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                progressBar.setMax(100);
                timerCounter(progressBar, playDurationTextView, m.getDuration());

            }
        });
        m.prepareAsync();


        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                progressBar.setProgress(0);
                playButton.setImageResource(R.drawable.play_filled);
                //playDurationTextView.setText(milliSecondsToTimer(audioDuration));
                if (timer != null) {
                    timer.cancel();
                }
                m.release();
                m = null;


            }
        });


    }


    private void loadTheme() {
        chatEditTextArea = findViewById(R.id.chatEditTextArea);
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {chatPageUerName, chatPageLastSeen, replyMessageHolder};

        setTextColors(textViews, currentTheme);

    }

    private void updateMessagesIveSeen(String id) {

        if (chatChannelID != null) {
            boolean readReciept = chatSettings.getBoolean("message receipt", true);
            if (readReciept) {
                DatabaseReference updateSeenMessageRef = chatChannels.child(thisChatChannel.getChatChannelID()).child("messages").child(id);

                Map<String, Object> updateSeenMessage = new HashMap<>();
                updateSeenMessage.put("messageState", MessageState.SEEN);
                updateSeenMessageRef.updateChildren(updateSeenMessage);
                messageCopy.child(id).updateChildren(updateSeenMessage);
            }
        }

    }

    private void updateMessageAsSeen(Message message, ImageView state) {


        if (thisChatChannel != null) {
            DatabaseReference allChatMessages = chatChannels.child(thisChatChannel.getChatChannelID()).child("messages");
            allChatMessages.child(message.getMessageID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                CloudMessage cloudMessage = dataSnapshot.getValue(CloudMessage.class);
                                Message seenMessage = message;
                                if (cloudMessage.getMessageState() == MessageState.SEEN) {
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("messageState", MessageState.SEEN);
                                    messageCopy.child(message.getMessageID()).updateChildren(update);
                                    seenMessage.setMessageState(cloudMessage.getMessageState());
                                    state.setImageResource(R.drawable.check);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    }


    private final int SEND_MESSAGE_REQUEST_CODE = 150;

    private List<String> markedImages = new ArrayList<>();

    private List<Integer> imageTypes = new ArrayList<>();


    public void openSocialProfile() {

        realtimeUser.child(chatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                            Intent intent = new Intent(ChatPage.this, SocialProfile.class);
                            intent.putExtra("user", detail);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    CountDownTimer countDownTimer;

    private void onclickAndChange() {


        MakePostAdapter makePostAdapter = new MakePostAdapter(this, images);
        makePostAdapter.setShowMarkings(true);

        BottomMakePost gallery = BottomMakePost.getInstance();
        gallery.setShowDone(true);
        gallery.setShowCount(false);
        gallery.setShowMarkings(true);


        makePostAdapter.setOnItemClickListener(new MakePostAdapter.OnclickListener() {
            @Override
            public void onClickImage(int position, ImageView mark) {
                GalleryItem galleryItem = images.get(position);
                String url = galleryItem.getFileURL();

                if (markedImages.contains(url)) {
                    if (markedImages.size() == 1) {

                        markedImages.clear();
                        mark.setVisibility(View.INVISIBLE);
                        gallery.setShowMarking(markedImages);
                        imageTypes.clear();
                    } else {

                        markedImages.remove(url);
                        mark.setVisibility(View.INVISIBLE);
                        gallery.setShowMarking(markedImages);
                        imageTypes.remove(galleryItem.getFileType());
                    }

                } else {
                    if (markedImages.size() < 4) {
                        mark.setVisibility(View.VISIBLE);
                        markedImages.add(url);
                        gallery.setShowMarking(markedImages);
                        imageTypes.add(galleryItem.getFileType());
                    } else {
                        Toast.makeText(ChatPage.this, "You can only send 4 at a time", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        gallery.setAdapter(makePostAdapter);

        gallery.setOnClickDone(() -> {

            if (markedImages.isEmpty()) {
                Toast.makeText(ChatPage.this, "You have'nt marked any images", Toast.LENGTH_SHORT).show();
            } else {


                int i = 0;
                for (String url : markedImages) {
                    int type = imageTypes.get(i);
                    String content = null;
                    int messageType = MessageType.IMAGE;
                    if (type == 2) {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(this, Uri.parse(url));
                        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeDuration = Long.parseLong(duration);
                        content = Functions.fileExtension(url) + "," + TimeFactor.convertMillieToHMmSs(timeDuration);

                        messageType = MessageType.VIDEO;
                    } else if (type == 1) {
                        messageType = MessageType.IMAGE;
                        content = Functions.fileExtension(url);
                    }

                    CloudMessage message =
                            new CloudMessage(messageType,
                                    new UserInformation(ChatPage.this)
                                            .getMainUserID(),
                                    System.currentTimeMillis(),
                                    content,
                                    url, MessageState.SENDING);
                    Message absoluteMessage = new Message("", chatID, message.getMessageTYPE(),
                            message.getMessageOwnerID(),
                            message.getMessageTIME(), message.getMessageCONTENT(),
                            message.getMessageURL(), chat_type, message.getMessageState());


                    firstUploadMyCopy(message, absoluteMessage);

                    i++;
                }


                // gallery.dismiss();
            }


        });


        camera.setOnClickListener(v -> {
            // startActivity(new Intent(this, ChoiceCamera.class));
        });

        //gallery.setImages(images);

        chatPageUserInfo.setOnClickListener(v -> {
            openProfile();
        });

        chatpageProfilePic.setOnClickListener(v -> {
            if (chat_type == ChannelTypes.ONE_TO_ONE_CHAT) {
                openSocialProfile();

            } else {
                openProfile();
            }

        });
        chatPageUerName.setOnClickListener(v -> {
            if (chat_type == ChannelTypes.ONE_TO_ONE_CHAT) {
                openSocialProfile();

            } else {
                openProfile();
            }
        });

        mic.setOnClickListener(v -> {
            Toast.makeText(this, "Long press to record", Toast.LENGTH_SHORT).show();
        });

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        startTime = System.currentTimeMillis();
                        writeMessage.setVisibility(View.INVISIBLE);
                        micFloat.setVisibility(View.VISIBLE);

                        chatVoiceNoteCounter.setVisibility(View.VISIBLE);
                        chatVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());
                        chatVoiceNoteCounter.start();
                        emoji.setVisibility(View.INVISIBLE);
                        countDownTimer = new CountDownTimer((int) (maxRecordingDuration * 1000 * 60), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {


                            }

                            @Override
                            public void onFinish() {
                                chatVoiceNoteCounter.stop();
                                chatVoiceNoteCounter.setVisibility(View.INVISIBLE);
                                endTime = (int) (maxRecordingDuration * 1000 * 60);

                                recorder.stop();

                            }
                        }.start();


                        YoYo.with(Techniques.Pulse).duration(1000).repeat(YoYo.INFINITE).playOn(micFloat);
                        //YoYo.with(Techniques.Pulse).duration(700).repeat(YoYo.INFINITE).playOn(chatVoiceNoteCounter);
                        startRecording();


                        break;
                    case MotionEvent.ACTION_UP:

                        writeMessage.setVisibility(View.VISIBLE);
                        emoji.setVisibility(View.VISIBLE);
                        chatVoiceNoteCounter.setBase(SystemClock.elapsedRealtime());
                        chatVoiceNoteCounter.stop();
                        micFloat.setVisibility(View.INVISIBLE);
                        chatVoiceNoteCounter.setVisibility(View.GONE);
                        stopRecording();

                        break;
                }
                return true;
            }
        });

        chatGallery.setOnClickListener(v -> {

            gallery.setImages(images);
            // gallery.show(getSupportFragmentManager(), "BottomGallery");
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.rootView, gallery).addToBackStack(null).commit();

        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        emoji.setOnClickListener(v -> {
            emojiPopup.toggle();
            emoji.setVisibility(View.INVISIBLE);
            keyboard.setVisibility(View.VISIBLE);
        });

        keyboard.setOnClickListener(v -> {
            emojiPopup.toggle();
            emoji.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.INVISIBLE);
        });

        writeMessage.setOnClickListener(v -> {
            if (emojiPopup.isShowing()) {
                emojiPopup.toggle();
                emoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.INVISIBLE);
            }
        });

        //__________________________--************************************_____________________-----------
        writeMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    //  camera.setVisibility(View.VISIBLE);

                    send.setVisibility(View.INVISIBLE);
                    amTyping(false);

                } else {
                    // camera.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                    amTyping(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    //camera.setVisibility(View.VISIBLE);
                    //mic.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                    amTyping(false);

                } else {
                    // camera.setVisibility(View.GONE);
                    // mic.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                    amTyping(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().isEmpty()) {
                    amTyping(false);
                } else {
                    amTyping(true);
                }

            }
        });

        replyCancelButton.setOnClickListener(v -> {
            replyHolder.setVisibility(View.GONE);
            generalMessageType = 0;
            messageToReply = null;
        });


        // optionsOnclickListeners();

    }

    ChatPageOption.leaveGroupClickListener listener = new ChatPageOption.leaveGroupClickListener() {
        @Override
        public void leaveGroupClick() {

        }
    };

    private void openProfile() {

        ChatPageOption chatPageOption = new ChatPageOption();
        chatPageOption.setChatID(chatID);
        chatPageOption.setLeaveGroupClickListener(listener);

        switch (chat_type) {
            case ChannelTypes.ONE_TO_ONE_CHAT:
                chatPageOption.setGroupChat(false);
                break;
            case ChannelTypes.GROUP_CHAT:
                chatPageOption.setGroupChat(true);
                break;

        }
        chatPageOption.show(getSupportFragmentManager(), "option");

    }


    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));

        }

    }


    String outputSourceFile;

    public static final double maxRecordingDuration = 1.5;

    public void startRecording() {

        if (recorder == null) {
            recorder = new MediaRecorder();
            outputSourceFile = privatepath + "/" + System.currentTimeMillis() + ".3gp";
            recorder.setOutputFile(outputSourceFile);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

            int maxDurationInMunite = 1000 * 60;
            recorder.setMaxDuration((int) (maxDurationInMunite * maxRecordingDuration));

            amRecording(true);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        isTyping();

        messageContainer.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (adapter.getItemCount() > 10) {
                    if (manager.findLastVisibleItemPosition() <= adapter.getItemCount() - 3) {
                        scrollDown.setVisibility(View.VISIBLE);
                    } else {
                        scrollDown.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });

        //fetchMessage();


    }

    public void stopRecording() {

        if (recorder != null) {
            recorder.release();
            recorder = null;

            long duration = 0;
            if (endTime != 0) {

                duration = endTime;
                endTime = 0;
            } else {
                duration = System.currentTimeMillis() - startTime;
            }

            chatVoiceNoteCounter.stop();

            amRecording(false);
            String text = String.valueOf(duration);
            long messageTime = System.currentTimeMillis();

            if ((duration / 1000) > 1) {

                if (messageToReply != null) {

                    int messageType = messageToReply.getMessageTYPE();
                    if (messageType == MessageType.VIDEO || messageType == MessageType.IMAGE) {
                        String replyContent = messageToReply.getMessageURL().split(",")[1];
                        text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID();
                    } else {
                        String replyContent = messageToReply.getMessageCONTENT().split(String.valueOf(messageToReply.getMessageTIME()))[0];
                        text = text + messageTime + replyContent + messageTime + messageToReply.getMessageOwnerID();

                    }
                }

                CloudMessage message = new CloudMessage(MessageType.VOICE_NOTE + generalMessageType, new UserInformation(this).getMainUserID(), messageTime,
                        text, outputSourceFile, MessageState.SENDING);
                Message absoluteMessage = new Message("", chatID, message.getMessageTYPE(),
                        message.getMessageOwnerID(),
                        message.getMessageTIME(), message.getMessageCONTENT(),
                        message.getMessageURL(), chat_type, message.getMessageState());

                messages.add(absoluteMessage);
                adapter.notifyDataSetChanged();
                firstUploadMyCopy(message, absoluteMessage);
                // uploadMessage(message,absoluteMessage);

            }

            replyHolder.setVisibility(View.GONE);
            messageToReply = null;
            generalMessageType = 0;
            replyContentHolder.setVisibility(View.GONE);
        }

    }

    SharedPreferences chatSettings;


    private void amTyping(boolean state) {

        boolean chatActivity = chatSettings.getBoolean("chat activity", true);
        if (chatActivity) {
            if (chatChannelID != null) {
                Map<String, Object> myActivity = new HashMap<>();
                myActivity.put("typing", state);
                channelActivity.updateChildren(myActivity);
            }
        }
    }

    String chatChannelID;

    private void amRecording(boolean state) {

        boolean chatActivity = chatSettings.getBoolean("chat activity", true);
        if (chatActivity) {
            if (chatChannelID != null) {
                Map<String, Object> myActivity = new HashMap<>();
                myActivity.put("recording", state);
                channelActivity.updateChildren(myActivity);
            }
        }
    }


    private void firstUploadMyCopy(CloudMessage message, Message absoluteMessage) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (!dataSnapshot.exists()) {
                    DatabaseReference ourChat = chatChannels.push();
                    DatabaseReference sendMessage = ourChat.child("messages");
                    String channelID = ourChat.getKey();
                    Channels yours = new Channels(channelID, System.currentTimeMillis(), ChannelTypes.ONE_TO_ONE_CHAT, false, false);
                    Channels mine = new Channels(channelID, System.currentTimeMillis(), ChannelTypes.ONE_TO_ONE_CHAT, false, false);
                    chatFolder.child(chatID).setValue(mine);
                    otherChatFolder.child(myPhoneNumber).setValue(yours);

                    DatabaseReference messageRef = messageCopy.push();

                    absoluteMessage.setMessageID(messageRef.getKey());
                    //viewModel.insertMessage(absoluteMessage);
                    sendMessage.child(messageRef.getKey()).setValue(message);
                    messageRef.setValue(message);
                    //sendToAdapter(messageRef, message);
                    CHANNEL_ID = channelID;

                } else {

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Channels channel = dataSnapshot.getValue(Channels.class);


                            DatabaseReference messageToSend = FirebaseDatabase.getInstance().
                                    getReference("ChatChannels").child(channel.getChatChannelID()).child("messages");
                            DatabaseReference newMessage = messageToSend.push();

                            DatabaseReference messageRef = messageCopy;
                            absoluteMessage.setMessageID(newMessage.getKey());
                            // viewModel.insertMessage(absoluteMessage);
                            messages.add(absoluteMessage);
                            messageIDs.add(absoluteMessage.getMessageID());
                            adapter.notifyDataSetChanged();

                            messageRef.child(newMessage.getKey()).setValue(message);
                            newMessage.setValue(message);
                            //sendToAdapter(newMessage, message);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void uploadGenerally(CloudMessage message, String id) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Channels channel = dataSnapshot.getValue(Channels.class);

                DatabaseReference messageToSend = FirebaseDatabase.getInstance().
                        getReference("ChatChannels").child(channel.getChatChannelID()).child("messages");
                messageToSend.child(id).setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference myCopy = dataSnapshot.getRef().child("messages").child(id);
                                Map<String, Object> update = new HashMap<>();
                                update.put("messageState", MessageState.SENT);


                                myCopy.updateChildren(update);
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
