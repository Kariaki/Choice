package com.kariaki.choice.UI.MainPage.Pages.Notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.NotificationForPost;
import com.kariaki.choice.Model.Post;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Comment.CommentPage;
import com.kariaki.choice.UI.MainPage.Pages.Notifications.Adapters.GeneralNotificationAdapter;
import com.kariaki.choice.UI.MainPage.util.MainFunctions;
import com.kariaki.choice.UI.Post.PostInfo;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralNotification extends Fragment {

    public GeneralNotification() {
        // Required empty public constructor
    }

    RecyclerView notificationList;
    GeneralNotificationAdapter adapter;
    List<NotificationForPost> postNotification = new ArrayList<>();
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");
    LinearLayout infoHolder;
    int color;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general_notification, container, false);
        notificationList = view.findViewById(R.id.notificationList);
        infoHolder = view.findViewById(R.id.infoHolder);
        adapter = new GeneralNotificationAdapter();

        adapter.setPostNotification(postNotification);

        adapterClicks();

        notificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList.setHasFixedSize(true);
        notificationList.setAdapter(adapter);

        fetchNotification();

        return view;
    }

    private void adapterClicks() {
        adapter.setOnClickListeners(new GeneralNotificationAdapter.OnClickListeners() {
            @Override
            public void onDeleteClick(int position) {
                NotificationForPost notificationForPost=postNotification.get(position);
                String postID=notificationForPost.getPostID();
                String id=IDs.get(position);

                postFolder.child(postID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    users.child(new UserInformation(getContext()).getMainUserID())
                                            .child("notifications").child(id).removeValue();
                                    postNotification.remove(position);
                                    IDs.remove(position);
                                    adapter.notifyItemRemoved(position);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void nameChoiceUser(int position, CircleImageView profileImage, TextView name) {

                NotificationForPost notification = postNotification.get(position);
                String mainID = new UserInformation(getContext()).getMainUserID();
                users.child(mainID).child("people")
                        .child(notification.getUserID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    displayImage(profileImage, notification.getUserID());
                                    ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                                    String text = contact.getUserContactName() + " " + notification.getTittle()+" "+notification.getBody();
                                    name.setText(text);
                                } else {
                                    setForNotContact(profileImage, notification.getUserID(), name, notification.getBody());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            private void displayImage(CircleImageView imageView, String userID) {
                users.child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                    Glide.with(getContext().getApplicationContext()).load(detail.getProfileURL()).into(imageView);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            private void setForNotContact(CircleImageView imageView, String userID, TextView name, String body) {
                users.child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                    Glide.with(getContext().getApplicationContext()).load(detail.getProfileURL()).into(imageView);
                                    String text = detail.getUsername() + " " + body;
                                    name.setText(text);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onItemClick(int position, View view) {

                NotificationForPost notification = postNotification.get(position);
                view.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                Intent intent = new Intent(getActivity(), CommentPage.class);

                String userID = new UserInformation(getContext()).getMainUserID();
                Map<String, Object> update = new HashMap<>();
                update.put("seen", true);
                users.child(userID).child("notifications").child(IDs.get(position))
                        .updateChildren(update);
                notification.setSeen(true);
                postNotification.set(position, notification);
                adapter.notifyItemChanged(position);

                postFolder.child(notification.getPostID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);
                                    dataSnapshot.getRef().child("postData")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot postDataSnapShot) {
                                                    if (postDataSnapShot.exists()) {
                                                        PostInfo info = postDataSnapShot.getValue(PostInfo.class);
                                                        Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(), cloudPost.getPOST_URL(),
                                                                cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME(), info.getPostIsOnRepeat(), "", info.isAllowPrivateComment());
                                                        intent.putExtra("postData", post);
                                                        intent.putExtra("notification", true);
                                                        intent.putExtra("message id", notification.getMessageID());
                                                        users.child(new UserInformation(getContext()).getMainUserID()).child("badge")
                                                                .child(post.getPostID()).removeValue();
                                                        MainFunctions.updateNotification(new UserInformation(getContext()).getMainUserID(),post.getPostID());
                                                        if(post.getOwnerID().equals(new UserInformation(getContext()).getMainUserID())){
                                                            postFolder.child(post.getPostID()).child("notification").removeValue();
                                                        }
                                                        startActivity(intent);

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

            @Override
            public void setTime(int position, TextView time) {
                NotificationForPost notification = postNotification.get(position);
                long timeStamp = notification.getTimeStamp();
                String notifyTime = TimeFactor.getDetailedDate(timeStamp,System.currentTimeMillis());
                time.setText(notifyTime);
                //set the time on the textView
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        loadTheme();
        fetchNotification();
    }

    List<String> IDs = new ArrayList<>();

    private void fetchNotification() {

        String userID = new UserInformation(getContext()).getMainUserID();
        users.child(userID).child("notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot notification : dataSnapshot.getChildren()) {
                                NotificationForPost notificationForPost = notification.getValue(NotificationForPost.class);
                                postFolder.child(notificationForPost.getPostID())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (!IDs.contains(notification.getKey())) {

                                                        postNotification.add(0, notificationForPost);
                                                        adapter.notifyItemInserted(0);
                                                        IDs.add(0, notification.getKey());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                            }
                        } else {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void setTextColors(int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                adapter.setColor(R.color.textColor);

                break;
            case Theme.DEEP:
                adapter.setColor(R.color.whiteGreen);
                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);
        adapter.notifyDataSetChanged();


    }
}
