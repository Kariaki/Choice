package com.kariaki.choice;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class Choice extends Application {

    public static final String postChannel = "ChannelForPost";
    public static final String postChannelName = "Notification for post";

    public static final String chatChannel = "ChannelForChat";
    public static final String chatChannelName = "Notification for chat messages";

    @Override
    public void onCreate() {
        super.onCreate();


         FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(1024*100000);
        EmojiManager.install(new IosEmojiProvider());


       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createPostChannel();
            createChatsChannel();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPostChannel() {
        SharedPreferences preferences=getSharedPreferences("notification",MODE_PRIVATE);
        boolean vibrate=preferences.getBoolean("vibrate",false);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify);//Here is FILE_NAME is the name of file that you want to play


        NotificationChannel channelPost = new NotificationChannel(postChannel, postChannelName, NotificationManager.IMPORTANCE_HIGH);

        channelPost.setShowBadge(true);
        channelPost.setSound(uri, attributes);
        channelPost.enableLights(true);
        channelPost.enableVibration(vibrate);
        channelPost.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channelPost);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChatsChannel() {
        SharedPreferences preferences=getSharedPreferences("notification",MODE_PRIVATE);
        boolean vibrate=preferences.getBoolean("vibrate",false);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);
        //Here is FILE_NAME is the name of file that you want to play


        NotificationChannel channelPost = new NotificationChannel(chatChannel, chatChannelName, NotificationManager.IMPORTANCE_DEFAULT);

        channelPost.setShowBadge(true);
        channelPost.setSound(uri, attributes);
        channelPost.enableLights(true);
        channelPost.enableVibration(vibrate);
        channelPost.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channelPost);

    }

    private NotificationManager manager;

    public NotificationManager getManager() {

        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }

}
