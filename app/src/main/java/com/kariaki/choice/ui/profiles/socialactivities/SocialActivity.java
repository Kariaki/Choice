package com.kariaki.choice.ui.Profiles.SocialActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.mainpage.pages.notifications.adapters.PageAdapter;
import com.kariaki.choice.ui.Post.Adapter.PostAdapter;
import com.kariaki.choice.ui.Post.PostInfo;
import com.kariaki.choice.ui.Profiles.SocialActivities.Pages.Posts;
import com.kariaki.choice.ui.Profiles.SocialActivities.Pages.Repeats;
import com.kariaki.choice.ui.Profiles.SocialActivities.Pages.Reposts;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialActivity extends AppCompatActivity {

    TabLayout notifactionTabLayout;
    ViewPager pager;
    PageAdapter adapter;
    Toolbar notifactionToolbar;

    List<Fragment> pages = new ArrayList<>();
    List<Post> postList = new ArrayList<>();
    List<Post> rePostList = new ArrayList<>();
    List<Post> repeatsList = new ArrayList<>();
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_social);
        notifactionTabLayout = findViewById(R.id.notifactionTabLayout);
        pager = findViewById(R.id.notificationViewPager);
        notifactionToolbar = findViewById(R.id.notifactionToolbar);

        assert getFragmentManager() != null;
        postAdapter = new PostAdapter(this);
        postAdapter.setOnclickListener(listeners);
        adapter = new PageAdapter(this);
        Posts posts = (new Posts());
        Repeats repeats = (new Repeats());
        Reposts reposts = (new Reposts());
        posts.setAdapter(postAdapter);
        repeats.setAdapter(postAdapter);
        reposts.setAdapter(postAdapter);
        pages.add(posts);
        pages.add(repeats);
        pages.add(reposts);


        adapter.setPages(pages);
        //pager.setAdapter(adapter);

        notifactionTabLayout.setupWithViewPager(pager);
        (notifactionTabLayout.getTabAt(0)).setText("Posts");
        (notifactionTabLayout.getTabAt(1)).setText("Repeats");
        (notifactionTabLayout.getTabAt(2)).setText("Re-Posts");


    }

    PostAdapter.OnclickListeners listeners = new PostAdapter.OnclickListeners() {

        @Override
        public void onClickOption(int position) {

        }

        @Override
        public void onClickComment(int position) {

        }

        @Override
        public void onCheckChangeMergeLikeOne(int position, boolean isChecked) {

        }

        @Override
        public void onCheckChangeMergeLikeTwo(int position, boolean isChecked) {

        }

        @Override
        public int onCheckChangeSingles(int position, boolean isChecked) {
            return 0;
        }

        @Override
        public void onClickShare(int position) {

        }

        @Override
        public void onClickLoop(int position) {

        }

        @Override
        public void onClickItem(int position, View sharedItem) {

        }

        @Override
        public void nameChoiceUser(int position, TextView nameView, CircleImageView profileImageView) {

        }

        @Override
        public void updateLikes(int position, TextView likeText, int type) {

        }

        @Override
        public void updateComments(int position, TextView commentText) {

        }

        @Override
        public void postHelper(int position, LinearLayout iconsHolder, TextView text, ImageView icon) {

        }

        @Override
        public void openSocialProfile(int position) {

        }
    };

    DatabaseReference myPostFolder;
    DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");

    private void setupListItem() {

        myPostFolder = FirebaseDatabase.getInstance().getReference(new UserInformation(this).getMainUserID()).child("post");
        myPostFolder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        postFolder.child(snapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);
                                            dataSnapshot.getRef().child("postData")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            PostInfo info = dataSnapshot.getValue(PostInfo.class);
                                                            Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(),
                                                                    cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME(), info.getPostIsOnRepeat(), "", info.isAllowPrivateComment());
                                                            if (new UserInformation(SocialActivity.this).getMainUserID().equals(post.getOwnerID()))
                                                            {
                                                                postList.add(post);
                                                            }else {
                                                                if(info.getPostIsOnRepeat()){
                                                                    repeatsList.add(post);
                                                                }else {
                                                                    rePostList.add(post);
                                                                }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
