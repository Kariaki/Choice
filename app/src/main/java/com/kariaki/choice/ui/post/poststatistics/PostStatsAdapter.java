package com.kariaki.choice.ui.Post.PostStatistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostStatsAdapter extends RecyclerView.Adapter<PostStatsAdapter.innerViewHolder> {

    List<UserDetail>userDetailList=new ArrayList<>();

    public interface actionListeners{
        void onLikeAction(int position, ImageView icon);
        void onViewAction(int position, ImageView icon);
    }
    private actionListeners actionListeners;
    private Context context;

    public void setActionListeners(PostStatsAdapter.actionListeners actionListeners) {
        this.actionListeners = actionListeners;
    }

    public void setUserDetailList(List<UserDetail> userDetailList) {
        this.userDetailList = userDetailList;
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_respond_user,parent,false);
        context=parent.getContext();
        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        Glide.with(context).load(userDetailList.get(position).getProfileURL()).into(holder.userProfilePicture);
        holder.userListUserName.setText(userDetailList.get(position).getUsername());
        actionListeners.onLikeAction(position,holder.likeIcon);
        actionListeners.onViewAction(position,holder.viewIcon);


    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userProfilePicture;
        TextView userListUserName;
        ImageView viewIcon,likeIcon;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePicture=itemView.findViewById(R.id.userProfilePicture);
            userListUserName=itemView.findViewById(R.id.userListUserName);
            viewIcon=itemView.findViewById(R.id.viewIcon);
            likeIcon=itemView.findViewById(R.id.likeIcon);
        }
    }
}
