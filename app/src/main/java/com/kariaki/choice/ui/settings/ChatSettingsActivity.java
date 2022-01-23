package com.kariaki.choice.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Theme;

public class ChatSettingsActivity extends AppCompatActivity {


    Toolbar chatSettingsToolbar;
    RelativeLayout chatSettingsRootView,chatSettingsShowMessageReceipt,chatSettingsChatActivity;
    TextView chatSettingsText;
    AppCompatCheckBox chatSettingsMessageReceiptCheckBox,chatActivityCheckBox;
    SharedPreferences chatSettings;
    SharedPreferences.Editor chatSettingsEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        chatSettingsToolbar=findViewById(R.id.chatSettingsToolbar);
        chatActivityCheckBox=findViewById(R.id.chatActivityCheckBox);
        chatSettingsText=findViewById(R.id.chatSettingsText);
        chatSettings=getSharedPreferences("chat setting",MODE_PRIVATE);
        chatSettingsEdit=chatSettings.edit();
        chatSettingsChatActivity=findViewById(R.id.chatSettingsChatActivity);
        chatSettingsMessageReceiptCheckBox=findViewById(R.id.chatSettingsMessageReceiptCheckBox);

        chatSettingsShowMessageReceipt=findViewById(R.id.chatSettingsChatActivity);

        chatSettingsRootView=findViewById(R.id.chatSettingsRootView);
        setSupportActionBar(chatSettingsToolbar);
        loadSettings();

        loadTheme();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backPress(View view) {
        onBackPressed();
    }


    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);

                chatSettingsRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                chatSettingsRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView []textViews={chatSettingsText};
        setTextColors(textViews,currentTheme);

    }
    private void loadSettings(){
        SharedPreferences sharedPreferences=getSharedPreferences("chatSettings",MODE_PRIVATE);
        boolean recieptCheck=sharedPreferences.getBoolean("message receipt",true);
        boolean activityCheck=sharedPreferences.getBoolean("chat activity",true);
        chatSettingsMessageReceiptCheckBox.setChecked(recieptCheck);
        chatActivityCheckBox.setChecked(activityCheck);


    }
    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }

    public void onClickMessageReceipt(View view) {
        boolean isChecked = chatSettingsMessageReceiptCheckBox.isChecked();
        if (isChecked) {
            chatSettingsMessageReceiptCheckBox.setChecked(false);

        } else {
            chatSettingsMessageReceiptCheckBox.setChecked(true);
        }
        chatSettingsEdit.putBoolean("message receipt",isChecked);
        chatSettingsEdit.apply();
    }

    public void chatActivity(View view) {
        boolean isChecked = chatActivityCheckBox.isChecked();
        if (isChecked) {
            chatActivityCheckBox.setChecked(false);

        } else {
            chatActivityCheckBox.setChecked(true);
        }
        chatSettingsEdit.putBoolean("chat activity",isChecked);
        chatSettingsEdit.apply();
    }
}
