package com.kariaki.choice.ui.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kariaki.choice.R;
import com.kariaki.choice.ui.DialogBox.ChoiceDialogBoxMultiChoice;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostSettings extends AppCompatActivity {
    AppCompatCheckBox postSettingsPrivateCommentCheckBox, postSettingRepeatCheckBox,nonContactRepeatCheckBox;
    SharedPreferences.Editor editor;

    SpannableString spannableString;
    private RelativeLayout postSettingsRootView;
    private TextView postSettingsText;
    private List<String> messages=new ArrayList<>();
    private ChoiceDialogBoxMultiChoice multiChoice;
    private int color=R.color.textHeaderColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_settings);
        postSettingsRootView=findViewById(R.id.postSettingsRootView);
        postSettingsPrivateCommentCheckBox=findViewById(R.id.postSettingsPrivateCommentCheckBox);
        postSettingRepeatCheckBox=findViewById(R.id.postSettingRepeatCheckBox);
        postSettingsText=findViewById(R.id.postSettingsText);
        nonContactRepeatCheckBox=findViewById(R.id.nonContactRepeatCheckBox);
        loadSettings();
        editor = sharedPreferences.edit();
        String learn = "learn more";
        spannableString = new SpannableString(learn);
        loadTheme();
        multiChoice=new ChoiceDialogBoxMultiChoice();
        messages=  Arrays.asList(multiple);
        multiChoice.setText(messages);
        multiChoice.setColor(color);


        postSettingsPrivateCommentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("allow private comment", isChecked);
            }
        });
        postSettingRepeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor.putBoolean("always repeat", isChecked);

            }
        });

    }


    public void onClickPrivateComment(View view) {
        boolean isChecked = postSettingsPrivateCommentCheckBox.isChecked();
        if (isChecked) {
            postSettingsPrivateCommentCheckBox.setChecked(false);
        } else {
            postSettingsPrivateCommentCheckBox.setChecked(true);
        }

    }

    public void onClickRepeatAllPost(View view) {
        boolean isChecked = postSettingRepeatCheckBox.isChecked();
        if (isChecked) {
            postSettingRepeatCheckBox.setChecked(false);
        } else {
            postSettingRepeatCheckBox.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editSharedPreference();
    }

    private boolean alwaysRepeat;
    private boolean allowPrivateComment;

    private SharedPreferences sharedPreferences;

    public void loadSettings() {
        sharedPreferences = getSharedPreferences("post", MODE_PRIVATE);

        alwaysRepeat = sharedPreferences.getBoolean("always repeat", false);
        allowPrivateComment = sharedPreferences.getBoolean("allow private comment", true);
        minDuration = sharedPreferences.getInt("post duration", PostLifeSpans.MAX);
        showOnlyContactPost=sharedPreferences.getBoolean("show only contact post",false);
        nonContactRepeatCheckBox.setChecked(showOnlyContactPost);
        postSettingRepeatCheckBox.setChecked(alwaysRepeat);

        postSettingsPrivateCommentCheckBox.setChecked(allowPrivateComment);

    }
    boolean showOnlyContactPost=false;

    public void editSharedPreference() {

        editor.putBoolean("always repeat", postSettingRepeatCheckBox.isChecked());
        editor.putBoolean("allow private comment", postSettingsPrivateCommentCheckBox.isChecked());
        editor.putBoolean("show only contact post",nonContactRepeatCheckBox.isChecked());
        editor.putInt("post duration", minDuration);
        editor.apply();

    }

    String[] multiple = new String[]{"6 hours max", "12 hours max", "24 hours max"};

    int minDuration;

    public void showDialog(View view) {

        String titile = "Post duration";

        multiChoice.setSelected(revertLifeSpan(minDuration));
        multiChoice.setItemClickListener(new ChoiceDialogBoxMultiChoice.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                setLifeSpan(position);
            }

            @Override
            public void onPositiveButtonClick() {
                editSharedPreference();
                multiChoice.dismiss();
            }

            @Override
            public void onNegativeButtonClick() {
                multiChoice.dismiss();
            }
        });
        multiChoice.show(getSupportFragmentManager(),"options");
    }

    private void setLifeSpan(int position) {
        switch (position) {
            case 0:
                minDuration = PostLifeSpans.MIN;
                break;
            case 1:
                minDuration = PostLifeSpans.MID;
                break;
            case 2:
                minDuration = PostLifeSpans.MAX;
                break;
        }
    }

    private int revertLifeSpan(int time) {
        switch (time) {
            case PostLifeSpans.MIN:
                return 0;
            case PostLifeSpans.MID:
                return 1;
            case PostLifeSpans.MAX:
                return 2;
            default:
                return -1;
        }
    }

    public void backPress(View view) {

        onBackPressed();
    }

    public void onClickOnlyContactPost(View view){

        boolean isChecked = nonContactRepeatCheckBox.isChecked();
        if (isChecked) {
            nonContactRepeatCheckBox.setChecked(false);
        } else {
            nonContactRepeatCheckBox.setChecked(true);
        }

    }
    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);

                postSettingsRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                postSettingsRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView []textViews={postSettingsText};
        setTextColors(textViews,currentTheme);

    }
    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }
}
