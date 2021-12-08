package com.kariaki.choice.UI.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kariaki.choice.R;
import com.kariaki.choice.UI.util.PostLifeSpans;
import com.kariaki.choice.UI.util.Theme;

public class NotificationSettings extends AppCompatActivity {

    TextView pushNotificationPost, messagingPushNotification, notificationVibrate, postDashBoardBadgeCounter,
            chatBadgeCounter, inAppSound, postSettingsText;
    AppCompatCheckBox inAppSoundCheckBox, chatCheckBox,
            darshBoardCheckBox, vibrateCheckBox, messagingCheckBox, postCheckBox;
    RelativeLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        viewById();
        loadTheme();
        loadSettings();
    }

    private SharedPreferences sharedPreferences;

    public void loadSettings() {
        sharedPreferences = getSharedPreferences("notification", MODE_PRIVATE);

        boolean sound = sharedPreferences.getBoolean("in app sound", false);
        inAppSoundCheckBox.setChecked(sound);
        boolean vibrate = sharedPreferences.getBoolean("vibrate", true);
        vibrateCheckBox.setChecked(vibrate);
        boolean postPush = sharedPreferences.getBoolean("post push notification", true);
        postCheckBox.setChecked(postPush);

      //  boolean dashboard=sharedPreferences.getBoolean("show only contact post",true);
        //darshBoardCheckBox.setChecked(dashboard);
        boolean badgeCount=sharedPreferences.getBoolean("badge counter",true);
        messagingCheckBox.setChecked(badgeCount);

    }

    private void applySettings(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("in app sound",inAppSoundCheckBox.isChecked());
        editor.putBoolean("vibrate",vibrateCheckBox.isChecked());
        editor.putBoolean("post push notification",postCheckBox.isChecked());
        editor.putBoolean("badge counter",messagingCheckBox.isChecked());
    }
    public void viewById() {
        pushNotificationPost = findViewById(R.id.pushNotificationPost);
        messagingPushNotification = findViewById(R.id.messagingPushNotification);
        notificationVibrate = findViewById(R.id.notificationVibrate);
        messagingCheckBox = findViewById(R.id.messagingCheckBox);
        postSettingsText = findViewById(R.id.postSettingsText);
        vibrateCheckBox = findViewById(R.id.vibrateCheckBox);
        darshBoardCheckBox = findViewById(R.id.darshBoardCheckBox);
        chatCheckBox = findViewById(R.id.chatCheckBox);
        postCheckBox = findViewById(R.id.postCheckBox);
        rootView = findViewById(R.id.rootView);
        inAppSoundCheckBox = findViewById(R.id.inAppSoundCheckBox);
        postDashBoardBadgeCounter = findViewById(R.id.postDashBoardBadgeCounter);
        chatBadgeCounter = findViewById(R.id.chatBadgeCounter);
        inAppSound = findViewById(R.id.inAppSound);

    }

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                rootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                rootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {pushNotificationPost, messagingPushNotification, notificationVibrate, postDashBoardBadgeCounter,
                chatBadgeCounter, inAppSound, postSettingsText};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }

    public void backPress(View view) {

        onBackPressed();
    }

    public void postPushNotification(View view) {

        boolean isChecked = postCheckBox.isChecked();
        if (isChecked) {
            postCheckBox.setChecked(false);
        } else {
            postCheckBox.setChecked(true);
        }

    }

    public void messagingPushNotification(View view) {
        boolean isChecked = messagingCheckBox.isChecked();
        if (isChecked) {
            messagingCheckBox.setChecked(false);
        } else {
            messagingCheckBox.setChecked(true);
        }

    }

    public void vibrate(View view) {

        boolean isChecked = vibrateCheckBox.isChecked();
        if (isChecked) {
            vibrateCheckBox.setChecked(false);
        } else {
            vibrateCheckBox.setChecked(true);


        }
    }

    public void postDashboard(View view) {

        boolean isChecked = messagingCheckBox.isChecked();
        if (isChecked) {
            messagingCheckBox.setChecked(false);
        } else {
            messagingCheckBox.setChecked(true);
        }


    }

    public void chatBadgeCounter(View view) {

        boolean isChecked = messagingCheckBox.isChecked();
        if (isChecked) {
            messagingCheckBox.setChecked(false);
        } else {
            messagingCheckBox.setChecked(true);


        }
    }

    public void inappSound(View view) {

            boolean isChecked = inAppSoundCheckBox.isChecked();
            if (isChecked) {
                inAppSoundCheckBox.setChecked(false);
            } else {
                inAppSoundCheckBox.setChecked(true);
            }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applySettings();
    }
}
