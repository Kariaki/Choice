package com.kariaki.choice.ui.post.viewer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.post.adapter.ViewPagerAdapter;
import com.kariaki.choice.ui.post.PostInfo;
import com.kariaki.choice.ui.util.LastCheck;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.time.TimeFactor;

import java.util.HashMap;
import java.util.Map;

public class PostImageViewer extends AppCompatActivity {


    private ViewPager postImageViewerViewPager;
    private ViewPagerAdapter adapter;

    DatabaseReference databaseReference;

    RelativeLayout postImageViewerRootLayout;
    UserInformation information;
    String[] images;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_image_viewer);

        postImageViewerViewPager = findViewById(R.id.postViewerViewPager);

        postImageViewerRootLayout = findViewById(R.id.postImageViewerRootLayout);
        adapter = new ViewPagerAdapter(this);
        information = new UserInformation(this);

        Intent intent = getIntent();
        int holder = intent.getIntExtra("state", 0);
        String url = intent.getStringExtra("url");


        if (holder == 0) {


            post = intent.getParcelableExtra("view images");

            images = post.getPOST_URL().split(",");
            databaseReference = FirebaseDatabase.getInstance().getReference("post")
                    .child(post.getPostID()).child("likes");
            DatabaseReference viewList = FirebaseDatabase.getInstance().getReference("post").child(post.getPostID()).child("views");
            DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post").child(post.getPostID());
            String userID = new UserInformation(this).getMainUserID();

            viewList.child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                updateLifeSpan(viewList, postFolder.child("postData"), PostLifeSpans.VIEWS);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            viewList.child(userID).setValue(userID);


            if (post.isRepeat()) {
                saveRepeat(post);
            }


        } else {
            images = url.split(",");
        }


        adapter.setImageURI(images);

        postImageViewerViewPager.setAdapter(adapter);


    }

    public void onBackPressed() {
        finish();

    }

    private void updateLifeSpan(DatabaseReference likeList, DatabaseReference postInfo, int minutes_to_add
    ) {
        postInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PostInfo info = dataSnapshot.getValue(PostInfo.class);

                    likeList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                            if (dataSnapshot1.exists()) {
                                long newLifeSpan = TimeFactor.updateLifeSpan(info.getPostLifeSpan(), minutes_to_add, (int) dataSnapshot1.getChildrenCount());
                                Map<String, Object> lifeSpanUpdate = new HashMap<>();
                                lifeSpanUpdate.put("postLifeSpan", newLifeSpan);
                                postInfo.updateChildren(lifeSpanUpdate);
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

    DatabaseReference liveUsers = FirebaseDatabase.getInstance().getReference("users");

    public void saveRepeat(Post cloudPost) {

        if (!cloudPost.getOwnerID().equals(information.getMainUserID())) {

            if (cloudPost.isRepeat()) {

                DatabaseReference myrepeatPost = liveUsers.child(information.getMainUserID())
                        .child("post").child(cloudPost.getPostID());
                myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

            }

        }
    }

    String outputSourceFile;
    String outputFile = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/choice";
    MediaRecorder recorder;
    long startTime;

    public static final double maxRecordingDuration = 1.5;
    boolean messageSending = false;

    DatabaseReference postFOLDER = FirebaseDatabase.getInstance().getReference("post");


}
