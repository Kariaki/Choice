package com.kariaki.choice.UI.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageViewer extends AppCompatActivity {

    SubsamplingScaleImageView imagePreview;

    RelativeLayout topAppbar;
    RelativeLayout downAppbar;
    RelativeLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        imagePreview = findViewById(R.id.imagePreview);
        topAppbar = findViewById(R.id.topAppbar);
        rootView = findViewById(R.id.rootView);
        downAppbar = findViewById(R.id.downAppbar);
        viewByID();
        Intent image_to_view = getIntent();
        String url = image_to_view.getStringExtra("url");
        String fileExtension=image_to_view.getStringExtra("message content");
        int messageState = image_to_view.getIntExtra("state", 1);

        Glide.with(this).load(url).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                BitmapDrawable bitmapDrawable=(BitmapDrawable)resource;
                Bitmap bitmap=bitmapDrawable.getBitmap();
                imagePreview.setImage(ImageSource.bitmap(bitmap));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        View decorView = getWindow().getDecorView();

        setData();
        decorView.setBackgroundColor(getResources().getColor(R.color.dialogColor));
        rootView.setOnClickListener(v -> {
            handleView();
        });

    }

    TextView senderName, messageTime, chatIDName;
    CircleImageView profileImage;

    private void viewByID() {
        senderName = findViewById(R.id.senderName);
        messageTime = findViewById(R.id.messageTime);
        profileImage = findViewById(R.id.profileImage);
        chatIDName = findViewById(R.id.chatIDName);
    }

    private void setData() {
        Intent intent = getIntent();
        String ownerId = intent.getStringExtra("owner");
        long messagetime = intent.getLongExtra("time", System.currentTimeMillis());
        String time = TimeFactor.getDetailedDate(messagetime, "dd/MM/yyyy");
        String secondTime = TimeFactor.getDetailedDate(messagetime, "HH:mm");
        time = time + " " + secondTime;
        messageTime.setText(time);
        if (ownerId.equals(new UserInformation(this).getMainUserID())) {
            senderName.setText("Me");
        } else {
            UserNaming.getUserName(ownerId, senderName);
        }

        String chatID = intent.getStringExtra("chat id");
        int chatType = intent.getIntExtra("chat type", ChannelTypes.ONE_TO_ONE_CHAT);
        if (chatType == ChannelTypes.ONE_TO_ONE_CHAT) {
            UserNaming.getUserName(chatID, chatIDName);
            UserNaming.getInstance().loadProfilePicture(chatID, profileImage, this);

        } else if (chatType == ChannelTypes.GROUP_CHAT) {
            UserNaming.getInstance().nameThisGroupChat(chatID,this,chatIDName,profileImage);
        }


    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uioption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uioption);
    }

    private void handleView() {
        if (downAppbar.getVisibility() == View.GONE) {
            downAppbar.setVisibility(View.VISIBLE);
            topAppbar.setVisibility(View.VISIBLE);


            showStatusBar();
        } else {
            downAppbar.setVisibility(View.GONE);
            topAppbar.setVisibility(View.GONE);
            hideStatusBar();

        }
    }

    private void showStatusBar() {

        View decorView = getWindow().getDecorView();
        decorView.setBackgroundColor(getResources().getColor(R.color.dialogColor));
        int uioption = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uioption);
    }

    public void backClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
