package com.kariaki.choice.ui.mainpage.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.CloudMessage;
import com.kariaki.choice.model.database.entities.Message;
import com.kariaki.choice.model.database.entities.MyChatChannel;
import com.kariaki.choice.model.database.entities.NotificationForPost;
import com.kariaki.choice.model.database.MessageState;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.notification.PushNotification;
import com.kariaki.choice.notification.PushNotificationEntry;
import com.kariaki.choice.ui.chat.ChannelTypes;
import com.kariaki.choice.ui.chat.Channels;

import java.util.HashMap;
import java.util.Map;

public class MainFunctions {

    static DatabaseReference userList = FirebaseDatabase.getInstance().getReference("users");
    static DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");

    public static void fetchBadgeCounter(String myPhoneNumber) {
        DatabaseReference badgeCount = userList.child(myPhoneNumber).child("badge");


        DatabaseReference notificationCount = userList.child(myPhoneNumber).child("notifications");
        userList.child(myPhoneNumber).child("post")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot myPost : dataSnapshot.getChildren()) {
                                if (myPost.exists()) {
                                    postFolder.child(myPost.getKey())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        CloudPost thePost = dataSnapshot.getValue(CloudPost.class);
                                                        if (thePost != null) {
                                                            if (thePost.getOwnerID().equals(myPhoneNumber)) {
                                                                postFolder.child(myPost.getKey()).child("notification")
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()) {
                                                                                    badgeCount.child(myPost.getKey()).setValue(myPost.getKey());
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        notificationCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot notification : dataSnapshot.getChildren()) {

                        NotificationForPost notificationForPost = notification.getValue(NotificationForPost.class);
                        if (!notificationForPost.isSeen()) {
                            badgeCount.child(notificationForPost.getPostID())
                                    .setValue(notificationForPost.getPostID());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void checkForNotificationExistence(String myPhoneNumber) {

        DatabaseReference notificationCount = userList.child(myPhoneNumber).child("notifications");
        notificationCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot notify : dataSnapshot.getChildren()) {
                        NotificationForPost notificationForPost = notify.getValue(NotificationForPost.class);
                        postFolder.child(notificationForPost.getPostID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            notify.getRef().removeValue();
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

    public static void updateNotification(String phoneNumber, String postID) {
        postFolder.child(phoneNumber).child("notification")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postNotification : dataSnapshot.getChildren()) {
                                NotificationForPost notificationForPost = postNotification.getValue(NotificationForPost.class);
                                if (notificationForPost.getPostID().equals(postID) && !notificationForPost.isSeen()) {
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("seen", true);
                                    postNotification.getRef().updateChildren(update);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public static void createNotification(String ownerID, String userID, String postID, String messageDI) {
        if (!ownerID.equals(userID)) {

            String tittle = "commented on your ";
            NotificationForPost notificationForPost = new NotificationForPost(userID,
                    tittle, " post", System.currentTimeMillis(), messageDI, postID, "", false);
            postFolder.child("notifications").push().setValue(notificationForPost);

        }
    }


    public static void fetchChats(String myPhoneNumber, ChoiceViewModel viewModel) {


        DatabaseReference chatChannels = FirebaseDatabase.getInstance().getReference("ChatChannels");

        DatabaseReference myChats = userList.child(myPhoneNumber).child("chats");
        DatabaseReference chatBadge = userList.child(myPhoneNumber).child("chat_badge");
        myChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Channels channel = snapshot.getValue(Channels.class);
                        long lastMessageTime = channel.getLastMessageTime();
                        DatabaseReference channelMessages = snapshot.getRef().child("messages");
                        //number of notification for each chats
                        DatabaseReference newMessages = snapshot.getRef().child("notification");
                        DatabaseReference channelRef = snapshot.getRef();
                        String chatID = snapshot.getKey();
                        MyChatChannel myChatChannel = new MyChatChannel(snapshot.getKey(), channel.getChatChannelID(),
                                channel.getLastMessageTime(), channel.getChannelType(),
                                channel.isMute(), channel.isBlockUser(), null, null, null, null, 0, 0, 0);
                        PushNotification inChat = snapshot.getValue(PushNotification.class);

                        if (!myChatChannel.isBlockUser()) {
                            String channelID = channel.getChatChannelID();

                            if (channelID != null) {

                                chatChannels.child(channelID).child("messages")

                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot messages : dataSnapshot.getChildren()) {
                                                        if (messages.exists()) {
                                                            CloudMessage cloudMessage = messages.getValue(CloudMessage.class);
                                                            Message absoluteMessage =
                                                                    new Message(messages.getKey(), chatID,
                                                                            cloudMessage.getMessageTYPE(),
                                                                            cloudMessage.getMessageOwnerID(),
                                                                            cloudMessage.getMessageTIME(), cloudMessage.getMessageCONTENT(),
                                                                            cloudMessage.getMessageURL(), myChatChannel.getChannelType(), cloudMessage.getMessageState());
                                                            if (!absoluteMessage.getMessageOwnerID().equals(myPhoneNumber)) {
                                                                absoluteMessage.setMessageTYPE(absoluteMessage.getMessageType() + MessageType.RECIEVED);
                                                            }
                                                            channelMessages.child(absoluteMessage.getMessageID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (!snapshot.exists()) {

                                                                                chatBadge.child(chatID).setValue(chatID);
                                                                                newMessages.child(messages.getKey()).setValue(messages.getKey());
                                                                                channelMessages.child(messages.getKey()).setValue(cloudMessage);
                                                                                // if will insert message to local db
                                                                                  viewModel.insertMessage(absoluteMessage);

                                                                                if (!myChatChannel.isMute()) {
                                                                                    //check for mute state or block state
                                                                                    if (!cloudMessage.equals(myPhoneNumber)) {
                                                                                        PushNotificationEntry entry = new PushNotificationEntry(myChatChannel.getChatID(), myChatChannel.getChannelType());
                                                                                        // register to push notification queue
                                                                                        userList.child(myPhoneNumber).child("chat_notification")
                                                                                                .child(myChatChannel.getChatID())
                                                                                                .setValue(entry);

                                                                                    }
                                                                                }
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

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void createGroupChatInvite(String myPhone, String userId,
                                             String groupChatID, String groupChatChannelID) {

        String content = groupChatID + "," + groupChatChannelID;
        sendMessage(content, userId, myPhone, MessageType.GROUP_CHAT_INVITE);


    }


    public static void sendMessage(String message, String receiver, String sender, int type) {
        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("users").child(sender).child("chats");
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Channels channel = dataSnapshot.getValue(Channels.class);

                    if (channel.getChatChannelID() != null) {
                        DatabaseReference messages = dataSnapshot.getRef().child("messages");
                        DatabaseReference chatChannel = FirebaseDatabase.getInstance().getReference("ChatChannels").child(channel.getChatChannelID()).child("messages");
                        String messageID = chatChannel.push().getKey();
                        CloudMessage cloudMessage = new CloudMessage(type, sender, System.currentTimeMillis(), message, "", MessageState.SENT);
                        messages.child(messageID).setValue(cloudMessage);
                        chatChannel.child(messageID).setValue(cloudMessage);

                    }
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

    public static Bitmap getCircleBitmap(Bitmap bitmap) {



       /* final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();


        */
        return bitmap;
    }
}
