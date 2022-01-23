package com.kariaki.choice.ui.profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kariaki.choice.R;

public class ViewProfilePicture extends AppCompatActivity {


    private SubsamplingScaleImageView profileImageViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_picture);

        profileImageViewer=findViewById(R.id.profileImageViewer);

        Intent result=getIntent();
        String url=result.getStringExtra("profileImage");
        Glide.with(this).load(url).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                BitmapDrawable drawable=(BitmapDrawable)resource;
                profileImageViewer.setImage(ImageSource.cachedBitmap(drawable.getBitmap()));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }


    public void backpress(View view){
        onBackPressed();
    }
}
