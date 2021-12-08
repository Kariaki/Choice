package com.kariaki.choice.ui.makepost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.MainActivity;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.Settings.PostSettings;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.Theme;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinishPost extends AppCompatActivity {

    private Bitmap bitmap;
    private String imagePath;

    private EmojiEditText postCaption;

    private ImageButton finishPostEmoji, finishPostKeyboard;
    private int postType;
    private Intent postIntent;
    private List<String> imagePathList = new ArrayList<>();
    private View splitter;
    private RelativeLayout finishPostRootView;
    private ToggleButton finishPostRepeat;
    private Button finishpost;
    private Intent cloudPostIntent;
    private ViewPager finishPostViewPager;
    AppCompatCheckBox checkMerge;
    private TextView mergeText, repeat;
    private ProgressDialog finishpostProgress;
    private ImageView finishPostVideoView, FinishPlayPauseButton;
    private SinglePostViewPagerAdapter adapter;
    private RelativeLayout finishPostMergeHolder;
    private RelativeLayout finishPostVideoViewHolder;
    ImageView finishPostSettings;
    SharedPreferences sharedPreferences;

    String videoURL;
    private int lifeSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_finish_post);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        finishpost = findViewById(R.id.finishpost);

        sharedPreferences = getSharedPreferences("post", MODE_PRIVATE);

        postCaption = findViewById(R.id.finishPostCaption);
        finishPostEmoji = findViewById(R.id.finishPostEmoji);
        finishPostRootView = findViewById(R.id.finishPostRootView);
        finishPostVideoViewHolder = findViewById(R.id.finishPostVideoViewHolder);
        FinishPlayPauseButton = findViewById(R.id.FinishPlayPauseButton);
        finishPostVideoView = findViewById(R.id.finishPostVideoView);
        finishPostSettings = findViewById(R.id.finishPostSettings);
        repeat = findViewById(R.id.repeat);

        finishPostRepeat = findViewById(R.id.finishPostRepeat);
        checkMerge = findViewById(R.id.checkMerge);
        finishPostViewPager = findViewById(R.id.finishPostViewPager);
        mergeText = findViewById(R.id.mergeText);
        finishpostProgress = new ProgressDialog(this);
        finishPostKeyboard = findViewById(R.id.finishPostKeyboard);


        loadTheme();


        ImageView one = findViewById(R.id.finishPostImage1);
        ImageView two = findViewById(R.id.finishPostImage2);

        splitter = findViewById(R.id.splitter);
        adapter = new SinglePostViewPagerAdapter(this);
        adapter.setOnClickListener(v -> {

        });
        finishPostMergeHolder = findViewById(R.id.finishPostMergeHolder);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(finishPostRootView).build(postCaption);

        finishPostRepeat.setChecked(sharedPreferences.getBoolean("always repeat", false));
        lifeSpan = sharedPreferences.getInt("post duration", PostLifeSpans.MAX);


        finishPostEmoji.setVisibility(View.VISIBLE);
        finishPostKeyboard.setVisibility(View.INVISIBLE);

        finishPostKeyboard.setOnClickListener(v -> {
            finishPostEmoji.setVisibility(View.VISIBLE);
            finishPostKeyboard.setVisibility(View.INVISIBLE);
            emojiPopup.toggle();

        });

        finishPostEmoji.setOnClickListener(v -> {
            finishPostEmoji.setVisibility(View.INVISIBLE);
            finishPostKeyboard.setVisibility(View.VISIBLE);
            emojiPopup.toggle();

        });

        postCaption.setOnClickListener(v -> {
            if (emojiPopup.isShowing()) {
                emojiPopup.toggle();
            }
        });
        finishPostVideoViewHolder.setVisibility(View.GONE);


        postIntent = getIntent();

        cloudPostIntent = getIntent();

        postType = cloudPostIntent.getIntExtra("postType", PostTypes.SINGLE_POST);

        if (postType != PostTypes.VIDEO_POST) {

            String[] manyImages = postIntent.getStringArrayExtra("pathToImages");
            imagePathList = Arrays.asList(manyImages);

            adapter.setImageURI(manyImages);
            finishPostViewPager.setAdapter(adapter);

            // using the checkbox to change the post type from single to merge

            checkMerge.setVisibility(View.VISIBLE);

            if (checkMerge.isChecked()) {

                postType = PostTypes.MERGED_POST;


            } else {

                postType = PostTypes.SINGLE_POST;
            }


            if (imagePathList.size() == 2) {

                checkMerge.setVisibility(View.VISIBLE);
                mergeText.setVisibility(View.VISIBLE);

            } else {

                checkMerge.setVisibility(View.GONE);
                mergeText.setVisibility(View.GONE);

            }


            // Glide.with(this).load(imagePath).into(firebaseImage);
            finishPostViewPager.setAdapter(adapter);

            checkMerge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        finishPostViewPager.setVisibility(View.GONE);
                        finishPostMergeHolder.setVisibility(View.VISIBLE);
                        Glide.with(FinishPost.this).load(imagePathList.get(0)).into(one);
                        Glide.with(FinishPost.this).load(imagePathList.get(1)).into(two);
                        postType = PostTypes.MERGED_POST;
                        finishPostRepeat.setChecked(false);

                    } else {

                        postType = PostTypes.SINGLE_POST;
                        finishPostViewPager.setVisibility(View.VISIBLE);
                        finishPostMergeHolder.setVisibility(View.GONE);

                    }
                }
            });
        } else {

            String[] manyImages = new String[]{postIntent.getStringExtra("videoURL")};

            imagePathList = Arrays.asList(manyImages);


            finishPostViewPager.setVisibility(View.GONE);
            finishPostMergeHolder.setVisibility(View.GONE);

            checkMerge.setVisibility(View.GONE);
            mergeText.setVisibility(View.GONE);

            finishPostVideoViewHolder.setVisibility(View.VISIBLE);


            videoURL = cloudPostIntent.getStringExtra("videoURL");

            Glide.with(this).load(videoURL).placeholder(R.drawable.square_place_holder).into(finishPostVideoView);


        }

    }


    public void sendPost(View view) {


        Intent sendPost = new Intent(this, MainActivity.class);

        String caption = postCaption.getText().toString();
        String[] nArray = new String[imagePathList.size()];

        String[] array = imagePathList.toArray(nArray);

        sendPost.putExtra("images", array);
        sendPost.putExtra("postCaption", caption);
        sendPost.putExtra("postType", postType);
        sendPost.putExtra("post duration", lifeSpan);
        sendPost.putExtra("allow private comment", sharedPreferences.getBoolean("allow private comment", true));
        sendPost.putExtra("repeat", finishPostRepeat.isChecked());
        sendPost.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        startActivity(sendPost);


    }

    private void creatNewPost(Post post) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("test");
        dbRef.push().setValue(post);
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void cloudPostFinish() {
        cloudPostIntent.getParcelableExtra("repost now");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void openPostSettings(View view) {
        startActivity(new Intent(this, PostSettings.class));
    }


    public void setTextColors(int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:

                postCaption.setTextColor(getResources().getColor(R.color.textContext));
                finishPostRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                repeat.setTextColor(getResources().getColor(R.color.textContext));
                mergeText.setTextColor(getResources().getColor(R.color.textContext));

                break;
            case Theme.DEEP:
                postCaption.setTextColor(getResources().getColor(R.color.whiteGreen));
                finishPostRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                repeat.setTextColor(getResources().getColor(R.color.whiteGreen));
                mergeText.setTextColor(getResources().getColor(R.color.whiteGreen));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);


        setTextColors(currentTheme);

    }
}
