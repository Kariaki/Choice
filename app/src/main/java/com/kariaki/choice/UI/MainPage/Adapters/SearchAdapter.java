package com.kariaki.choice.UI.MainPage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.R;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends  RecyclerView.Adapter<SearchAdapter.innerViewHolder>{

    List<UserDetail>userDetailList=new ArrayList<>();

    public void setUserDetailList(List<UserDetail> userDetailList) {
        this.userDetailList = userDetailList;
    }


    public interface contactCheck{
        void contactCheck(int position, LinearLayout icon);
        void searchResultClick(int position);
    }

    private contactCheck check;

    public void setCheck(contactCheck check) {
        this.check = check;
    }

    Context CONTEXT;
    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CONTEXT=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_design,parent,false);

        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        holder.userListUserName.setText(userDetailList.get(position).getUsername());
        Glide.with(CONTEXT.getApplicationContext()).load(userDetailList.get(position).getProfileURL()).into(holder.userProfilePicture);
        check.contactCheck(position,holder.inContactHolder);
        holder.userListAbout.setText(userDetailList.get(position).getAbout());
        holder.itemView.setOnClickListener(v->{
            check.searchResultClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder{
        TextView userListUserName;
        CircleImageView userProfilePicture;
        LinearLayout inContactHolder;
        EmojiTextView userListAbout;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            userListUserName=itemView.findViewById(R.id.userListUserName);
            userProfilePicture=itemView.findViewById(R.id.userProfilePicture);
            inContactHolder=itemView.findViewById(R.id.inContactHolder);
            userListAbout=itemView.findViewById(R.id.userListAbout);

        }
    }

}
