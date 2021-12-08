package com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatInvite;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.Chat.Channels;
import com.kariaki.choice.UI.GroupChat.GroupMember;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Messaging.ChatViewHolders.ChatMainViewHolder;


public class GroupChatInviteSent extends ChatMainViewHolder {
    TextView groupInviteGroupChatName,groupInviteAccept;
    RelativeLayout rootView;
    public GroupChatInviteSent(@NonNull View itemView) {
        super(itemView);
        groupInviteGroupChatName=itemView.findViewById(R.id.groupInviteGroupChatName);
        groupInviteAccept=itemView.findViewById(R.id.groupInviteAccept);
        rootView=itemView.findViewById(R.id.rootView);
    }

    UserInformation information;
    @Override
    public void bindType(MessageType type, Context context, ChatAdapter.OnclickListener onclickListener, int currentlyPlaying) {

        Message message=(Message)type;
        String[] messageContent=message.getMessageCONTENT().split(",");
        information=new UserInformation(context.getApplicationContext());

        DatabaseReference groupchats=FirebaseDatabase.getInstance().getReference("GroupChats").child(messageContent[0])
                .child("members");
        groupchats.child(information.getMainUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            groupInviteAccept.setOnClickListener(v->{

                                String acceptedMessage="Invite Accepted";
                                if(!groupInviteAccept.getText().toString().equals(acceptedMessage)){
                                    addToChats(messageContent[0],messageContent[1],context);
                                    groupInviteAccept.setText(acceptedMessage);
                                }

                            });
                        }else {
                            groupInviteAccept.setText("Invite Accepted");
                            rootView.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public void progressChange(ChatAdapter.onProgressChangeListener listener) {

    }

    private void addToChats(String groupChatID,String groupChatChannelID,Context context){

        DatabaseReference myChats= FirebaseDatabase.getInstance().getReference("users").child(information.getMainUserID())
                .child("chats");
        Channels channels=new Channels(groupChatChannelID,System.currentTimeMillis(), ChannelTypes.GROUP_CHAT,false,false);
        DatabaseReference groupchats=FirebaseDatabase.getInstance().getReference("GroupChats").child(groupChatID)
                .child("members");
        myChats.child(groupChatID)
                .setValue(channels)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Invite Accepted", Toast.LENGTH_SHORT).show();
                    }
                });
        GroupMember member=new GroupMember(information.getMainUserID(),false);
        groupchats.child(information.getMainUserID()).setValue(member);


    }
    private void checkIfAmMember(String groupChatID){

    }

}
