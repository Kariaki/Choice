package com.kariaki.choice.UI.Dashboard;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Post.PostTypes;
import com.kariaki.choice.UI.util.LastCheck;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

public class DashBoardTextPostViewHolder extends DashBoardMainViewHolder {
    TextView  textDashBoardSharesCount, textDashBoardCommentCount, textDashBoardLikeCount;
    ImageView dashBoardDeleteTextPost,postDashBoardTextComment;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RelativeLayout dashboardTextPostNotificationCountHolder;
    EmojiTextView dashBoardTextPostCaption;

    TextView dashBoardTextPostNotificationCount,dashBoardTextPostDay;
    DatabaseReference liveUsers=FirebaseDatabase.getInstance().getReference("users");
    UserInformation information;
    CardView itemCard;


    public DashBoardTextPostViewHolder(@NonNull View itemView) {
        super(itemView);
        dashBoardTextPostCaption = itemView.findViewById(R.id.dashBoardTextPostCaption);
        dashBoardDeleteTextPost = itemView.findViewById(R.id.dashBoardDeleteTextPost);
        textDashBoardLikeCount = itemView.findViewById(R.id.textDashBoardLikeCount);

        itemCard=itemView.findViewById(R.id.itemView);
        dashBoardTextPostDay=itemView.findViewById(R.id.dashBoardTextPostDay);
        textDashBoardCommentCount = itemView.findViewById(R.id.textDashBoardCommentCount);
        dashboardTextPostNotificationCountHolder=itemView.findViewById(R.id.dashboardTextPostNotificationCountHolder);

        postDashBoardTextComment=itemView.findViewById(R.id.postDashBoardTextComment);
        dashBoardTextPostNotificationCount=itemView.findViewById(R.id.dashBoardTextPostNotificationCount);

        information=new UserInformation(itemView.getContext());


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    void onBind(PostTypes post, int color) {

        if(color==R.color.whiteGreen){
            itemCard.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.postDark));
           // dashBoardTextPostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));

        }else {
            itemCard.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.whiteGreen));
            //dashBoardTextPostDay.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));

        }


        CloudPost cloudPost = (CloudPost) post;
        dashBoardTextPostCaption.setText(cloudPost.getPOST_CAPTION());
        //dashBoardTextPostDay.setText(date);
        dashBoardTextPostDay.setText(TimeFactor.getDetailedDate(cloudPost.getPOST_TIME(),System.currentTimeMillis()));

        updateLikesAndComments(cloudPost.getPostID());



    }

    private void updateLikesAndComments(String key){
        DatabaseReference commentCount = database.getReference("post").child(key).child("comments");
        DatabaseReference likeCount = database.getReference("post").child(key).child("likes");
        likeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textDashBoardLikeCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    textDashBoardLikeCount.setText("");

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
                    textDashBoardCommentCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    textDashBoardCommentCount.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    void listeners(PostDashBoardAdapter.OnClickDashBoardListener listener) {


        dashBoardDeleteTextPost.setOnClickListener(v -> {
            listener.onClickDelete(getAdapterPosition());
        });


        itemView.setOnClickListener(v->{
            listener.onClickPostDashItem(getAdapterPosition());

            dashboardTextPostNotificationCountHolder.setVisibility(View.INVISIBLE);
            dashBoardTextPostNotificationCount.setVisibility(View.INVISIBLE);
        });

        listener.notificationDashBoard(getAdapterPosition(),dashboardTextPostNotificationCountHolder,dashBoardTextPostNotificationCount);

    }
}
