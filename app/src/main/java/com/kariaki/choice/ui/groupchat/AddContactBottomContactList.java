package com.kariaki.choice.ui.GroupChat;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.CloudMessage;
import com.kariaki.choice.model.database.entities.Contact;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Chat.ChannelTypes;
import com.kariaki.choice.ui.Chat.Channels;
import com.kariaki.choice.ui.MainPage.util.MainFunctions;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AddContactBottomContactList extends BottomSheetDialogFragment {

    RecyclerView Contactlist;
    List<ChoiceUser> choiceUsers = new ArrayList<>();
    AddGroupMemberAdapter addGroupMemberAdapter;
    DatabaseReference myContacts = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    UserInformation information;
    DatabaseReference allGroupChats = FirebaseDatabase.getInstance().getReference("GroupChats");
    private String groupChatID;
    RelativeLayout curveHolder;

    public void setGroupChatID(String groupChatID) {
        this.groupChatID = groupChatID;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_contact_design, null);
        dialog.setContentView(view);
        curveHolder = view.findViewById(R.id.curveHolder);

        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        Contactlist = view.findViewById(R.id.Contactlist);
        addGroupMemberAdapter = new AddGroupMemberAdapter();
        information = new UserInformation(getContext());
        myContacts = myContacts.child(information.getMainUserID()).child("people");
        addMembers();
        addGroupMemberAdapter.setChoiceUsers(choiceUsers);
        addGroupMemberAdapter.setOnAddClickListener(new AddGroupMemberAdapter.addClickListener() {
            @Override
            public void addClick(int position, TextView textView) {
                ChoiceUser user = choiceUsers.get(position);
                //addMemberToGroup(user.getUserPhoneNumber());
                addMember(user.getUserPhoneNumber());
                textView.setVisibility(View.INVISIBLE);
            }
        });
        Contactlist.setLayoutManager(new LinearLayoutManager(getContext()));
        Contactlist.setAdapter(addGroupMemberAdapter);
        reference=users.child(new UserInformation(getContext()).getMainUserID()).child("chats");
        loadTheme();

    }

    private void addMembers() {
        myContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot contacts : dataSnapshot.getChildren()) {
                        Contact contact = contacts.getValue(Contact.class);
                        users.child(contact.getPhoneNumber())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                            ChoiceUser choiceUser = new ChoiceUser(detail.getUsername(), detail.getPhone(), contact.getContactName(),
                                                    detail.getAbout(), detail.getProfileURL());
                                            allGroupChats.child(groupChatID)
                                                    .child("members").child(choiceUser.getUserPhoneNumber())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (!dataSnapshot.exists()) {
                                                                choiceUsers.add(choiceUser);
                                                                addGroupMemberAdapter.notifyDataSetChanged();

                                                            }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addMemberToGroup(String userID) {

        GroupMember member = new GroupMember(userID, false);
        String phone = UserInformation.getInstance(getContext()).getMainUserID();

        //allGroupChats.child(groupChatID).child("members").child(userID).setValue(member);
        allGroupChats.child(groupChatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GroupChatInformation information = dataSnapshot.getValue(GroupChatInformation.class);
                            String groupChatID = dataSnapshot.getKey();
                            String groupChatChannelID = information.getChannelKey();
                            MainFunctions.createGroupChatInvite(phone, userID, groupChatID, groupChatChannelID);
                            Toast.makeText(getContext(), "Contact added", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                // changeTextColors(text, R.color.textContext);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_design_light));
                break;
            case Theme.DEEP:
                // changeTextColors(text, R.color.whiteGreen);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_design_dark));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {
        };
        setTextColors(null, currentTheme);

    }

    private void addMember(String userID){
        DatabaseReference memberChat=FirebaseDatabase.getInstance().getReference("users").child(userID).child("chats");
        GroupMember member = new GroupMember(userID, false);
        String phone = UserInformation.getInstance(getContext()).getMainUserID();
        allGroupChats.child(groupChatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupChatInformation information=dataSnapshot.getValue(GroupChatInformation.class);
                        Channels channels=new Channels(information.getChannelKey(),System.currentTimeMillis(),ChannelTypes.GROUP_CHAT,false,false);
                        String groupChatID=dataSnapshot.getKey();
                        String groupChatChannelID=information.getChannelKey();
                        String message=groupChatID+","+groupChatChannelID;
                        CloudMessage cloudMessage=new CloudMessage(
                                MessageType.GROUP_CHAT_INVITE,new UserInformation(getContext()).getMainUserID(),System.currentTimeMillis(),
                                message,"", MessageState.SENT

                        );
                        createGroupInvite(cloudMessage,userID,dataSnapshot.getKey(),channels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    public void createGroupInvite(CloudMessage message,String userID,String groupChatID,Channels channels) {

        information = new UserInformation(getContext());
        DatabaseReference otherChatFolder=users.child(userID).child("chats");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    DatabaseReference ourChat = chatChannels.push();
                    DatabaseReference sendMessage = ourChat.child("messages");
                    String channelID = ourChat.getKey();
                    DatabaseReference myChatWithYou = reference.child(userID);
                    myChatWithYou.setValue(new Channels(channelID, message.getMessageTIME(), ChannelTypes.ONE_TO_ONE_CHAT, false, false));

                    otherChatFolder.child(groupChatID)
                            .setValue(channels);

                    DatabaseReference messageRef = sendMessage.push();

                    messageRef.setValue(message).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                   // Toast.makeText(getContext(), "Sending comment", Toast.LENGTH_SHORT).show();

                                }
                            }
                    );

                } else {
                    createMessage(message,userID);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int lastSeenMessageCount;

    DatabaseReference chatChannels=FirebaseDatabase.getInstance().getReference("ChatChannels");
    DatabaseReference reference;

    public void createMessage(CloudMessage message,String userid) {


        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Channels value = dataSnapshot.getValue(Channels.class);
                String ID = value.getChatChannelID();
                DatabaseReference sendMessageRef = chatChannels.child(ID)
                        .child("messages");

                DatabaseReference messageRef = sendMessageRef.push();
                DatabaseReference myChats=users.child(new UserInformation(getContext()).getMainUserID()).child("chats").child("messages");

                messageRef.setValue(message).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getContext(), "Sending comment", Toast.LENGTH_SHORT).show();
                                message.setMessageTIME(0);
                                myChats.child(messageRef.getKey()).setValue(message);


                            }
                        }
                );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
