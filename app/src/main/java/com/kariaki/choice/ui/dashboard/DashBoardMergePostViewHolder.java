package com.kariaki.choice.ui.Dashboard;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

public class DashBoardMergePostViewHolder extends DashBoardMainViewHolder {
    private ImageView dashBoardPostImageOnePreview, dashBoardPostImageTwoPreview;
    private TextView dashBoardSinglePostNotificationCount, dashBoardSinglePostCommentCount, dashBoardMergePostLikeOneCount,
            dashBoardMergePostLikeTwoCount, dashboardMergePostDay;
    EmojiTextView dashBoardMergePostCaption;
    private ImageView dashBoardDeleteMergePost;
    private RelativeLayout dashboardSinglePostNotificationCountHolder;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    ImageView postDashBoardMergeImageComment;
    DatabaseReference liveUsers = FirebaseDatabase.getInstance().getReference("users");

    UserInformation information;

    TextView viewsCount;
    CardView dashBoardSinglePostRoot;

    public DashBoardMergePostViewHolder(@NonNull View itemView) {
        super(itemView);
        dashBoardPostImageOnePreview = itemView.findViewById(R.id.dashBoardPostImageOnePreview);
        dashBoardPostImageTwoPreview = itemView.findViewById(R.id.dashBoardPostImageTwoPreview);
        dashBoardMergePostCaption = itemView.findViewById(R.id.dashBoardMergePostCaption);
        dashboardMergePostDay = itemView.findViewById(R.id.dashboardMergePostDay);

        dashBoardSinglePostRoot = itemView.findViewById(R.id.dashBoardSinglePostRoot);
        dashBoardDeleteMergePost = itemView.findViewById(R.id.dashBoardDeleteMergePost);
        dashboardSinglePostNotificationCountHolder = itemView.findViewById(R.id.dashboardMergePostNotificationCountHolder);
        dashBoardSinglePostNotificationCount = itemView.findViewById(R.id.dashBoardMergePostNotificationCount);
        dashBoardSinglePostCommentCount = itemView.findViewById(R.id.dashBoardMergePostCommentCount);
        viewsCount = itemView.findViewById(R.id.viewsCount);
        dashBoardMergePostLikeOneCount = itemView.findViewById(R.id.dashBoardMergePostLikeOneCount);
        dashBoardMergePostLikeTwoCount = itemView.findViewById(R.id.dashBoardMergePostLikeTwoCount);
        postDashBoardMergeImageComment = itemView.findViewById(R.id.postDashBoardMergeImageComment);
        information = new UserInformation(itemView.getContext());
        viewsCount = itemView.findViewById(R.id.viewsCount);

    }


    private void updateLikesAndComments(String key) {
        DatabaseReference commentCount = database.getReference("post").child(key).child("comments");
        DatabaseReference likeCount = database.getReference("post").child(key).child("like_one");
        DatabaseReference likeTwoCount = database.getReference("post").child(key).child("like_two");

        likeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dashBoardMergePostLikeOneCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    dashBoardMergePostLikeOneCount.setText(String.valueOf(0));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        likeTwoCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dashBoardMergePostLikeTwoCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    dashBoardMergePostLikeTwoCount.setText(String.valueOf(0));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        commentCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dashBoardSinglePostCommentCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                } else {
                    dashBoardSinglePostCommentCount.setText(String.valueOf(0));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    void onBind(PostTypes post, int color) {

        if (color == R.color.whiteGreen) {
            //postDark
            dashBoardSinglePostRoot.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.postDark));
            dashboardMergePostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));

        } else {
            dashBoardSinglePostRoot.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
            dashboardMergePostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));


        }
        CloudPost cloudPost = (CloudPost) post;
        String splitter[] = cloudPost.getPOST_URL().split(",");
        Glide.with(itemView).load(splitter[0]).placeholder(R.drawable.square_place_holder).into(dashBoardPostImageOnePreview);
        Glide.with(itemView).load(splitter[1]).placeholder(R.drawable.square_place_holder).into(dashBoardPostImageTwoPreview);


        dashBoardMergePostCaption.setTextColor(itemView.getContext().getResources().getColor(color));
        dashboardMergePostDay.setText(TimeFactor.getDetailedDate(cloudPost.getPOST_TIME(), System.currentTimeMillis()));

        String caption = cloudPost.getPOST_CAPTION();
        dashBoardMergePostCaption.setText(caption);
        updateLikesAndComments(cloudPost.getPostID());

        DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());

        updateViewsCount(postFolder);

    }

    @Override
    void listeners(PostDashBoardAdapter.OnClickDashBoardListener listener) {
        //    listener.postStatistics(getAdapterPosition());


        itemView.setOnClickListener(v -> {
            listener.onClickPostDashItem(getAdapterPosition());
        });

        dashBoardDeleteMergePost.setOnClickListener(v -> {
            listener.onClickDelete(getAdapterPosition());

            dashboardSinglePostNotificationCountHolder.setVisibility(View.INVISIBLE);
            dashBoardSinglePostNotificationCount.setVisibility(View.INVISIBLE);
        });

        listener.notificationDashBoard(getAdapterPosition(), dashboardSinglePostNotificationCountHolder, dashBoardSinglePostNotificationCount);
    }

    private void updateViewsCount(DatabaseReference postFolder) {
        postFolder.child("views")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String text = String.valueOf(dataSnapshot.getChildrenCount()) + " views";
                            viewsCount.setVisibility(View.VISIBLE);
                            viewsCount.setText(text);
                        } else {
                            viewsCount.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
