package com.kariaki.choice.ui.Post.Adapter;

import android.annotation.SuppressLint;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Time.TimeFactor;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class TextViewHolder extends PostMainViewHolder {

    private TextView textPostuserID, textPostCommentCount, textPostLikeCount;

    private EmojiTextView pager;
    private CircleImageView textPostprofileImage;
    private ImageButton  textPostMoreOption;
    ImageView textPostComment,textPostshareButton;
    private ToggleButton textPostLikeButton;
    private int likeCount;
    Toolbar textPosttoolbar;
    private int originalLikeCount;
    CardView cardView;
    LinearLayout commentClick;
    TextView repostCount;

    public TextViewHolder(@NonNull View itemView) {

        super(itemView);

        EmojiManager.install(new IosEmojiProvider());
        pager = itemView.findViewById(R.id.post_text);
        textPostuserID = itemView.findViewById(R.id.textPostuserID);

        textPostprofileImage = itemView.findViewById(R.id.textPostprofileImage);
        textPostshareButton = itemView.findViewById(R.id.textPostshareButton);
        textPosttoolbar=itemView.findViewById(R.id.textPosttoolbar);
        cardView=itemView.findViewById(R.id.itemView);
        repostCount=itemView.findViewById(R.id.repostCount);
        commentClick=itemView.findViewById(R.id.commentClick);

        textPostComment = itemView.findViewById(R.id.textPostComment);
        textPostMoreOption = itemView.findViewById(R.id.textPostMoreOption);
        textPostCommentCount = itemView.findViewById(R.id.textPostCommentCount);
        textPostLikeCount = itemView.findViewById(R.id.textPostLikeCount);
        textPostLikeButton = itemView.findViewById(R.id.textPostlikeButton);
        textPostTime=itemView.findViewById(R.id.textPostTime);
        otherInfo=itemView.findViewById(R.id.otherInfo);
        otherInfoIcon=itemView.findViewById(R.id.otherInfoIcon);
        otherInfoName=itemView.findViewById(R.id.otherInfoName);

    }

    private Context CONTEXT;
    private static final String LIST_OF_USERS = "users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersCollcetion = database.getReference(LIST_OF_USERS);
    private String folder = "post";
    DatabaseReference postFolder = database.getReference(folder);
    private TextView textPostTime;
    int count = 0;
    Post insidePost;
    private LinearLayout otherInfo;
    private TextView otherInfoName;
    private ImageView otherInfoIcon;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindPostType(PostTypes types, Context context, int color) {

        Post post = (Post) types;
        pager.setText(post.getPOST_CAPTION());
        textPostuserID.setTextColor(context.getResources().getColor(color));


        Functions.countComments(textPostCommentCount, post.getPostID());
        if(color==R.color.whiteGreen){
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.postDark));
        }else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteGreen));

        }

        insidePost = post;
        textPostTime.setText(TimeFactor.getDetailedDate((post.getPOST_TIME()),System.currentTimeMillis()));

        CONTEXT = context;
        Functions.countLikes(textPostLikeCount,post.getPostID(),post);

        if(post.isRepeat()){
            otherInfo.setVisibility(View.VISIBLE);
        }


    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void listeners(PostAdapter.OnclickListeners listeners) {

        listeners.nameChoiceUser(getAdapterPosition(),textPostuserID,textPostprofileImage);

        listeners.postHelper(getAdapterPosition(),otherInfo,otherInfoName,otherInfoIcon);

        textPosttoolbar.setOnClickListener(v->{
            listeners.openSocialProfile(getAdapterPosition());
        });

        textPostComment.setOnClickListener(v -> {
            listeners.onClickComment(getAdapterPosition());
        });
        textPostshareButton.setOnClickListener(v -> {
            listeners.onClickShare(getAdapterPosition());
        });
        textPostMoreOption.setOnClickListener(v -> {
            listeners.onClickOption(getAdapterPosition());
        });
        textPostLikeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            YoYo.with(Techniques.Pulse).duration(200).playOn(textPostLikeButton);
            listeners.onCheckChangeSingles(getAdapterPosition(),isChecked);
            if (isChecked) {
                likeCount++;
                textPostLikeCount.setTextColor(CONTEXT.getResources().getColor(android.R.color.holo_red_light));

            } else {
                likeCount = originalLikeCount;
                textPostLikeCount.setTextColor(CONTEXT.getResources().getColor(android.R.color.black));
            }
        });

        itemView.setOnClickListener(v->{
            listeners.onClickItem(getAdapterPosition(),itemView);
        });

        otherInfoIcon.setOnClickListener(v->{
            otherInfo.setVisibility(View.GONE);
            listeners.onClickLoop(getAdapterPosition());
        });

        commentClick.setOnClickListener(v->{
            listeners.onClickComment(getAdapterPosition());
        });
        textPostCommentCount.setOnClickListener(v -> {
            listeners.onClickComment(getAdapterPosition());
        });
        textPostLikeCount.setOnClickListener(v->{
            if(textPostLikeButton.isChecked()){
                textPostLikeButton.setChecked(false);
            }else {
                textPostLikeButton.setChecked(true);
            }
        });
        repostCount.setOnClickListener(v -> {
            listeners.onClickShare(getAdapterPosition());
        });

    }
}
