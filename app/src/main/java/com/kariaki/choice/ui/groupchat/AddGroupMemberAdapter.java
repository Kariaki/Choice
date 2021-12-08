package com.kariaki.choice.ui.GroupChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.innerViewHolder> {

    List<ChoiceUser>choiceUsers=new ArrayList<>();

    public void setChoiceUsers(List<ChoiceUser> choiceUsers) {
        this.choiceUsers = choiceUsers;
    }
    public interface addClickListener{
        void addClick(int position, TextView textView);
    }
    private addClickListener onAddClickListener;

    public void setOnAddClickListener(addClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    Context context;
    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View design= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_member_design,parent,false);
        this.context=parent.getContext();
        return new innerViewHolder(design);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        holder.inviteMember.setOnClickListener(v->{
            onAddClickListener.addClick(position,holder.inviteMember);
            holder.added.setVisibility(View.VISIBLE);

        });
        Glide.with(context).load(choiceUsers.get(position).getUserImageUrl()).into(holder.memberProfilePicture);
        holder.memberName.setText(choiceUsers.get(position).getUserContactName());
        holder.about.setText(choiceUsers.get(position).getUserAboutMe());

    }

    @Override
    public int getItemCount() {
        return choiceUsers.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder{
        CircleImageView memberProfilePicture;
        TextView memberName,inviteMember,added,about;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            memberProfilePicture=itemView.findViewById(R.id.memberProfilePicture);
            memberName=itemView.findViewById(R.id.memberName);
            added=itemView.findViewById(R.id.added);
            about=itemView.findViewById(R.id.about);
            inviteMember=itemView.findViewById(R.id.inviteMember);
        }
    }

}
