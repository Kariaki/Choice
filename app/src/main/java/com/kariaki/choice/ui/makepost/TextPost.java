package com.kariaki.choice.ui.makepost;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.MainActivity;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.PostInfo;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.Post.TextPostLastCheck;
import com.kariaki.choice.ui.Settings.PostSettings;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class TextPost extends AppCompatActivity {
    private EmojiEditText writeTextPost;

    ImageButton textPostBackButton,  keyboard, emoji;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String folder = "post";
    private DatabaseReference postFolder = database.getReference(folder);
    private RelativeLayout textPostRootView;
    private ToggleButton repeatTextPost;
    Button finishTextPostButton;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_text_post);
        viewById();
        editProperties();
        sharedPreferences=getSharedPreferences("post",MODE_PRIVATE);

        repeatTextPost.setChecked(sharedPreferences.getBoolean("always repeat",false));

        lifeSpan=sharedPreferences.getInt("post duration", PostLifeSpans.MAX);
        loadTheme();
    }

    private void editProperties(){
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(textPostRootView).build(writeTextPost);

        emoji.setOnClickListener(v->{
            if(!emojiPopup.isShowing()){
                emojiPopup.toggle();
                keyboard.setVisibility(View.VISIBLE);
                emoji.setVisibility(View.INVISIBLE);
            }
        });
        keyboard.setOnClickListener(v->{
            if(emojiPopup.isShowing()){
                emojiPopup.toggle();
                emoji.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.INVISIBLE);
            }
        });
        writeTextPost.setOnClickListener(v->{
            if(emojiPopup.isShowing()){
                emojiPopup.toggle();
                keyboard.setVisibility(View.INVISIBLE);
                emoji.setVisibility(View.VISIBLE);

            }
        });

        writeTextPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if(s.toString().isEmpty()){
                    finishTextPostButton.setEnabled(false);
                }else {
                    finishTextPostButton.setEnabled(true);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    finishTextPostButton.setEnabled(false);
                }else {
                    finishTextPostButton.setEnabled(true);
                }
                if (s.toString().length() > 200) {
                    Toast.makeText(TextPost.this, "Text limit has been reached", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().isEmpty()){
                    finishTextPostButton.setEnabled(false);
                }else {
                    finishTextPostButton.setEnabled(true);
                }

            }
        });
        if(writeTextPost.getText().toString().isEmpty()){
            finishTextPostButton.setEnabled(false);
        }else {
            finishTextPostButton.setEnabled(true);
        }

    }

    public void viewById() {
        writeTextPost = findViewById(R.id.writeTextPost);
        textPostBackButton = findViewById(R.id.textPostBackButton);
        keyboard=findViewById(R.id.textPostKeyboard);
        finishTextPostButton=findViewById(R.id.finishTextPostButton);
        emoji=findViewById(R.id.textPostEmoji);
        repeatTextPost = findViewById(R.id.repeatTextPost);
        textPostRootView = findViewById(R.id.textPostRootView);


    }

    DatabaseReference userDetail = FirebaseDatabase.getInstance().getReference("users");

    int lifeSpan;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void finishClick(View view) {

        String caption = writeTextPost.getText().toString();
        String likes = "0";
        long currentTime=System.currentTimeMillis();

        UserInformation userInformation = new UserInformation(this);

        long createLifeSpan= TimeFactor.createDefaultLifeSpan(currentTime,lifeSpan);

        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference(folder);
        DatabaseReference postToUpload = mdatabase.push();

        CloudPost post = new CloudPost(postToUpload.getKey(), userInformation.getMainUserID(), caption, "", PostTypes.TEXT,
                currentTime,createLifeSpan , "true");


        postToUpload.setValue(post);

        boolean isChecked = repeatTextPost.isChecked();
        DatabaseReference postData=postToUpload.child("postData");

        postData
                .setValue(new PostInfo(createLifeSpan, isChecked,
                        sharedPreferences
                                .getBoolean("allow private comment",true)))
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                TextPostLastCheck lastCheck = new TextPostLastCheck( currentTime, 0);

                DatabaseReference userPost=userDetail.child(userInformation.getMainUserID()).child("post")
                        .child(postToUpload.getKey());

                userPost.setValue(lastCheck);

            }
        });

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);


    }

    public void openPostSettings(View view){
        startActivity(new Intent(this, PostSettings.class));
    }

    public void textPostBackPress(View view) {

        onBackPressed();
    }

    public void setTextColors( int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:

                writeTextPost.setTextColor(getResources().getColor(R.color.textContext));
                textPostRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                writeTextPost.setTextColor(getResources().getColor(R.color.whiteGreen));
                textPostRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        setTextColors(currentTheme);

    }

}
