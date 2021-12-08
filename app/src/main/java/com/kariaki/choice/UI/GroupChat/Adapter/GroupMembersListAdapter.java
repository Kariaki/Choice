package com.kariaki.choice.UI.GroupChat.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.R;
import com.kariaki.choice.UI.GroupChat.GroupMember;
import com.kariaki.choice.UI.util.UserNaming;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembersListAdapter extends RecyclerView.Adapter<GroupMembersListAdapter.innerViewHolder> {

    List<GroupMember> groupChatGroupChatMembers = new ArrayList<>();
    private int option = GroupChatOptions.VIEW_MEMBERS;

    public void setOption(int option) {
        this.option = option;
    }

    private Activity activity;

    public interface groupMemberOptionOnClick{
        void onclickRemoveMember(int position, TextView inviteIcon);
        void onclickInviteMember(int position, TextView removeIcon);
    }
    private groupMemberOptionOnClick onClick;
    private int color=R.color.textHeaderColor;

    public void setColor(int color) {
        this.color = color;
    }

    public void setOnClick(groupMemberOptionOnClick onClick) {
        this.onClick = onClick;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setGroupChatGroupChatMembers(List<GroupMember> groupChatGroupChatMembers) {
        this.groupChatGroupChatMembers = groupChatGroupChatMembers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_member_design, parent, false);

        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        UserNaming naming = UserNaming.getInstance();
        naming.nameThisUser(activity, activity.getApplicationContext(), groupChatGroupChatMembers.get(position).getMemberPhone(),
                holder.memberName, holder.memberProfileImage);

        switch (option) {
            case GroupChatOptions.VIEW_MEMBERS:

                if (!groupChatGroupChatMembers.get(position).isAdmin()) {
                    holder.memberIsAdmin.setVisibility(View.INVISIBLE);
                }else {
                    holder.memberIsAdmin.setVisibility(View.VISIBLE);
                }
                holder.inviteMember.setVisibility(View.INVISIBLE);
                holder.removeMember.setVisibility(View.INVISIBLE);
                break;
            case GroupChatOptions.EDIT_ADMIN:

                holder.memberIsAdmin.setVisibility(View.INVISIBLE);
                holder.removeMember.setVisibility(View.INVISIBLE);
                if(groupChatGroupChatMembers.get(position).isAdmin()){
                    holder.removeMember.setVisibility(View.VISIBLE);

                    holder.inviteMember.setVisibility(View.INVISIBLE);
                }else {
                    holder.removeMember.setVisibility(View.INVISIBLE);

                    holder.inviteMember.setVisibility(View.VISIBLE);
                }

                holder.inviteMember.setOnClickListener(v->{
                    onClick.onclickInviteMember(position,holder.inviteMember);
                    holder.removeMember.setVisibility(View.VISIBLE);
                    holder.inviteMember.setVisibility(View.INVISIBLE);

                });
                holder.removeMember.setOnClickListener(v->{
                    onClick.onclickRemoveMember(position,holder.removeMember);
                    holder.inviteMember.setVisibility(View.VISIBLE);
                    holder.removeMember.setVisibility(View.INVISIBLE);

                });


                break;
            case GroupChatOptions.EDIT_MEMBERS:
                holder.memberIsAdmin.setVisibility(View.INVISIBLE);
                holder.inviteMember.setVisibility(View.INVISIBLE);
                holder.removeMember.setVisibility(View.VISIBLE);

                holder.removeMember.setOnClickListener(v->{
                    onClick.onclickRemoveMember(position,holder.inviteMember);
                });

                break;
        }


    }

    @Override
    public int getItemCount() {
        return groupChatGroupChatMembers.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder {

        TextView memberName, memberIsAdmin,inviteMember,removeMember;
        CircleImageView memberProfileImage;

        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            memberProfileImage = itemView.findViewById(R.id.memberProfilePicture);
            memberName = itemView.findViewById(R.id.memberName);
            memberIsAdmin = itemView.findViewById(R.id.memberIsAdmin);
            inviteMember=itemView.findViewById(R.id.inviteMember);
            removeMember=itemView.findViewById(R.id.removeMember);
            memberName.setTextColor(ContextCompat.getColor(itemView.getContext(),color));

        }
    }
}
