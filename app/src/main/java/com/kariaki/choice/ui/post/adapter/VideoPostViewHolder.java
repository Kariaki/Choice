package com.kariaki.choice.ui.post.adapter;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.post.PostTypes;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPostViewHolder extends PostMainViewHolder {

    private ImageView videoView;
    private ImageButton videoPostMoreOption;
    private ToggleButton videoPostlikeButton;
    TextView videoPostLikeCount,videoPostCommentCount;
    TextView videoPostUserID,videoPostTime;
    EmojiTextView singlePostCaption;
    Toolbar videoPosttoolbar;
    ImageView videoPostComment,videoPostshareButton;
    TextView viewsCount;
    CardView cardView;
    private Context CONTEXT;

    private static final String LIST_OF_USERS = "users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersCollcetion = database.getReference(LIST_OF_USERS);
    private Post POST;
    private TextView otherInfoName;
    private LinearLayout otherInfo;

    private ImageView otherInfoIcon;

    private CircleImageView videoPostprofileImage;
    private TextView repostCount;

    LinearLayout commentButtonHolder,likeButtonHolder,shareHolder;


    public VideoPostViewHolder(@NonNull View itemView) {

        super(itemView);
        videoView=itemView.findViewById(R.id.videoPost);
        videoPostComment=itemView.findViewById(R.id.videoPostComment);
        videoPostshareButton=itemView.findViewById(R.id.videoPostshareButton);
        videoPostUserID=itemView.findViewById(R.id.videoPostUserID);
        singlePostCaption=itemView.findViewById(R.id.singlePostCaption);
        videoPosttoolbar=itemView.findViewById(R.id.videoPosttoolbar);
        cardView=itemView.findViewById(R.id.itemView);
        likeButtonHolder=itemView.findViewById(R.id.likeButtonHolder);
        commentButtonHolder=itemView.findViewById(R.id.commentButtonHolder);
        shareHolder=itemView.findViewById(R.id.shareHolder);

        otherInfoIcon=itemView.findViewById(R.id.otherInfoIcon);
        viewsCount=itemView.findViewById(R.id.viewsCount);
        otherInfoName=itemView.findViewById(R.id.otherInfoName);
        otherInfo=itemView.findViewById(R.id.otherInfo);
        repostCount=itemView.findViewById(R.id.repostCount);
        videoPostTime=itemView.findViewById(R.id.videoPostTime);
        videoPostlikeButton=itemView.findViewById(R.id.videoPostlikeButton);
        videoPostLikeCount=itemView.findViewById(R.id.videoPostLikeCount);
        videoPostprofileImage=itemView.findViewById(R.id.videoPostprofileImage);
        videoPostCommentCount=itemView.findViewById(R.id.videoPostCommentCount);
        videoPostMoreOption=itemView.findViewById(R.id.videoPostMoreOption);


    }

    public View findViewById(int id){
        return itemView.findViewById(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindPostType(PostTypes types, Context context, int color) {

        Post post=(Post) types;
        String id=post.getOwnerID();
        singlePostCaption.setText(post.getPOST_CAPTION());
        singlePostCaption.setTextColor(context.getResources().getColor(color));
        videoPostUserID.setTextColor(context.getResources().getColor(color));
        videoPostTime.setText(TimeFactor.getDetailedDate(post.getPOST_TIME(),System.currentTimeMillis()));
        CONTEXT=context;
        POST=post;
        Glide.with(context.getApplicationContext()).load(post.getPOST_URL()).placeholder(R.drawable.square_place_holder).into(videoView);

        if(color==R.color.whiteGreen){
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.postDark));
        }else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteGreen));

        }
        otherInfoIcon.setOnClickListener(v->{
            otherInfo.setVisibility(View.GONE);
            otherInfoIcon.setVisibility(View.GONE);
        });
      //  videoPostCommentCount.setText(String.valueOf(post.getComments()));
       // videoPostLikeCount.setText(String.valueOf(post.getLikes()));


        Functions.countComments(videoPostCommentCount,POST.getPostID());

        Functions.countLikes(videoPostLikeCount,POST.getPostID(),post);
        Functions.viewsCount(viewsCount,post.getPostID());

    }



    @Override
    public void listeners(PostAdapter.OnclickListeners listeners) {

        videoPosttoolbar.setOnClickListener(v->{
            listeners.openSocialProfile(getAdapterPosition());
        });
        listeners.nameChoiceUser(getAdapterPosition(),videoPostUserID,videoPostprofileImage);

        listeners.postHelper(getAdapterPosition(),otherInfo,otherInfoName,otherInfoIcon);

        videoPostlikeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            YoYo.with(Techniques.Pulse).duration(200).playOn(videoPostlikeButton);

            listeners.onCheckChangeSingles(getAdapterPosition(), isChecked);


        });
        likeButtonHolder.setOnClickListener(v->{
            boolean isChecked=videoPostlikeButton.isChecked();
            YoYo.with(Techniques.Pulse).duration(200).playOn(videoPostlikeButton);


            if (isChecked) {
               videoPostlikeButton.setChecked(false);
               isChecked=false;
            } else {

                videoPostlikeButton.setChecked(true);
                isChecked=true;
            }
            listeners.onCheckChangeSingles(getAdapterPosition(), isChecked);


        });
        videoPostLikeCount.setOnClickListener(v->{
            if(videoPostlikeButton.isChecked()){
                videoPostlikeButton.setChecked(false);
            }else {
                videoPostlikeButton.setChecked(true);
            }
        });
        videoPostCommentCount.setOnClickListener(v -> {

            listeners.onClickComment(getAdapterPosition());
        });


        itemView.setOnClickListener(v->{
            listeners.onClickItem(getAdapterPosition(),videoView);
        });

        videoPostComment.setOnClickListener(v->{
            listeners.onClickComment(getAdapterPosition());

        });
        commentButtonHolder.setOnClickListener(v->{
            listeners.onClickComment(getAdapterPosition());

        });

        videoPostshareButton.setOnClickListener(v->{
            listeners.onClickShare(getAdapterPosition());
        });
        shareHolder.setOnClickListener(v->{
            listeners.onClickShare(getAdapterPosition());

        });
        repostCount.setOnClickListener(v -> {
            listeners.onClickShare(getAdapterPosition());
        });
        videoPostMoreOption.setOnClickListener(v->{
            listeners.onClickOption(getAdapterPosition());

        });
        otherInfo.setOnClickListener(v->{
            listeners.onClickLoop(getAdapterPosition());
            otherInfo.setVisibility(View.GONE);
        });

    }
}
