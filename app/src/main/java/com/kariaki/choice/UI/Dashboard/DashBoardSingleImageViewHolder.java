package com.kariaki.choice.UI.Dashboard;

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
import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Post.PostTypes;
import com.kariaki.choice.UI.util.Functions;
import com.kariaki.choice.UI.util.LastCheck;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

public class DashBoardSingleImageViewHolder extends DashBoardMainViewHolder {
    ImageView dashBoardPostImagePreview;
    TextView dashBoardSinglePostCommentCount, dashBoardSinglePostLikeCount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    EmojiTextView dashBoardPostCaption;
    ImageView dashBoardDeletePost;
    RelativeLayout dashboardSinglePostNotificationCountHolder;
    PostDashBoardAdapter.OnClickDashBoardListener listener;
    View thisView;
    UserInformation information;
    TextView dashBoardSinglePostNotificationCount, dashboardSinglePostDay;
    DatabaseReference liveUsers = FirebaseDatabase.getInstance().getReference("users");
    RelativeLayout detailsHolder;
    CardView dashBoardSinglePostRoot;
    TextView viewsCount;


    //TextView viewsCount;
    public DashBoardSingleImageViewHolder(@NonNull View itemView) {
        super(itemView);

        dashBoardPostImagePreview = itemView.findViewById(R.id.dashBoardPostImagePreview);
        dashBoardPostCaption = itemView.findViewById(R.id.dashBoardPostCaption);
        dashBoardSinglePostRoot = itemView.findViewById(R.id.itemView);
        viewsCount = itemView.findViewById(R.id.viewsCount);
        dashBoardSinglePostLikeCount = itemView.findViewById(R.id.dashBoardSinglePostLikeCount);
        dashBoardSinglePostCommentCount = itemView.findViewById(R.id.dashBoardSinglePostCommentCount);
        dashBoardSinglePostRoot = itemView.findViewById(R.id.dashBoardSinglePostRoot);

        dashBoardSinglePostNotificationCount = itemView.findViewById(R.id.dashBoardSinglePostNotificationCount);
        dashboardSinglePostNotificationCountHolder = itemView.findViewById(R.id.dashboardSinglePostNotificationCountHolder);
        // postDashBoardSingleImageComment = itemView.findViewById(R.id.postDashBoardSingleImageComment);
        dashboardSinglePostDay = itemView.findViewById(R.id.dashboardSinglePostDay);
        // viewsCount=itemView.findViewById(R.id.viewsCount);
        information = new UserInformation(itemView.getContext());

        thisView = itemView;


    }

    private void updateLikesAndComments(String key) {
        DatabaseReference commentCount = database.getReference("post").child(key).child("comments");
        DatabaseReference likeCount = database.getReference("post").child(key).child("likes");


        likeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dashBoardSinglePostLikeCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    dashBoardSinglePostLikeCount.setText(String.valueOf(""));

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
                    dashBoardSinglePostCommentCount.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBind(PostTypes post, int color) {

        if (color == R.color.whiteGreen) {

            dashBoardSinglePostRoot.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.postDark));
          //  dashboardSinglePostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));

        } else {
            dashBoardSinglePostRoot.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
           // dashboardSinglePostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));

        }


        CloudPost cloudPost = (CloudPost) post;
        dashBoardPostCaption.setText(cloudPost.getPOST_CAPTION());
        String postUrl[] = cloudPost.getPOST_URL().split(",");
        dashboardSinglePostDay.setText(TimeFactor.getDetailedDate(cloudPost.getPOST_TIME(), System.currentTimeMillis()));

        //dashBoardPostCaption.setTextColor(itemView.getContext().getResources().getColor(color));
        Glide.with(dashBoardSinglePostRoot).load(postUrl[0]).placeholder(R.drawable.square_place_holder).into(dashBoardPostImagePreview);

        updateLikesAndComments(cloudPost.getPostID());

        DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());
        Functions.viewsCount(viewsCount, cloudPost.getPostID());


    }

    @Override
    void listeners(PostDashBoardAdapter.OnClickDashBoardListener listener) {
        this.listener = listener;
        dashBoardDeletePost = itemView.findViewById(R.id.dashBoardDeletePost);


        dashBoardDeletePost.setOnClickListener(v -> {

            listener.onClickDelete(getAdapterPosition());
        });

        itemView.setOnClickListener(v -> {
            listener.onClickPostDashItem(getAdapterPosition());


            dashboardSinglePostNotificationCountHolder.setVisibility(View.INVISIBLE);
            dashBoardSinglePostNotificationCount.setVisibility(View.INVISIBLE);
        });
        listener.notificationDashBoard(getAdapterPosition(), dashboardSinglePostNotificationCountHolder, dashBoardSinglePostNotificationCount);
    }


}
