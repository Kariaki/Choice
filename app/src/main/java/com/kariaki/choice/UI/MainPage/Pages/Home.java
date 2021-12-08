package com.kariaki.choice.UI.MainPage.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.CloudMessage;
import com.kariaki.choice.Model.Database.Entities.Contact;
import com.kariaki.choice.Model.Database.Entities.ContactPost;
import com.kariaki.choice.Model.Database.MessageState;
import com.kariaki.choice.Model.MessageType;
import com.kariaki.choice.Model.Post;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChannelTypes;
import com.kariaki.choice.UI.Chat.Channels;
import com.kariaki.choice.UI.Comment.CommentOption;
import com.kariaki.choice.UI.Comment.CommentPage;
import com.kariaki.choice.UI.Comment.CommentPrivately;
import com.kariaki.choice.UI.Comment.PrivateCommentHelper;
import com.kariaki.choice.UI.Post.Adapter.PostAdapter;
import com.kariaki.choice.UI.Post.PostInfo;
import com.kariaki.choice.UI.Post.PostTypes;
import com.kariaki.choice.UI.Post.Viewer.PostImageViewer;
import com.kariaki.choice.UI.Post.Viewer.PostVideoViewer;
import com.kariaki.choice.UI.Profiles.EditFromBottom;
import com.kariaki.choice.UI.Profiles.PartialProfile;
import com.kariaki.choice.UI.Profiles.SocialProfile;
import com.kariaki.choice.UI.util.Functions;
import com.kariaki.choice.UI.util.LastCheck;
import com.kariaki.choice.UI.DialogBox.OptionBottomSheet;
import com.kariaki.choice.UI.util.PostLifeSpans;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Home extends Fragment {


    public Home() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;
    private List<Post> POST = new ArrayList<>();


    PostAdapter adapter;
    LinearLayout layout;
    private String dataURL = "";

    private String folder = "post";

    private static final String LIST_OF_USERS = "users";
    DatabaseReference liveUsers = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);

    String mainUserPhoneNumber;
    int currentlikes;
    UserInformation information;
    boolean willRepeat = true;
    DatabaseReference myTimeLine;
    ChoiceViewModel choiceViewModel;
    LinearLayoutManager manager;
    RelativeLayout newPost;
    TextView newPostText;
    List<String> postID = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);


        choiceViewModel = ChoiceViewModel.getInstance(getActivity().getApplication());
        information = new UserInformation(getActivity());
        mainUserPhoneNumber = information.getMainUserID();
        newPost = view.findViewById(R.id.newPost);
        newPostText = view.findViewById(R.id.newPostText);
        myTimeLine = liveUsers.child(mainUserPhoneNumber).child("timeLine");
        postFolder = FirebaseDatabase.getInstance().getReference("post");
        //  myTimeLine.keepSynced(true);

        adapter = new PostAdapter(getActivity());
        // adapter.setPosts(POST);

        loadTheme();
        adapterOnclick();

        //adapter.setPosts(POST);


        choiceViewModel.getAllPost().observe(getActivity(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {

                adapter.setPosts(posts);
                POST = posts;
            }
        });


        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        realtimeData();

        return view;
    }

    private FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();


    private DatabaseReference postFolder;
    String urlfile = "";

    List<Post> postList = new ArrayList<>();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firebaseFirestore.collection("test");

    public void realtimeData() {
/*
        DatabaseReference reference = firebaseDb.getReference("test");
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    POST.add(0,post);
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/*
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Post post=snapshot.toObject(Post.class);
                        POST.add(0,post);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });    */

    }

    @Override
    public void onStart() {
        super.onStart();

        loadTheme();

        /*
        postFolder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Post> removedPost = choiceViewModel.getPost(dataSnapshot.getKey());

                        if (removedPost != null && !removedPost.isEmpty()) {
                            choiceViewModel.deletePost(removedPost.get(0));
                        }
                    }
                }).start();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkPost();


         */
    }

    private void checkPost() {
        for (Post post : POST) {
            postFolder.child(post.getPostID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.exists()) {
                                choiceViewModel.deletePost(post);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });
        }
    }

    Context context;

    private void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);



    }

    private void adapterOnclick() {

        context = getContext();
        adapter.setOnclickListener(new PostAdapter.OnclickListeners() {

            @Override
            public void openSocialProfile(int position) {
                Post post = POST.get(position);
                String id = post.getOwnerID();
                liveUsers.child(id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                    Intent intent = new Intent(getActivity(), SocialProfile.class);
                                    intent.putExtra("user", detail);
                                    startActivity(intent);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void postHelper(int position, LinearLayout iconsHolder, TextView text, ImageView icon) {
                Post post = POST.get(position);

                DatabaseReference myContact = liveUsers.child(mainUserPhoneNumber).child("people");
                if (post.isRepeat()) {
                    //case where the post was repeated
                    iconsHolder.setVisibility(View.VISIBLE);
                    //  icon.setVisibility(View.VISIBLE);
                    // icon.setImageResource(R.drawable.repeat);
                    if (post.getOwnerID().equals(post.getFromUserID())) {
                        text.setText("post on repeat");

                    } else {
                        //checking if the from user, if your contact and naming them in either cases
                        myContact.child(post.getFromUserID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            liveUsers.child(post.getFromUserID())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                                                String message = "repeat from " + detail.getUsername();
                                                                text.setText(message);

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                        } else {
                                            ChoiceUser choiceUser = snapshot.getValue(ChoiceUser.class);
                                            String message = "repeat from " + choiceUser.getUserContactName();
                                            text.setText(message);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                } else {// case where the post was reposted from one of your contact
                    myContact.child(post.getOwnerID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        iconsHolder.setVisibility(View.INVISIBLE);

                                    } else {

                                        iconsHolder.setVisibility(View.VISIBLE);
                                        icon.setImageResource(R.drawable.reply_to_left);
                                        myContact.child(post.getFromUserID())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            ChoiceUser fromContact = snapshot.getValue(ChoiceUser.class);
                                                            String message = fromContact.getUserContactName() + "\t" + "reposted";
                                                            text.setText(message);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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
                Post post = POST.get(position);

                naming.nameThisUser(post.getOwnerID(), getContext(), nameView, profileImageView);
                //naming.nameThisUser(getActivity(), getContext().getApplicationContext(), post.getOwnerID(), nameView, profileImageView);

            }

            @Override
            public void onClickShare(int position) {
                Post post = POST.get(position);
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
                                    bottomSheet.dismiss();
                                    Toast.makeText(getContext(), "Reposted", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onMutePost() {

                                }
                            });


                            bottomSheet.show(getFragmentManager(), String.valueOf(System.currentTimeMillis()));
                        } else {
                            Toast.makeText(getActivity(), "This post no longer exist", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onClickLoop(int position) {
                Post post = POST.get(position);

                post.setRepeat(false);
                choiceViewModel.updatePost(post);

            }


            @Override
            public void onClickItem(int position, View v) {

                Post post = POST.get(position);

                switch (post.getPostType()) {
                    case PostTypes.MERGED_POST:
                    case PostTypes.SINGLE_POST:
                        Intent intent1 = new Intent(getContext(), PostImageViewer.class);
                        intent1.putExtra("view images", post);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "images");
                        startActivity(intent1, optionsCompat.toBundle());
                        break;
                    case PostTypes.VIDEO_POST:
                        intent1 = new Intent(getContext(), PostVideoViewer.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent1.putExtra("view images", post);
                        startActivity(intent1);
                        break;

                }


            }


            @Override
            public void onClickOption(int position) {

                Post post = POST.get(position);
                DatabaseReference folder = postFolder.child(post.getPostID());

                OptionBottomSheet bottomSheet = OptionBottomSheet.getInstance();
                bottomSheet.setPost(post);
                privateMessageinstances(post);

                boolean isMuted;

                bottomSheet.setShare(false);
                bottomSheet.setListener(new OptionBottomSheet.OptionClickListener() {
                    @Override
                    public void commentPrivately() {
                        PrivateCommentHelper editFromBottom = new PrivateCommentHelper();

                        int privateCommentType = 0;
                        switch (post.getPostType()) {
                            case PostTypes.TEXT:
                                privateCommentType = MessageType.PRIVATE_COMMENT_TEXT;
                                break;
                            case PostTypes.SINGLE_POST:
                            case PostTypes.VIDEO_POST:

                                privateCommentType = MessageType.PRIVATE_COMMENT_IMAGE_SINGLE;

                                break;


                        }

                        int finalPrivateCommentType = privateCommentType;
                        bottomSheet.dismiss();
                        editFromBottom.setClickListeners(new EditFromBottom.onClickListeners() {
                            @Override
                            public void onClickConfirm(String text) {
                                sendPrivateComment(text, post);
                                editFromBottom.dismiss();

                            }

                            @Override
                            public void onClickCancel() {
                                editFromBottom.dismiss();
                            }
                        });
                        editFromBottom.show(getFragmentManager(), String.valueOf(System.currentTimeMillis()));
                    }

                    @Override
                    public void onCopyText() {
                        switch (post.getPostType()) {
                            case PostTypes.TEXT:
                                String text = post.getPOST_CAPTION();
                                setClipboard(context, text);
                                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();

                                break;
                            case PostTypes.VIDEO_POST:
                                text = post.getPOST_URL();
                                setClipboard(context, text);

                                Toast.makeText(context, "link copied", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(context, "no text to copy", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        bottomSheet.dismiss();

                    }

                    @Override
                    public void onCancelRepeat() {
                        Post post = POST.get(position);

                        post.setRepeat(false);
                        choiceViewModel.updatePost(post);
                        bottomSheet.dismiss();

                    }

                    @Override
                    public void onHidePost() {
                        Post post1 = POST.get(position);
                        post1.setPOST_TYPE(PostTypes.DEFAULT);
                        POST.remove(position);
                        adapter.notifyItemRemoved(position);
                        choiceViewModel.updatePost(post1);

                        bottomSheet.dismiss();
                    }

                    @Override
                    public void onRepost() {

                        repost(post);
                        bottomSheet.dismiss();
                        Toast.makeText(getContext(), "Post will be visible to your contacts", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onMutePost() {

                        mutePostFromUser(post.getFromUserID());
                        bottomSheet.dismiss();

                    }
                });
                bottomSheet.show(getFragmentManager(), String.valueOf(System.currentTimeMillis()));


            }

            @Override
            public void onClickComment(int position) {

                Intent intent = new Intent(getActivity(), CommentPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Post post = POST.get(position);
                intent.putExtra("postData", post);

                startActivity(intent);


            }

            @Override
            public void onCheckChangeMergeLikeOne(int position, boolean isChecked) {
                Post post = POST.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_one");
                DatabaseReference postInfo = reference.child("postData");


                if (isChecked) {
                    DatabaseReference mylike = likeList.child(mainUserPhoneNumber);
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ndataSnapshot) {
                                    if (!ndataSnapshot.exists()) {

                                        updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                                        mylike.setValue(mainUserPhoneNumber);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (willRepeat) {
                        saveRepeat((Post) POST.get(position), reference);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(mainUserPhoneNumber);
                    myLike.removeValue();


                }


            }

            @Override
            public void onCheckChangeMergeLikeTwo(int position, boolean isChecked) {

                Post post = POST.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("like_two");
                DatabaseReference postInfo = reference.child("postData");

                if (isChecked) {

                    DatabaseReference mylike = likeList.child(mainUserPhoneNumber);
                    mylike.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {

                                        mylike.setValue(mainUserPhoneNumber);
                                        updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                    if (willRepeat) {
                        saveRepeat((Post) POST.get(position), reference);
                    }
                } else {
                    DatabaseReference myLike = likeList.child(mainUserPhoneNumber);
                    myLike.removeValue();


                }

            }


            @Override
            public int onCheckChangeSingles(int position, boolean isChecked) {
                Post post = POST.get(position);
                DatabaseReference reference = postFolder.child(post.getPostID());
                DatabaseReference likeList = reference.child("likes");
                DatabaseReference postInfo = reference.child("postData");

                if (isChecked) {
                    if (willRepeat) {
                        saveRepeat(post, reference);

                    }
                    DatabaseReference mylike = likeList.child(mainUserPhoneNumber);

                    updateLifeSpan(likeList, postInfo, PostLifeSpans.LIKES);
                    mylike.setValue(mainUserPhoneNumber);


                } else {
                    DatabaseReference myLike = likeList.child(mainUserPhoneNumber);
                    myLike.removeValue();

                }


                return currentlikes;
            }

        });

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void repost(Post cloudPost) {

        if (cloudPost.isRepeat()) {
            DatabaseReference myrepeatPost = liveUsers.child(information.getMainUserID())
                    .child("post").child(cloudPost.getPostID());
            myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

        }


    }

    DatabaseReference muteUsersPost = firebaseDb.getReference("users");

    private void mutePostFromUser(String userID) {

        muteUsersPost = muteUsersPost.child(information.getMainUserID()).child("people").child(userID);
        muteUsersPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ContactPost contactPost = dataSnapshot.getValue(ContactPost.class);

                    Map<String, Object> update = new HashMap<>();
                    if (contactPost.isMute()) {

                        update.put("mute", false);

                    } else {
                        update.put("mute", true);
                        choiceViewModel.getChoiceUser(userID)
                                .observe(getActivity(), new Observer<List<ChoiceUser>>() {
                                    @Override
                                    public void onChanged(List<ChoiceUser> choiceUsers) {
                                        if (!choiceUsers.isEmpty()) {
                                            String text = "You muted " + choiceUsers.get(0).getUserContactName();
                                            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    muteUsersPost.updateChildren(update);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void saveRepeat(Post cloudPost, DatabaseReference reference) {

        if (!cloudPost.getOwnerID().equals(information.getMainUserID())) {

            if (cloudPost.isRepeat()) {

                DatabaseReference myrepeatPost = liveUsers.child(information.getMainUserID())
                        .child("post").child(cloudPost.getPostID());
                myrepeatPost.setValue(new LastCheck(System.currentTimeMillis(), 0));

            }

        }
    }

    Handler handler;


    public void setTextColors(int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                adapter.setColor(R.color.textHeaderColor);
                newPost.setBackground(getResources().getDrawable(R.drawable.full_curve));
                newPostText.setTextColor(getResources().getColor(R.color.textContext));

                break;
            case Theme.DEEP:
                adapter.setColor(R.color.whiteGreen);

                newPostText.setTextColor(getResources().getColor(R.color.whiteGreen));
                newPost.setBackground(getResources().getDrawable(R.drawable.full_curve_dark));

                break;

        }
    }
    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getItemCount() != 0) {
            int position = manager.findFirstVisibleItemPosition();
            outState.putInt("scroll position", position);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        int scroll_position = 0;//savedInstanceState.getInt("scroll position",0);

    }


    DatabaseReference userToChatWith;
    DatabaseReference chatChannels;
    DatabaseReference myChats;
    DatabaseReference chatFolder;
    DatabaseReference otherChatFolder;
    DatabaseReference reference;
    StorageReference chatFiles;

    public void privateMessageinstances(Post post) {

        userToChatWith = liveUsers.child(post.getOwnerID());
        chatChannels = FirebaseDatabase.getInstance().getReference("ChatChannels");
        myChats = liveUsers.child(new UserInformation(getContext()).getMainUserID());
        chatFolder = myChats.child("chats");
        otherChatFolder = userToChatWith.child("chats");
        reference = chatFolder.child(post.getOwnerID());
        chatFiles = FirebaseStorage.getInstance().getReference("ChatFiles");

    }

    public void createPrivateComment(CloudMessage message, Post cloudPost) {

        information = new UserInformation(getContext());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    DatabaseReference ourChat = chatChannels.push();
                    DatabaseReference sendMessage = ourChat.child("messages");
                    String channelID = ourChat.getKey();
                    DatabaseReference myChatWithYou = chatFolder.child(cloudPost.getOwnerID());
                    myChatWithYou.setValue(new Channels(channelID, message.getMessageTIME(), ChannelTypes.ONE_TO_ONE_CHAT, false, false));

                    otherChatFolder.child(new UserInformation(getContext()).getMainUserID())
                            .setValue(new Channels(channelID, message.getMessageTIME(), ChannelTypes.ONE_TO_ONE_CHAT, false, false));

                    DatabaseReference messageRef = sendMessage.push();

                    myChatWithYou.child("messages")
                            .setValue(message);
                    messageRef.setValue(message).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getContext(), "Sending comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                } else {
                    createMessage(message);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int lastSeenMessageCount;


    public void createMessage(CloudMessage message) {


        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Channels value = dataSnapshot.getValue(Channels.class);
                String ID = value.getChatChannelID();
                DatabaseReference sendMessageRef = chatChannels.child(ID)
                        .child("messages");

                DatabaseReference messageRef = sendMessageRef.push();

                messageRef.setValue(message).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Sending comment", Toast.LENGTH_SHORT).show();

                                reference.child(messageRef.getKey()).setValue(message);

                            }
                        }
                );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

    public void sendPrivateComment(String text, Post cloudpost) {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connect = dataSnapshot.getValue(Boolean.class);
                if (connect) {

                    privateCommentSender(text, cloudpost);

                } else {

                    Toast.makeText(context, "Fail to send comment, check your connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    DatabaseReference postFOLDER = FirebaseDatabase.getInstance().getReference("post");

    private void privateCommentSender(String text, Post cloudPost) {

        long messageTime = System.currentTimeMillis();
        postFOLDER.child(cloudPost.getPostID()).child("postData")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        PostInfo info = dataSnapshot.getValue(PostInfo.class);

                        if (info.isAllowPrivateComment()) {
                            if (!text.isEmpty() && cloudPost.getPostType() != PostTypes.MERGED_POST
                            ) {

                                if (!information.getMainUserID().equals(cloudPost.getOwnerID())) {
                                    String makeText = text + messageTime + cloudPost.getPOST_CAPTION() + messageTime + cloudPost.getPostID() + messageTime + cloudPost.getOwnerID() + messageTime;
                                    int privateCommentType = 0;
                                    CloudMessage comment;
                                    switch (cloudPost.getPOST_TYPE()) {
                                        case PostTypes.TEXT:
                                            privateCommentType = MessageType.PRIVATE_COMMENT_TEXT;

                                            break;
                                        case PostTypes.SINGLE_POST:
                                        case PostTypes.VIDEO_POST:

                                            privateCommentType = MessageType.PRIVATE_COMMENT_IMAGE_SINGLE;

                                            break;

                                    }


                                    String urls[] = cloudPost.getPOST_URL().split(",");
                                    comment = new CloudMessage(privateCommentType, information.getMainUserID()
                                            , messageTime,
                                            makeText, urls[0], MessageState.SENT);

                                    createPrivateComment(comment, cloudPost);


                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

}
