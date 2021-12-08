package com.kariaki.choice.Notification;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Database.Entities.ChoiceUser;
import com.kariaki.choice.model.Database.Entities.CloudMessage;
import com.kariaki.choice.model.Database.Entities.NotificationForPost;
import com.kariaki.choice.model.MessageType;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.Chat.ChatPage;
import com.kariaki.choice.UI.Comment.CommentPage;
import com.kariaki.choice.UI.GroupChat.GroupChatInformation;
import com.kariaki.choice.UI.MainPage.util.MainFunctions;
import com.kariaki.choice.UI.Post.PostInfo;
import com.kariaki.choice.UI.util.Time.TimeFactor;

import static com.kariaki.choice.Choice.chatChannel;
import static com.kariaki.choice.Choice.postChannel;

public class PushNotificationService extends IntentService {

    public PushNotificationService() {

        super("notification service");

    }

    PowerManager.WakeLock wakeLock;
    NotificationManagerCompat managerCompat;

    SharedPreferences preferences;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        myPhoneNumber = intent.getStringExtra("phone");

        preferences = getSharedPreferences("notification", MODE_PRIVATE);

        boolean enable_post = preferences.getBoolean("post push notification", true);
        if (enable_post) {
            fetchPostForNotification();
        }

        getPushNotificationList();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager manager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake:lock");
        wakeLock.acquire();
        managerCompat = NotificationManagerCompat.from(this);
        setIntentRedelivery(true);

    }

    DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");


    private void fetchPostForNotification() {
        userList.child(myPhoneNumber)
                .child("pendingNotification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int id = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                NotificationForPost notificationForPost = snapshot.getValue(NotificationForPost.class);

                                int finalId = id;
                                postFolder.child(notificationForPost.getPostID())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);
                                                    DatabaseReference postRef = dataSnapshot.getRef();

                                                    createPostFromCloudPost(finalId, postRef, cloudPost, notificationForPost);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                id++;
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });


    }

    private void createPostFromCloudPost(int id, DatabaseReference postRef, CloudPost cloudPost, NotificationForPost notification) {
        //collecting post data to send as intent to comment page
        postRef.child("postData")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            PostInfo info = dataSnapshot.getValue(PostInfo.class);
                            Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(),
                                    cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME(),
                                    info.getPostIsOnRepeat(), "", info.isAllowPrivateComment());
                            Intent intent = new Intent(PushNotificationService.this, CommentPage.class);
                            intent.putExtra("postData", post);
                            pendingNotification(id, intent, notification);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void pendingNotification(int id, Intent intent, NotificationForPost notification) {

        nameCreateNotification(id, notification, intent);

    }

    String myPhoneNumber;
    DatabaseReference userList = FirebaseDatabase.getInstance().getReference("users");

    private void nameCreateNotification(int id, NotificationForPost notification, Intent intent) {
        userList.child(myPhoneNumber).child(notification.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                            String name = contact.getUserContactName();
                            Glide.with(getApplicationContext()).load(contact.getUserImageUrl())
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            String content = name + " " + notification.getBody();

                                            BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                            sendPostNotification(id, content, bitmapDrawable, intent);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });


                        } else {
                            userList.child(notification.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                            Glide.with(getApplicationContext()).load(detail.getProfileURL())
                                                    .into(new CustomTarget<Drawable>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                            String content = detail.getUsername() + " " + notification.getBody();
                                                            BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                                            sendPostNotification(id, content, bitmapDrawable, intent);
                                                        }

                                                        @Override
                                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                                        }
                                                    });
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


    private void sendPostNotification(int id, String textContent, BitmapDrawable bitmap, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = getPostNotification(textContent, "Comments", bitmap, pendingIntent);
        managerCompat.notify(id, builder.build());


    }

    private void getPushNotificationList() {
        userList.child(myPhoneNumber).child("chat_notification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int id = 0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot registered_Notification : dataSnapshot.getChildren()) {
                                PushNotificationEntry chat_notifiable = registered_Notification.getValue(PushNotificationEntry.class);
                                checkChatForMissedMessages(id, chat_notifiable.getChatID(), chat_notifiable.getChatType());
                                registered_Notification.getRef().removeValue();
                                id++;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkChatForMissedMessages(int notification_id, String id, int chatType) {
        userList.child(myPhoneNumber).child("chats").child(id).child("messages")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot cloudMessage : dataSnapshot.getChildren()) {
                                CloudMessage missedMessage = cloudMessage.getValue(CloudMessage.class);
                                switch (chatType) {
                                    case ChannelTypes
                                            .ONE_TO_ONE_CHAT:
                                        nameUser(notification_id, missedMessage.getMessageOwnerID(), missedMessage);
                                        break;
                                    case ChannelTypes.GROUP_CHAT:
                                        nameGroupChat(notification_id, id, missedMessage);

                                        break;

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void nameUser(int notification_id, String id, CloudMessage message) {
        DatabaseReference myContact = userList.child(myPhoneNumber).child("people");
        myContact.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                    String contactName = contact.getUserContactName();
                    Glide.with(getApplicationContext()).load(contact.getUserImageUrl())
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    String tittle = contactName;

                                    String content = processMessageToReply(message);
                                    //call message processor here
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                    Intent intent = getChatIntent(PushNotificationService.this, id, ChannelTypes.ONE_TO_ONE_CHAT,
                                            contact.getUserContactName(), contact.getUserImageUrl());
                                    PendingIntent pendingIntent = PendingIntent.getActivity(PushNotificationService.this
                                            , 0, intent, PendingIntent.FLAG_ONE_SHOT);

                                    NotificationCompat.Builder notify = getChatNotification(content, tittle, bitmapDrawable, pendingIntent);
                                    managerCompat.notify(1, notify.build());

                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                } else {
                    userList.child(id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                        Glide.with(getApplicationContext()).load(detail.getProfileURL())
                                                .into(new CustomTarget<Drawable>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                        String tittle = detail.getUsername();

                                                        String content = processMessageToReply(message);
                                                        //call message processor here
                                                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                                        Intent intent = getChatIntent(PushNotificationService.this, id, ChannelTypes.ONE_TO_ONE_CHAT
                                                                , detail.getDisplayName(), detail.getProfileURL());
                                                        PendingIntent pendingIntent = PendingIntent.getActivity(PushNotificationService.this
                                                                , 0, intent, PendingIntent.FLAG_ONE_SHOT);

                                                        NotificationCompat.Builder notify = getChatNotification(content, tittle, bitmapDrawable, pendingIntent);
                                                        managerCompat.notify(notification_id, notify.build());

                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                                    }
                                                });
                                    } else {

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

    private void nameGroupChat(int id, String chatID, CloudMessage message) {
        DatabaseReference grouChatList = FirebaseDatabase.getInstance().getReference("GroupChat");
        grouChatList.child(chatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupChatInformation information = dataSnapshot.getValue(GroupChatInformation.class);
                        Glide.with(getApplicationContext()).load(information.getImageUrl())
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                        String tittle = information.getGroupChatName();
                                        String context = processMessageToReply(message);
                                        //call function processor here

                                        processedNotification(message, id, chatID, message.getMessageOwnerID(),
                                                tittle, context, bitmapDrawable, ChannelTypes.ONE_TO_ONE_CHAT);

                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {


                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private Intent getChatIntent(Context object, String chatID, int type, String name, String url) {
        Intent intent = new Intent(object, ChatPage.class);

        intent.putExtra("phone number", chatID);
        intent.putExtra("name", name);
        intent.putExtra("channelType", type);
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;

    }

    private void processedNotification(CloudMessage message, int notificationID, String chatID, String ownerID,
                                       String tittle, String context, BitmapDrawable bitmapDrawable, int chatType) {

        DatabaseReference myContact = userList.child(myPhoneNumber).child("people");
        myContact.child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                    String contactName = contact.getUserContactName();


                    String content = contactName + " : " + processMessageToReply(message);
                    //call message processor here
                    Intent intent = getChatIntent(PushNotificationService.this, chatID, chatType,
                            contactName, contact.getUserImageUrl());
                    PendingIntent pendingIntent = PendingIntent.getActivity(PushNotificationService.this
                            , 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder notify = getChatNotification(content, tittle, bitmapDrawable, pendingIntent);
                    managerCompat.notify(notificationID, notify.build());


                } else {
                    userList.child(ownerID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserDetail detail = dataSnapshot.getValue(UserDetail.class);

                                        String content = detail.getUsername() + " : " + processMessageToReply(message);
                                        //call message processor here
                                        Intent intent = getChatIntent(PushNotificationService.this, chatID,
                                                chatType, detail.getDisplayName(), detail.getProfileURL());
                                        PendingIntent pendingIntent = PendingIntent.getActivity(PushNotificationService.this
                                                , 0, intent, PendingIntent.FLAG_ONE_SHOT);

                                        NotificationCompat.Builder notify = getChatNotification(content, tittle, bitmapDrawable, pendingIntent);
                                        managerCompat.notify(1, notify.build());

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

    private void runNotification(NotificationCompat.Builder notify) {
        // NotificationCompat.Builder builder = getChatNotification(missedMessage.getMessageOwnerID(), "Choice", bitmapDrawable, null);
        managerCompat.notify(1, notify.build());


    }

    public NotificationCompat.Builder getPostNotification(String contentText, String contentTittle,
                                                          BitmapDrawable bitmapDrawable,
                                                          PendingIntent pendingIntent) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play
        return new NotificationCompat.Builder(getApplicationContext(), postChannel)
                .setContentText(contentText)
                .setContentTitle(contentTittle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(uri)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.mipmap.choice_icon_foreground)
                .setAutoCancel(true)
                .setLargeIcon(bitmapDrawable.getBitmap())
                .setContentIntent(pendingIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }


    public NotificationCompat.Builder getChatNotification(String contentText, String contentTittle, BitmapDrawable bitmapDrawable
            , PendingIntent intent) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play

        return new NotificationCompat.Builder(getApplicationContext(), chatChannel)
                .setContentText(contentText)
                .setContentTitle(contentTittle)
                .setSound(uri)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setContentIntent(intent)

                //
                // .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))

                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(MainFunctions.getCircleBitmap(bitmapDrawable.getBitmap()))
                .setSmallIcon(R.mipmap.choice_icon_foreground);

    }

    private String processMessageToReply(CloudMessage message) {

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

                return messageContent;


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

                return messageContent;
            case MessageType.IMAGE:
            case MessageType.IMAGE + MessageType.RECIEVED:
                messageContent = "sent a photo";
                return messageContent;
            case MessageType.VIDEO:
            case MessageType.IMAGE + MessageType.VIDEO:
                messageContent = "sent a video";

                return messageContent;
            default:
                return "sent a message";

        }

    }


}
