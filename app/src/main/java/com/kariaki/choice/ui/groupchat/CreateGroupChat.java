package com.kariaki.choice.ui.GroupChat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kariaki.choice.MainActivity;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.CloudMessage;
import com.kariaki.choice.model.database.entities.GroupChatInformation;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Chat.ChannelTypes;
import com.kariaki.choice.ui.Chat.Channels;
import com.kariaki.choice.ui.MainPage.Adapters.ContactAndFriendsAdapter;
import com.kariaki.choice.ui.util.Theme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupChat extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGroupChatsDirectory;
    List<String> groupChatMembers = new ArrayList<>();
    DatabaseReference addedUserChat;
    DatabaseReference allUsers = database.getReference("users");

    Channels addedUserGroupChatChannel;
    DatabaseReference chatChannels;
    EditText GroupChatName;
    String imageUrl;
    RelativeLayout changeGroupIcon, createGroupChatSendButton, groupChatRootView;
    StorageReference profileURL = FirebaseStorage.getInstance().getReference("ProfilePictures");
    GroupChatInformation chatInformation;
    ChoiceViewModel choiceViewModel;
    List<ChoiceUser> addedChoiceUsers = new ArrayList<>();
    CircleImageView groupChatProfilePicture;
    private ContactAndFriendsAdapter contactAndFriendsAdapter;
    private RecyclerView currentlyAddedMemberList;
    TextView addedMembersInfo, selectedmemberTextview, groupChatText;
    final String selectedMembers = "selected members ";
    private ImageButton createGroupChatBackbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);

        allGroupChatsDirectory = database.getReference("GroupChats");
        changeGroupIcon = findViewById(R.id.changeGroupIcon);
        GroupChatName = findViewById(R.id.GroupChatName);
        selectedmemberTextview = findViewById(R.id.selectedmemberTextview);
        groupChatProfilePicture = findViewById(R.id.groupChatProfilePicture);
        choiceViewModel = ChoiceViewModel.getInstance(getApplication());
        addedMembersInfo = findViewById(R.id.addedMembersInfo);
        groupChatRootView = findViewById(R.id.groupChatRootView);
        groupChatText = findViewById(R.id.groupChatText);
        createGroupChatSendButton = findViewById(R.id.createGroupChatSendButton);
        createGroupChatBackbutton = findViewById(R.id.createGroupChatBackbutton);
        currentlyAddedMemberList = findViewById(R.id.currentlyAddedMemberList);
        contactAndFriendsAdapter = new ContactAndFriendsAdapter();

        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQUf9QPA3pe7BY91OIKO_4xqJfLRsPWJyHDRQ&usqp=CAU";

        chatChannels = database.getReference("ChatChannels");
        groupChatMembers = getIntent().getStringArrayListExtra("selectedUsersList");
        addMembersToUsers();
        String message = "All contacts will be added to this group chat and you alone will be the admin";

        addedMembersInfo.setText(message);
        contactAndFriendsAdapter.setParticularUser(addedChoiceUsers);
        contactAndFriendsAdapter.setShowMarking(true);
        contactAndFriendsAdapter.setMarkAll(true);
        selectedmemberTextview.setText(selectedmemberTextview.getText().toString() + " " + groupChatMembers.size());
        contactAndFriendsAdapter.setOnClickUser(new ContactAndFriendsAdapter.OnClickUser() {

            @Override
            public void OnclickUserListener(int position, ImageView marker, CircleImageView profile) {
                addedChoiceUsers.remove(addedChoiceUsers.get(position));
                contactAndFriendsAdapter.notifyItemRemoved(position);
                groupChatMembers.remove(groupChatMembers.get(position));
                selectedmemberTextview.setText(selectedMembers + groupChatMembers.size());

            }

            @Override
            public void OnCheckChangeListener(int position) {

               /* if (!isChecked) {
                    addedChoiceUsers.remove(addedChoiceUsers.get(position));
                    contactAndFriendsAdapter.notifyItemRemoved(position);
                    groupChatMembers.remove(groupChatMembers.get(position));
                }

                */

            }
        });
        currentlyAddedMemberList.setHasFixedSize(true);
        currentlyAddedMemberList.setLayoutManager(new LinearLayoutManager(this));
        currentlyAddedMemberList.setAdapter(contactAndFriendsAdapter);

        GroupChatName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    createGroupChatSendButton.setVisibility(View.INVISIBLE);
                } else {
                    createGroupChatSendButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    createGroupChatSendButton.setVisibility(View.INVISIBLE);
                } else {
                    createGroupChatSendButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    createGroupChatSendButton.setVisibility(View.INVISIBLE);
                } else {
                    createGroupChatSendButton.setVisibility(View.VISIBLE);
                }
            }
        });

        loadTheme();

    }

    public void backButtonPress(View view) {
        onBackPressed();
    }

    public void addMembersToUsers() {
        for (String phoneNumber : groupChatMembers) {
            choiceViewModel.getChoiceUser(phoneNumber).observe(this, new Observer<List<ChoiceUser>>() {
                @Override
                public void onChanged(List<ChoiceUser> choiceUsers) {
                    addedChoiceUsers.addAll(0, choiceUsers);
                }
            });
        }
    }

    public void addUserToGroupChat(String phoneNumber, String channelID, String chatChannelID, int messageseen) {
        addedUserChat = allUsers.child(phoneNumber).child("chats");
        addedUserGroupChatChannel = new Channels(chatChannelID, System.currentTimeMillis(), ChannelTypes.GROUP_CHAT, false, false);
        addedUserChat.child(channelID).setValue(addedUserGroupChatChannel);


    }

    UserInformation information;

    public void createNewGroupChat(View view) {


        information = new UserInformation(this);
        String groupChatName = GroupChatName.getText().toString();

        if (!groupChatName.isEmpty()) {
            DatabaseReference newGroupChatChannel = chatChannels.push();

            newGroupChatChannel.setValue(newGroupChatChannel.getKey());
            // GroupChatInformation chatInformation=new GroupChatInformation(information.getMainUserID(),System.currentTimeMillis(),);
            DatabaseReference newGroupChat = allGroupChatsDirectory.push(); //remain to set value
            String description = "No description added yet. ";
            chatInformation = new GroupChatInformation(information.getMainUserID(), System.currentTimeMillis(), imageUrl, newGroupChatChannel.getKey(), groupChatName
                    , description);

            newGroupChat.setValue(chatInformation);

            GroupMember member = new GroupMember(information.getMainUserID(), true);
            // newGroupChat.child("members").child(information.getMainUserID()).setValue(member);
            String myPhoneNumber = new UserInformation(this).getMainUserID();
            String myMessageContent = "you created this Group chat " + groupChatName;

            CloudMessage myMessage = new CloudMessage(MessageType.GROUP_CHAT_FIRST_MESSAGE,
                    information.getMainUserID(), System.currentTimeMillis(), myMessageContent, "",
                    MessageState.SENT);


            addedUserChat = allUsers.child(myPhoneNumber).child("chats");
            addedUserGroupChatChannel = new Channels(newGroupChatChannel.getKey(), System.currentTimeMillis(), ChannelTypes.GROUP_CHAT, false, false);
            DatabaseReference myCopy = addedUserChat.child(newGroupChat.getKey());
            myCopy.setValue(addedUserGroupChatChannel);
            myCopy.child("messages").push().setValue(myMessage);

            for (String phoneNumber : groupChatMembers) {

                addUserToGroupChat(phoneNumber, newGroupChat.getKey(), newGroupChatChannel.getKey(), 0);
                member = new GroupMember(phoneNumber, false);
                newGroupChat.child("members").child(phoneNumber).setValue(member);

            }
            DatabaseReference firstMessage = newGroupChatChannel.child("messages").push();

            //firstMessage.setValue(message);
            Intent filter = new Intent(this, MainActivity.class);
            filter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(filter, 504);


        }
    }

    // stop new members from retrieving all old messages before them
    // to do that we need to start retrieving messages at time of group chat creation

    private final int PICK_IMAGE = 101;

    public void pickImage(View view) {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select pictures"), PICK_IMAGE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Glide.with(getApplicationContext()).load(uri.toString()).into(groupChatProfilePicture);
            changeGroupIcon.setVisibility(View.INVISIBLE);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
            byte[] sendingBytes = outputStream.toByteArray();
            StorageReference newProfilePicture = FirebaseStorage.getInstance().getReference("ProfilePictures")
                    .child(System.currentTimeMillis() + getFileExtension(uri));

            newProfilePicture.putBytes(sendingBytes).addOnSuccessListener(taskSnapshot -> {
                newProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();

                    }
                });
            });

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }


    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                groupChatRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                contactAndFriendsAdapter.setTextColor((R.color.textContext));
                GroupChatName.setTextColor(getResources().getColor(R.color.textContext));

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                GroupChatName.setTextColor(getResources().getColor(R.color.whiteGreen));
                contactAndFriendsAdapter.setTextColor(R.color.whiteGreen);
                groupChatRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {groupChatText};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


}
