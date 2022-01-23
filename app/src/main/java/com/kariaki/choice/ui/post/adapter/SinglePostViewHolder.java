package com.kariaki.choice.ui.post.adapter;

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
import androidx.cardview.widget.CardView;
import androidx.core.widget.ContentLoadingProgressBar;

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
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePostViewHolder extends PostMainViewHolder {
    ImageButton more_option;
    private ToggleButton likeButton;
    PostAdapter.OnclickListeners listen;
    private EmojiTextView singlePostCaption;
    private ImageView singlePostImageView;
    private TextView singlepostLikecount, singlePostUserID, singlePostCommentCount;
    private CircleImageView singlePostprofileImage;
    private boolean checkChange;
    int originalLikeCount;
    int likeCount;
    ContentLoadingProgressBar singleImageLoading;
    ImageView imageOne, imageTwo, imageThree, imageFour;
    View split, split2;

    RelativeLayout singlePostRootView;
    RelativeLayout singleImageSwitcher;
    private LinearLayout otherInfo;
    private TextView otherInfoName;
    private ImageView otherIcon, singlePostshareButton,comment;

    TextView viewsCount;
    CardView cardView;
    TextView repostText;
    LinearLayout commentClick,likeButtonHolder,shareHolder,commentButtonHolder;

    public SinglePostViewHolder(@NonNull View itemView) {
        super(itemView);
        EmojiManager.install(new IosEmojiProvider());
        more_option = itemView.findViewById(R.id.singlePostMoreOption);
        imageOne = itemView.findViewById(R.id.imageOne);
        imageTwo = itemView.findViewById(R.id.imageTwo);
        otherIcon = itemView.findViewById(R.id.otherIcon);
        cardView=itemView.findViewById(R.id.itemView);
        commentButtonHolder=itemView.findViewById(R.id.commentButtonHolder);
        likeButtonHolder=itemView.findViewById(R.id.likeButtonHolder);
        shareHolder=itemView.findViewById(R.id.shareHolder);
        viewsCount=itemView.findViewById(R.id.viewsCount);
        otherInfoName = itemView.findViewById(R.id.otherInfoName);
        singleImageSwitcher = itemView.findViewById(R.id.singleImageSwitcher);
        imageThree = itemView.findViewById(R.id.imageThree);
        otherInfo = itemView.findViewById(R.id.otherInfo);
        repostText=itemView.findViewById(R.id.repostText);
        split2 = itemView.findViewById(R.id.split2);
        split = itemView.findViewById(R.id.split);
        likeButton = itemView.findViewById(R.id.singlePostlikeButton);
        imageFour = itemView.findViewById(R.id.imageFour);
        comment = itemView.findViewById(R.id.singlePostComment);
        singlePostCaption = itemView.findViewById(R.id.singlePostCaption);
        singlepostLikecount = itemView.findViewById(R.id.singlePostLikeCount);
        //singlePostImageView=itemView.findViewById(R.id.singlePostImage);
        singlePostshareButton = itemView.findViewById(R.id.singlePostshareButton);
        singlePostUserID = itemView.findViewById(R.id.singlePostUserID);
        singlePostprofileImage = itemView.findViewById(R.id.singlePostprofileImage);
        singlePostCommentCount = itemView.findViewById(R.id.singlePostCommentCount);
        commentClick=itemView.findViewById(R.id.commentClick);

        singlePostRootView = itemView.findViewById(R.id.singlePostRootView);
        singlePostTime = itemView.findViewById(R.id.singlePostTime);
        //adapter=new SinglePostViewPagerAdapter(itemView.getContext().getApplicationContext());


    }

    private Context CONTEXT;

    private static final String LIST_OF_USERS = "users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersCollcetion = database.getReference(LIST_OF_USERS);
    private Post POST;
    private TextView singlePostTime;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void bindPostType(PostTypes types, Context context, int color) {
        Post post = (Post) types;
        POST = post;

        singlePostUserID.setTextColor(context.getResources().getColor(color));
       // singlePostCaption.setTextColor(context.getResources().getColor(color));
        if(color==R.color.whiteGreen){
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.postDark));
        }else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteGreen));

        }

        singlePostTime.setText(TimeFactor.getDetailedDate(post.getPOST_TIME(), System.currentTimeMillis()));

       // singlepostLikecount.setText(String.valueOf(post.getLikes()));
       // singlePostCommentCount.setText(String.valueOf(post.getComments()));

        Functions.countLikes(singlepostLikecount, POST.getPostID(),post);
        Functions.countComments(singlePostCommentCount, POST.getPostID());
        Functions.viewsCount(viewsCount,post.getPostID());



        String urls[] = post.getPOST_URL().split(",");

        setViews(urls.length);

        shareImages(urls, context);
        // Glide.with(context.getApplicationContext()).load(post.getPOST_URL()).into(singlePostImageView);


        singlePostCaption.setText(post.getPOST_CAPTION());


        CONTEXT = context;


    }

    private void setViews(int numberOfImages) {
        switch (numberOfImages) {
            case 1:
                split2.setVisibility(View.GONE);
                imageTwo.setVisibility(View.GONE);

                break;
            case 2:
                imageTwo.setVisibility(View.VISIBLE);
                split2.setVisibility(View.GONE);
                break;
            case 3:
                imageOne.setVisibility(View.GONE);
                break;
            case 4:
                imageTwo.setVisibility(View.VISIBLE);
                split.setVisibility(View.VISIBLE);
                break;



        }
    }

    private void shareImages(String[] links, Context context) {
        ImageView[] images = {imageOne, imageTwo, imageThree, imageFour};

        switch (links.length) {
            case 1:
                Glide.with(context).load(links[0]).into(imageFour);

                break;
            case 2:

                Glide.with(context).load(links[0]).into(imageOne);

                Glide.with(context).load(links[1]).into(imageTwo);
                break;
            case 3:
                Glide.with(context).load(links[1]).into(imageThree);

                Glide.with(context).load(links[0]).into(imageTwo);

                Glide.with(context).load(links[2]).into(imageFour);
                break;
            case 4:


                Glide.with(context).load(links[0]).into(imageOne);
                Glide.with(context).load(links[1]).into(imageTwo);

                Glide.with(context).load(links[2]).into(imageThree);
                Glide.with(context).load(links[3]).into(imageFour);

                break;


        }
    }


    @Override
    public void listeners(PostAdapter.OnclickListeners listeners) {
        listen = listeners;

        singlePostprofileImage.setOnClickListener(v->{
            listeners.openSocialProfile(getAdapterPosition());
        });
        singlePostUserID.setOnClickListener(v->{
            listeners.openSocialProfile(getAdapterPosition());
        });

        listeners.postHelper(getAdapterPosition(),otherInfo,otherInfoName,otherIcon);

        listeners.nameChoiceUser(getAdapterPosition(), singlePostUserID, singlePostprofileImage);

        //listeners.updateLikes(getAdapterPosition(),singlepostLikecount,PostLikeTypes.NORMAL);

        singleImageSwitcher.setOnClickListener(v -> {
            listeners.onClickItem(getAdapterPosition(), singleImageSwitcher);
        });

        shareHolder.setOnClickListener(v -> {
            listeners.onClickShare(getAdapterPosition());
        });
        commentButtonHolder.setOnClickListener(v -> {
            listeners.onClickComment(getAdapterPosition());
        });


        likeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            YoYo.with(Techniques.Pulse).duration(200).playOn(likeButton);
            listen.onCheckChangeSingles(getAdapterPosition(), isChecked);

            if (isChecked) {
                likeCount++;
                //singlepostLikecount.setText(String.valueOf(likeCount));

            } else {
                likeCount = originalLikeCount;
                //singlepostLikecount.setText(String.valueOf(likeCount));
            }
        });
        likeButtonHolder.setOnClickListener(v->{
            boolean isChecked=likeButton.isChecked();
            if(isChecked){
                isChecked=false;
                likeButton.setChecked(false);
            }else {
                isChecked=true;
                likeButton.setChecked(true);
            }
            YoYo.with(Techniques.Pulse).duration(200).playOn(likeButton);
            listen.onCheckChangeSingles(getAdapterPosition(), isChecked);

        });


        more_option.setOnClickListener(v -> {
            listeners.onClickOption(getAdapterPosition());
        });

        otherIcon.setOnClickListener(v->{
      otherInfo.setVisibility(View.GONE);
        listeners.onClickLoop(getAdapterPosition());
    }) ;
        singlePostCommentCount.setOnClickListener(v->{
            listeners.onClickComment(getAdapterPosition());
        });
        singlepostLikecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeButton.isChecked()){
                    likeButton.setChecked(false);
                }else {
                    likeButton.setChecked(true);
                }
            }
        });
        repostText.setOnClickListener(v -> {
            listeners.onClickShare(getAdapterPosition());
        });
    }

}
