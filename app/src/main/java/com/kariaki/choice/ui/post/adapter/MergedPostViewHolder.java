package com.kariaki.choice.ui.Post.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.time.TimeFactor;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MergedPostViewHolder extends PostMainViewHolder {

    ImageButton more_option ;
    ToggleButton like_one, like_two;
    TextView likeOneCount, likeTwoCount, MergedPageruserIDMergeds, MergePostCommentCount,mergePostTime;
    EmojiTextView mergedPostTittle;
    private ImageView mergePostImageOne, mergePostImageTwo;
    PostAdapter.OnclickListeners listen;
    private static final String LIST_OF_USERS = "users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersCollcetion = database.getReference(LIST_OF_USERS);
    private CircleImageView MergedPagerprofImage;
    private RelativeLayout mergePostImageHolder;
    private LinearLayout otherInfo;
    private ImageView otherInfoIcon,comment, MergePostShare;
    Toolbar mergePostToolbar;
    private TextView otherInfoName;
    TextView viewsCount;
    LinearLayout commentClick;
    CardView cardView;

    public MergedPostViewHolder(@NonNull View itemView) {
        super(itemView);
        more_option = itemView.findViewById(R.id.MergedPagerMoreOption);
        like_one = itemView.findViewById(R.id.Mergedlikeone);
        viewsCount=itemView.findViewById(R.id.viewsCount);
        otherInfo = itemView.findViewById(R.id.otherInfo);
        cardView=itemView.findViewById(R.id.itemView);
        otherInfoIcon = itemView.findViewById(R.id.otherInfoIcon);
        mergePostToolbar=itemView.findViewById(R.id.mergePostToolbar);
        mergePostTime=itemView.findViewById(R.id.mergePostTime);
        otherInfoName = itemView.findViewById(R.id.otherInfoName);
        commentClick=itemView.findViewById(R.id.commentClick);
        like_two = itemView.findViewById(R.id.Mergedliketwo);
        mergedPostTittle = itemView.findViewById(R.id.MergedPagerPostTitile);
        comment = itemView.findViewById(R.id.MergedPostComment);
        mergePostImageOne = itemView.findViewById(R.id.MergedcustomImage);
        mergePostImageTwo = itemView.findViewById(R.id.MergedcustomImageTwo);
        MergePostShare = itemView.findViewById(R.id.MergePostShare);
        likeOneCount = itemView.findViewById(R.id.MergedPostLikeOneCount);
        MergedPagerprofImage = itemView.findViewById(R.id.MergedPagerprofImage);
        likeTwoCount = itemView.findViewById(R.id.MergedPostLikeTwoCount);
        MergedPageruserIDMergeds = itemView.findViewById(R.id.MergedPageruserIDMergeds);
        MergePostCommentCount = itemView.findViewById(R.id.MergePostCommentCount);
        mergePostImageHolder = itemView.findViewById(R.id.mergePostImageHolder);
         mergePostTime = itemView.findViewById(R.id.mergePostTime);

    }

    Context CONTEXT;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindPostType(PostTypes types, Context context, int color) {

        Post post = (Post) types;


        mergedPostTittle.setText(post.getPOST_CAPTION());
        mergedPostTittle.setTextColor(context.getResources().getColor(color));
        String[] links = post.getPOST_URL().split(",");

        mergePostTime.setText(TimeFactor.getDetailedDate(post.getPOST_TIME(), System.currentTimeMillis()));
        MergedPageruserIDMergeds.setTextColor(context.getResources().getColor(color));
        if(color==R.color.whiteGreen){
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.postDark));
        }else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteGreen));

        }
        Functions.countComments(MergePostCommentCount, post.getPostID());

        Functions.mergeLikeCount(likeOneCount,post.getPostID(),post,"like_one");

        Functions.mergeLikeCount(likeTwoCount,post.getPostID(),post,"like_two");
        Functions.viewsCount(viewsCount,post.getPostID());


        Glide.with(context).load(links[0]).placeholder(context.getDrawable(R.drawable.square_place_holder)).into(mergePostImageOne);
        Glide.with(context).load(links[1]).placeholder(context.getDrawable(R.drawable.square_place_holder)).into(mergePostImageTwo);





    }

    @Override
    public void listeners(PostAdapter.OnclickListeners listeners) {

        listeners.postHelper(getAdapterPosition(),otherInfo,otherInfoName,otherInfoIcon);
        mergePostToolbar.setOnClickListener(v->{
            listeners.openSocialProfile(getAdapterPosition());
        });

        listen = listeners;
        more_option.setOnClickListener(v -> {
            listeners.onClickOption(getAdapterPosition());
        });
        comment.setOnClickListener(v -> {
            listeners.onClickComment(getAdapterPosition());
        });

        mergePostImageHolder.setOnClickListener(v -> {
            listeners.onClickItem(getAdapterPosition(),mergePostImageHolder);
        });


        listeners.nameChoiceUser(getAdapterPosition(), MergedPageruserIDMergeds, MergedPagerprofImage);

        MergePostShare.setOnClickListener(v -> {
            listen.onClickShare(getAdapterPosition());
        });
        int duration = 200;


        like_two.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listeners.onCheckChangeMergeLikeTwo(getAdapterPosition(), isChecked);

            if (isChecked) {
                like_one.setChecked(false);
                YoYo.with(Techniques.Pulse).duration(duration).playOn(like_two);
                //likeTwoCount.setBackgroundColor(CONTEXT.getResources().getColor(android.R.color.holo_red_light));

            } else {
                like_one.setChecked(true);
                //likeTwoCount.setBackgroundColor(CONTEXT.getResources().getColor(android.R.color.black));


            }
        });


        like_one.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            listen.onCheckChangeMergeLikeOne(getAdapterPosition(), isChecked);
            if (isChecked) {
                like_two.setChecked(false);
                YoYo.with(Techniques.Pulse).duration(duration).playOn(like_one);
                //likeOneCount.setBackgroundColor(CONTEXT.getResources().getColor(android.R.color.holo_red_light));


            } else {
                like_two.setChecked(true);
                //likeOneCount.setBackgroundColor(CONTEXT.getResources().getColor(android.R.color.black));


            }
        }));

        otherInfoIcon.setOnClickListener(v->{
            otherInfo.setVisibility(View.GONE);
            listeners.onClickLoop(getAdapterPosition());
        });

        commentClick.setOnClickListener(v->{
            listeners.onClickComment(getAdapterPosition());
        });

    }

}
