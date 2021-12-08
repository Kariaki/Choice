package com.kariaki.choice.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.kariaki.choice.R;


public class NotificationHelper extends ContextWrapper {
    public static final String postChannel = "ChannelForPost";
    public static final String postChannelName = "Notification for post";

    public static final String chatChannel = "ChannelForChats";
    public static final String chatChannelName = "Notification for chat messages";

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createPostChannel();
            createChatsChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPostChannel() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play


        NotificationChannel channelPost = new NotificationChannel(postChannel, postChannelName, NotificationManager.IMPORTANCE_HIGH);

        channelPost.setShowBadge(true);
       channelPost.setSound(uri, attributes);
        channelPost.enableLights(true);
        channelPost.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channelPost);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChatsChannel() {

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel channelPost = new NotificationChannel(chatChannel, chatChannelName, NotificationManager.IMPORTANCE_DEFAULT);

        channelPost.setShowBadge(true);
        channelPost.setSound(uri, attributes);
        channelPost.enableLights(true);
        channelPost.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channelPost);

    }

    private NotificationManager manager;

    public NotificationManager getManager() {

        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }

    public NotificationCompat.Builder getPostNotification(String contentText, String contentTittle,
                                                          BitmapDrawable bitmapDrawable,
                                                          PendingIntent pendingIntent) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play

        return new NotificationCompat.Builder(getApplicationContext(), postChannel)
                .setContentText(contentText)
                .setContentTitle(contentTittle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(uri)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.post_icon)
                .setLargeIcon(bitmapDrawable.getBitmap())
                .setContentIntent(pendingIntent);

    }

    public NotificationCompat.Builder getChatNotification(String contentText, String contentTittle) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pristine);//Here is FILE_NAME is the name of file that you want to play

        return new NotificationCompat.Builder(getApplicationContext(), chatChannel)
                .setContentText(contentText)
                .setContentTitle(contentTittle)
                .setSound(uri)
                .setSmallIcon(R.drawable.choice_icon_foreground);

    }

}
