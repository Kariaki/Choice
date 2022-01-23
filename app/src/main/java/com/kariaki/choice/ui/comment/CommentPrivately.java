package com.kariaki.choice.ui.comment;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.entities.CloudMessage;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.ui.chat.ChannelTypes;
import com.kariaki.choice.ui.chat.Channels;

public class CommentPrivately {


    public static void sendComment(String message, String receiver, String sender, int type, Context context) {
        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("users").child(sender).child("chats");
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Channels channel = dataSnapshot.getValue(Channels.class);

                    DatabaseReference messages = dataSnapshot.getRef().child("messages");
                    DatabaseReference chatChannel = FirebaseDatabase.getInstance().getReference("ChatChannels").child(channel.getChatChannelID()).child("messages");
                    String messageID = chatChannel.push().getKey();
                    CloudMessage cloudMessage = new CloudMessage(type, sender, System.currentTimeMillis(), message, "", MessageState.SENT);
                    messages.child(messageID).setValue(cloudMessage);
                    chatChannel.child(messageID).setValue(cloudMessage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Sending comment", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    sendFirstMessage(message, receiver, sender, type);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static void sendFirstMessage(String message, String receiver, String sender, int type) {

        DatabaseReference chatChannels = FirebaseDatabase.getInstance().getReference("ChatChannels");
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference firstPerson = users.child(sender).child("chats");

        DatabaseReference secondPerson = users.child(receiver).child("chats");
        DatabaseReference ourChat = chatChannels.push();
        String channelID = ourChat.getKey();
        Channels channels = new Channels(channelID, System.currentTimeMillis(), ChannelTypes.ONE_TO_ONE_CHAT, false, false);
        DatabaseReference mychats = firstPerson.child(receiver);
        mychats.setValue(channels);
        secondPerson.child(sender).setValue(channels);
        CloudMessage cloudMessage = new CloudMessage(type, sender, System.currentTimeMillis(), message, "", MessageState.SENT);

        DatabaseReference firstMessage = ourChat.child("messages");
        String messageID = firstMessage.getKey();
        firstMessage.push().setValue(cloudMessage);
        mychats.child(messageID).setValue(cloudMessage);


    }
}
