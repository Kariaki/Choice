package com.kariaki.choice.ui.post.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.model.Post;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.post.PostTypes;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostMainViewHolder> {


    public interface OnclickListeners {
        void onClickOption(int position);

        void onClickComment(int position);

        void onCheckChangeMergeLikeOne(int position, boolean isChecked);

        void onCheckChangeMergeLikeTwo(int position, boolean isChecked);

        int onCheckChangeSingles(int position, boolean isChecked);

        void onClickShare(int position);

        void onClickLoop(int position);

        void onClickItem(int position, View sharedItem);

        void nameChoiceUser(int position, TextView nameView, CircleImageView profileImageView);

        void updateLikes(int position, TextView likeText, int type);

        void updateComments(int position, TextView commentText);
        void postHelper(int position, LinearLayout iconsHolder, TextView text, ImageView icon);
        void openSocialProfile(int position);


    }

    OnclickListeners listeners;
    List<Post> posts = new ArrayList<>();
    Context CONTEXT;
    private int color = R.color.textHeaderColor;

    public void setColor(int color) {
        this.color = color;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void setOnclickListener(OnclickListeners listen) {
        listeners = listen;
    }

    public PostAdapter(Context context) {
        this.CONTEXT = context;
    }

    @NonNull
    @Override
    public PostMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        EmojiManager.install(new IosEmojiProvider());
        switch (viewType) {
            case PostTypes.SINGLE_POST:
                view = LayoutInflater.from(CONTEXT).inflate(R.layout.singlepost, parent, false);
                return new SinglePostViewHolder(view);
            case PostTypes.MERGED_POST:
                view = LayoutInflater.from(CONTEXT).inflate(R.layout.mergepost, parent, false);
                return new MergedPostViewHolder(view);
            case PostTypes.VIDEO_POST:
                view = LayoutInflater.from(CONTEXT).inflate(R.layout.video_post, parent, false);
                return new VideoPostViewHolder(view);
            case PostTypes.TEXT:
                view = LayoutInflater.from(CONTEXT).inflate(R.layout.text_post, parent, false);
                return new TextViewHolder(view);

            default:
                view=LayoutInflater.from(CONTEXT).inflate(R.layout.post_default,parent,false);

                return new PostDefaultViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull PostMainViewHolder holder, int position) {
        PostTypes types = posts.get(position);
        holder.bindPostType(types, CONTEXT, color);
        holder.listeners(listeners);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position).getPOST_TYPE();
    }
}
