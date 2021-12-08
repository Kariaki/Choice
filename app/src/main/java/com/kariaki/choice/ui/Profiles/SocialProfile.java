package com.kariaki.choice.ui.Profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Chat.ChannelTypes;
import com.kariaki.choice.ui.Chat.ChatPage;
import com.kariaki.choice.ui.DialogBox.ChoiceDialogBox;
import com.kariaki.choice.ui.Post.Adapter.PostAdapter;
import com.kariaki.choice.ui.Post.PostInfo;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.Post.Viewer.PostImageViewer;
import com.kariaki.choice.ui.Post.Viewer.PostVideoViewer;
import com.kariaki.choice.ui.Settings.Entities.PrivacySettings;
import com.kariaki.choice.ui.Settings.Settings;
import com.kariaki.choice.ui.util.ConnectionState;
import com.kariaki.choice.ui.util.FastBlur;
import com.kariaki.choice.ui.util.LastCheck;
import com.kariaki.choice.ui.DialogBox.OptionBottomSheet;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.kariaki.choice.ui.util.UserNaming;
import com.r0adkll.slidr.Slidr;
import com.vanniktech.emoji.EmojiTextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialProfile extends AppCompatActivity {

    Toolbar socialProfileToolbar;
    RecyclerView activityList;
    NestedScrollView curveHolder;
    RelativeLayout curveHolder2;
    PostAdapter adapter;

    LinearLayoutManager manager;

    Intent intent;
    UserDetail detail;
    EmojiTextView partialProfileAboutUser;
    TextView partialProfileUserName;
    CircleImageView partialProfilePicture;
    List<Post> postList = new ArrayList<>();
    LinearLayout noAcitivty;
    TextView partialProfileUserOnLineStatus;

    ProgressBar postLoader;
    RelativeLayout toolbar;


    ImageView infoImage;
    TextView infoText;
    DatabaseReference connected = FirebaseDatabase.getInstance().getReference(".info/connected");

    DatabaseReference thisUserChat = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference thisUserContact;
    DatabaseReference myContactInThisUser;
    TextView username;
    Button sendMessage;
    ImageView imageBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_profile);
        viewByID();

        adapter = new PostAdapter(this);
        Slidr.attach(this);


        intent = getIntent();
        imageBlur = findViewById(R.id.imageBlur);
        detail = intent.getParcelableExtra("user");
        thisUsersPosts = FirebaseDatabase.getInstance().getReference("users")
                .child(detail.getPhone()).child("post");
        viewModel = ChoiceViewModel.getInstance(getApplication());
        manager = new LinearLayoutManager(this);
        information = UserInformation.getInstance(getApplicationContext());
        activityList.setLayoutManager(manager);
        adapter.setPosts(postList);
        adapterOnclick();
        loadTheme();
        activityList.setHasFixedSize(true);

        activityList.setAdapter(adapter);


        thisUserChat = thisUserChat.child(information.getMainUserID()).child("chats").child(detail.getPhone());
        thisUserContact = FirebaseDatabase.getInstance().getReference("users")
                .child(information.getMainUserID()).child("people")
                .child(detail.getPhone());
        myContactInThisUser = FirebaseDatabase.getInstance().getReference("users")
                .child(detail.getPhone()).child("people")
                .child(information.getMainUserID());


        String name = "@ " + detail.getUsername();
        username.setText(name);
        partialProfileAboutUser.setText(detail.getAbout());
        String profileUrl = detail.getProfileURL();
        Glide.with(this.getApplicationContext()).load(profileUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        bitmap = FastBlur.fastblur(bitmap, 0.7f, 25);
                        imageBlur.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        postCounter(detail.getPhone(), postsCount);


    }

    ChoiceViewModel viewModel;
    TextView postsCount, repostCount, repeatsCount;

    private void viewByID() {
        activityList = findViewById(R.id.activityList);
        curveHolder = findViewById(R.id.curveHolder);
        infoText = findViewById(R.id.infoText);
        toolbar = findViewById(R.id.toolbar);
        infoImage = findViewById(R.id.infoImage);
        postsCount = findViewById(R.id.postsCount);
        postLoader = findViewById(R.id.postLoader);
        sendMessage = findViewById(R.id.sendMessage);
        username = findViewById(R.id.username);
        partialProfileUserOnLineStatus = findViewById(R.id.partialProfileUserOnLineStatus);
        noAcitivty = findViewById(R.id.noAcitivty);
        partialProfilePicture = findViewById(R.id.partialProfilePicture);

        partialProfileAboutUser = findViewById(R.id.partialProfileAboutUser);
        partialProfileUserName = findViewById(R.id.partialProfileUserName);
        curveHolder2 = findViewById(R.id.curveHolder2);
        repeatsCount = findViewById(R.id.repeatsCount);
        repostCount = findViewById(R.id.repostCount);
    }

    boolean isContact = false;

    @Override
    protected void onStart() {
        super.onStart();
        Glide.with(SocialProfile.this).load(detail.getProfileURL()).into(partialProfilePicture);

        DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference("users")
                .child(new UserInformation(SocialProfile.this).getPhoneNumber());
        allUsers.child("people")
                .child(detail.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ChoiceUser choiceUser = snapshot.getValue(ChoiceUser.class);

                            isContact = true;
                            partialProfileUserName.setText(choiceUser.getUserContactName());
                            //  partialProfileAboutUser.setText(choiceUser.getUserAboutMe());
                            privacyContact();
                        } else {

                            isContact = false;
                            partialProfileUserName.setText(detail.getUsername());
                            // partialProfileAboutUser.setText(detail.getAbout());

                            privacy();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        connected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean connect = dataSnapshot.getValue(Boolean.class);
                    if (connect) {

                        fetchPost(isContact);

                    } else {
                        YoYo.with(Techniques.Wave).duration(1000).repeat(YoYo.INFINITE).playOn(infoImage);
                        postLoader.setVisibility(View.GONE);
                        String message = "failed to load";
                        infoText.setText(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadTheme();

    }


    public void openViewProfilePage(View view) {

        Intent intent = new Intent(this, ViewProfilePicture.class);

        intent.putExtra("profileImage", detail.getProfileURL());
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, partialProfilePicture,
                ViewCompat.getTransitionName(partialProfilePicture));
        startActivity(intent, optionsCompat.toBundle());

    }

    private void privacy() {
        choiceUser.child(detail.getPhone()).child("Privacy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            PrivacySettings privacySettings = dataSnapshot.getValue(PrivacySettings.class);
                            if (!privacySettings.isAllowDmFromNoneContacts()) {
                                sendMessage.setEnabled(false);
                            }
                            if (privacySettings.getShowLastSeenTo() == Settings.EVERYONE) {
                                showLastSeen();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void privacyContact() {
        choiceUser.child(detail.getPhone()).child("Privacy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            PrivacySettings privacySettings = dataSnapshot.getValue(PrivacySettings.class);
                            if (privacySettings.getShowLastSeenTo() != Settings.NOBODY) {
                                showLastSeen();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backPress(View view) {

        onBackPressed();
    }

    List<String> ID = new ArrayList<>();
    DatabaseReference thisUsersPosts;

    public void fetchPost(boolean isContact) {
        thisUsersPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int count = 0;
                    for (DataSnapshot posts : dataSnapshot.getChildren()) {
                        if (!ID.contains(posts.getKey())) {
                            getParticularPost(posts.getKey(), isContact);
                            count++;
                            ID.add(posts.getKey());
                        }
                    }
                    if (count == dataSnapshot.getChildrenCount()) {
                        postLoader.setVisibility(View.INVISIBLE);
                    } else {
                        postLoader.setVisibility(View.VISIBLE);
                    }
                } else {
                    postLoader.setVisibility(View.INVISIBLE);
                }

                if (dataSnapshot.getChildrenCount() == 0) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");

    public void getParticularPost(String id, boolean isContact) {
        postFolder.child(id)
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
                                            Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(),
                                                    cloudPost.getPOST_CAPTION(), cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME()
                                                    , info.getPostIsOnRepeat(), detail.getPhone(), info.isAllowPrivateComment());

                                            if (isContact) {
                                                postList.add(0, post);

                                                adapter.notifyDataSetChanged();
                                            } else {
                                                if (post.isRepeat()) {

                                                    postList.add(0, post);

                                                    adapter.notifyDataSetChanged();

                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(SocialProfile.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    DatabaseReference choiceUser = FirebaseDatabase.getInstance().getReference("users");


    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                //changeTextColors(text,R.color.textContext);

                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));

                curveHolder2.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));

                //curveHolder2.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

                adapter.setColor(R.color.textColor);
                color = R.color.textColor;
                changeTextColors(text, R.color.textColor);

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                color = R.color.whiteGreen;
                curveHolder2.setBackground(getResources().getDrawable(R.drawable.bottom_curve_dark_mode));

                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_curve_second));
                adapter.setColor(R.color.whiteGreen);
                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {partialProfileAboutUser, partialProfileUserName
                , username, postsCount, repostCount, repeatsCount};
        setTextColors(textViews, currentTheme);
        adapter.notifyDataSetChanged();

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


    private void showLastSeen() {
        choiceUser.child(detail.getPhone()).child("Connection")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ConnectionState connectionState = dataSnapshot.getValue(ConnectionState.class);
                            if (connectionState.isOnline()) {
                                partialProfileUserOnLineStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                                partialProfileUserOnLineStatus.setText("online");
                            } else {
                                String text = "Last online " + TimeFactor.getDetailedDate(connectionState.getLastSeen(), System.currentTimeMillis());

                                partialProfileUserOnLineStatus.setText(text);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void postCounter(String userID, TextView view) {
        postFolder.
                orderByChild("ownerID")
                .equalTo(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String post = snapshot.getChildrenCount() + " post";
                            view.setText(post);
                        } else {
                            String post = 0 + " post";
                            view.setText(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void repostAndRepeatsCounter(String userID, Text repost, TextView repeats) {

    }

    private void adapterOnclick() {

        adapter.setOnclickListener(new PostAdapter.OnclickListeners() {

            @Override
            public void openSocialProfile(int position) {

            }

            @Override
            public void postHelper(int position, LinearLayout iconsHolder, TextView text, ImageView icon) {
                Post post = postList.get(position);
                if (post.isRepeat()) {
                    iconsHolder.setVisibility(View.VISIBLE);
                    icon.setImageResource(R.drawable.repeat);
                    viewModel.getChoiceUser(post.getOwnerID())
                            .observe(SocialProfile.this, new Observer<List<ChoiceUser>>() {
                                @Override
                                public void onChanged(List<ChoiceUser> choiceUsers) {
                                    if (!choiceUsers.isEmpty()) {
                                        text.setText("post on repeat");
                                    } else {
                                        viewModel
                                                .getChoiceUser(post.getFromUserID())
                                                .observe(SocialProfile.this, new Observer<List<ChoiceUser>>() {
                                                    @Override
                                                    public void onChanged(List<ChoiceUser> choiceUsers) {
                                                        if (!choiceUsers.isEmpty()) {

                                                            String message =
                                                                    "repeat from" + "\t" + choiceUsers.get(0).getUserContactName();
                                                            text.setText(message);
                                                        } else {
                                                            text.setText("not your contact");
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                } else {
                    viewModel.getChoiceUser(post.getOwnerID())
                            .observe(SocialProfile.this, new Observer<List<ChoiceUser>>() {
                                @Override
                                public void onChanged(List<ChoiceUser> choiceUsers) {
                                    if (!choiceUsers.isEmpty()) {
                                        iconsHolder.setVisibility(View.INVISIBLE);
                                    } else {
                                        iconsHolder.setVisibility(View.VISIBLE);
                                        icon.setImageResource(R.drawable.reply_to_left);
                                        viewModel.getChoiceUser(post.getFromUserID())
                                                .observe(SocialProfile.this, new Observer<List<ChoiceUser>>() {
                                                    @Override
                                                    public void onChanged(List<ChoiceUser> choiceUsers) {
                                                        if (!choiceUsers.isEmpty()) {
                                                            String message = choiceUsers.get(0).getUserContactName() + "\t" + "reposted";
                                                            text.setText(message);
                                                        }
                                                    }
                                                });


                                    }
                                }
                            });
                }
            }

            @Override
            public void updateComments(int position, TextView commentText) {

            }

            @Override
            public void updateLikes(int position, TextView likeText, int type) {

            }

            @Override
            public void nameChoiceUser(int position, TextView nameView, CircleImageView profileImageView) {
                UserNaming naming = UserNaming.getInstance();
                Post post = postList.get(position);
                naming.nameThisUser(SocialProfile.this, SocialProfile.this, post.getOwnerID(), nameView, profileImageView);

            }

            @Override
            public void onClickShare(int position) {
                Post post = postList.get(position);
                DatabaseReference folder = postFolder.child(post.getPostID());
                folder.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            OptionBottomSheet bottomSheet = OptionBottomSheet.getInstance();

                            bottomSheet.setShare(true);
                            bottomSheet.setPost(post);
                            bottomSheet.setListener(new OptionBottomSheet.OptionClickListener() {
                                @Override
                                public void commentPrivately() {

                                }

                                @Override
                                public void onCopyText() {

                                }

                                @Override
                                public void onCancelRepeat() {

                                }

                                @Override
                                public void onHidePost() {

                                }

                                @Override
                                public void onRepost() {
                                    repost(post);
                                }

                                @Override
                                public void onMutePost() {

                                }
                            });

                            bottomSheet.show(getSupportFragmentManager(), "Dialog Bottom Sheet");
                        } else {
                            Toast.makeText(SocialProfile.this, "This post no longer exist", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onClickLoop(int position) {
                Post post = postList.get(position);

                post.setRepeat(false);
                postList.set(position, post);
                //choiceViewModel.updatePost(post);

            }


            @Override
            public void onClickItem(int position, View v) {
                Post post = (Post) postList.get(position);


                switch (post.getPostType()) {
                    case PostTypes.MERGED_POST:
                    case PostTypes.SINGLE_POST:
                        Intent intent1 = new Intent(SocialProfile.this, PostImageViewer.class);
                        intent1.putExtra("view images", post);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SocialProfile.this, v, "images");
                        startActivity(intent1);
                        break;
                    case PostTypes.VIDEO_POST:
                        intent1 = new Intent(SocialProfile.this, PostVideoViewer.class);
                        intent1.putExtra("view images", post);
                        startActivity(intent1);
                        break;

                }


            }


            @Override
            public void onClickOption(int position) {

              /*  Post post = (Post) postList.get(position);
                DatabaseReference folder = postFolder.child(post.getPostID());

                OptionBottomSheet bottomSheet = OptionBottomSheet.getInstance();

                bottomSheet.setData(false);
                bottomSheet.setListener(position12 -> {
                    switch (position12) {
                        case 0:
                            bottomSheet.dismiss();
                            break;
                        case 2:
                            Post post1 = postList.get(position);
                            post1.setPOST_TYPE(PostTypes.DEFAULT);
                            postList.remove(position);
                            adapter.notifyItemRemoved(position);
                            // choiceViewModel.updatePost(post1);

                            bottomSheet.dismiss();
                            break;
                        case 1:
                            repost(post);
                            break;


                    }
                });
                bottomSheet.show(getSupportFragmentManager(), "Dialog Bottom Sheet");


               */

            }

            @Override
            public void onClickComment(int position) {

               /* Intent intent = new Intent(SocialProfile.this, CommentPage.class);
                Post post = postList.get(position);
                intent.putExtra("postData", post);
                startActivity(intent);

                */

            }

            @Override
            public void onCheckChangeMergeLikeOne(int position, boolean isChecked) {
                Post post = (Post) postList.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_one");
                DatabaseReference postInfo = reference.child("postData");

                if (isChecked) {
                    DatabaseReference mylike = likeList.child(information.getMainUserID());
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ndataSnapshot) {
                                    if (!ndataSnapshot.exists()) {

                                        updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                                        mylike.setValue(information.getMainUserID());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (post.isRepeat()) {
                        saveRepeat((Post) postList.get(position), reference);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();


                }


            }

            @Override
            public void onCheckChangeMergeLikeTwo(int position, boolean isChecked) {

                Post post = (Post) postList.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_two");
                DatabaseReference postInfo = reference.child("postData");

                if (isChecked) {

                    DatabaseReference mylike = likeList.child(information.getMainUserID());
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {

                                        mylike.setValue(information.getMainUserID());
                                        updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (post.isRepeat()) {
                        saveRepeat((Post) postList.get(position), reference);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();


                }

            }


            @Override
            public int onCheckChangeSingles(int position, boolean isChecked) {
                Post post = (Post) postList.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("likes");
                DatabaseReference postInfo = reference.child("postData");

                if (isChecked) {
                    if (post.isRepeat()) {
                        saveRepeat(post, reference);

                    }
                    DatabaseReference mylike = likeList.child(information.getMainUserID());

                    updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                    mylike.setValue(information.getMainUserID());


                } else {
                    DatabaseReference myLike = likeList.child(information.getMainUserID());
                    myLike.removeValue();

                }


                return 0;
            }

        });

    }

    private FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();


    String urlfile = "";

    private void updateLifeSpan(DatabaseReference likeList, DatabaseReference postInfo, int minutes_to_add
    ) {
        postInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostInfo info = dataSnapshot.getValue(PostInfo.class);

                likeList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        long newLifeSpan = TimeFactor.updateLifeSpan(info.getPostLifeSpan(), minutes_to_add, (int) dataSnapshot1.getChildrenCount());
                        Map<String, Object> lifeSpanUpdate = new HashMap<>();
                        lifeSpanUpdate.put("postLifeSpan", newLifeSpan);
                        postInfo.updateChildren(lifeSpanUpdate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void repost(Post cloudPost) {

        if (cloudPost.isRepeat()) {
            DatabaseReference myrepeatPost = choiceUser.child(information.getMainUserID())
                    .child("post").child(cloudPost.getPostID());
            myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

        }


    }

    UserInformation information;

    public void saveRepeat(Post cloudPost, DatabaseReference reference) {

        if (!cloudPost.getOwnerID().equals(information.getMainUserID())) {

            if (cloudPost.isRepeat()) {

                DatabaseReference myrepeatPost = choiceUser.child(information.getMainUserID())
                        .child("post").child(cloudPost.getPostID());
                myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

            }

        }
    }


    ChoiceDialogBox dialogBox;


    public void sendMessage(View view) {
        Intent intent = new Intent(this, ChatPage.class);

        intent.putExtra("phone number", detail.getPhone());
        boolean isFromChat = getIntent().getBooleanExtra("from chat", false);

        intent.putExtra("channelType", ChannelTypes.ONE_TO_ONE_CHAT);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        String nameText = partialProfileUserName.getText().toString();
        intent.putExtra("name", nameText);

        if (isFromChat) {
            onBackPressed();
        } else {

            startActivity(intent);


        }
    }


    int color = R.color.textContext;


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
