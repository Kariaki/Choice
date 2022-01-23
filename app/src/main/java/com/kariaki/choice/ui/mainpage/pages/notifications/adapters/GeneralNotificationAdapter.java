package com.kariaki.choice.ui.mainpage.pages.notifications.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.model.database.entities.NotificationForPost;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.UserNaming;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralNotificationAdapter extends RecyclerView.Adapter<GeneralNotificationAdapter.innerViewHolder> {

    public interface OnClickListeners{

        void nameChoiceUser(int position,CircleImageView profileImage,TextView name);
        void onItemClick(int position,View view);
        void setTime(int position,TextView time);
        void onDeleteClick(int position);

    }

    int color=R.color.textColor;
    private OnClickListeners onClickListeners;

    public void setColor(int color) {
        this.color = color;
    }

    public void setOnClickListeners(OnClickListeners onClickListeners) {
        this.onClickListeners = onClickListeners;
        UserNaming naming;
    }

    List<NotificationForPost>postNotification=new ArrayList<>();

    public void setPostNotification(List<NotificationForPost> postNotification) {
        notifyDataSetChanged();
        this.postNotification = postNotification;
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.general_notification,parent,false);
        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {
        onClickListeners.nameChoiceUser(position,holder.profileImage,holder.notificationContent);
        holder.notificationContent.setText(postNotification.get(position).getBody());
        holder.notificationContent.setTextColor(holder.itemView.getContext().getResources().getColor(color));
        onClickListeners.onDeleteClick(position);
        holder.itemView.setOnClickListener(v->{
            onClickListeners.onItemClick(position,holder.itemView);

        });
        if(postNotification.get(position).isSeen()){
            holder.rootView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));

        }
        onClickListeners.setTime(position,holder.notificationTime);

    }

    @Override
    public int getItemCount() {
        return postNotification.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        EmojiTextView notificationContent;
        TextView notificationTime;
        RelativeLayout rootView;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.userProfilePicture);
            notificationContent=itemView.findViewById(R.id.notificationContent);
            rootView=itemView.findViewById(R.id.rootView);
             notificationTime=itemView.findViewById(R.id.notificationTime);


        }
    }
}
