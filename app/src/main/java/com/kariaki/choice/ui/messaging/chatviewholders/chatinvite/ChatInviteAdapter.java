package com.kariaki.choice.ui.Messaging.ChatViewHolders.ChatInvite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.UserNaming;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatInviteAdapter extends RecyclerView.Adapter<ChatInviteAdapter.innerViewHolder> {

    List<String> invitedUsers = new ArrayList<>();
    Context CONTEXT;

    public interface OnclickInviteChatListener {
        void onClickDisengage(int position);

        void onClickProfileImage(int position);
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_chat_design, parent, false);

        CONTEXT = parent.getContext().getApplicationContext();
        return new ChatInviteAdapter.innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        UserNaming naming =  UserNaming.getInstance();
        naming.nameThisUser(invitedUsers.get(position), CONTEXT, holder.inviteChatUserName, holder.inviteChatUserProfilePicture);

    }

    @Override
    public int getItemCount() {
        return invitedUsers.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView inviteChatUserProfilePicture;
        public TextView inviteChatUserName;
        public ImageButton disEngageInviteduser;


        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            disEngageInviteduser = itemView.findViewById(R.id.removeInviteChatUser);
            inviteChatUserProfilePicture = itemView.findViewById(R.id.inviteChatUserProfilePicture);
            inviteChatUserName = itemView.findViewById(R.id.inviteChatUserName);
        }
    }
}
