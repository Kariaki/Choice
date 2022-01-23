package com.kariaki.choice.ui.mainpage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAndFriendsAdapter extends RecyclerView.Adapter<ContactAndFriendsAdapter.innerViewHolder> {

    List<ChoiceUser> particularUser = new ArrayList<>();
    Context CONTEXT;

    public interface OnClickUser {
        void OnclickUserListener(int position, ImageView marker, CircleImageView profile);

        void OnCheckChangeListener(int position);

    }

    private OnClickUser onClickUser;
    private boolean markAll = false;
    private DatabaseReference myContacts = FirebaseDatabase.getInstance().getReference("users");

    public void setOnClickUser(OnClickUser user) {

        this.onClickUser = user;
    }

    private boolean showMarking = false;

    public void setMarkAll(boolean markAll) {
        this.markAll = markAll;
    }

    public void setShowMarking(boolean showMarking) {
        this.showMarking = showMarking;
    }

    public boolean isShowMarking() {
        return showMarking;
    }

    private int textColor = R.color.textContext;

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private List<String> markedUsers = new ArrayList<>();

    public void setMarkedUsers(List<String> markedUsers) {
        this.markedUsers = markedUsers;
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_design, parent, false);
        CONTEXT = parent.getContext();
        return new innerViewHolder(view);
    }

    public void setParticularUser(List<ChoiceUser> particularUser) {
        this.particularUser = particularUser;
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {

        holder.about.setText(particularUser.get(position).getUserPhoneNumber());
        holder.name.setText(particularUser.get(position).getUserContactName());
        Glide.with(CONTEXT.getApplicationContext()).load(particularUser.get(position).getUserImageUrl())
                .fitCenter().into(holder.profileImage);
        holder.name.setTextColor(CONTEXT.getResources().getColor(textColor));
        onClickUser.OnCheckChangeListener(position);


    }

    @Override
    public int getItemCount() {
        return particularUser.size();
    }

    public class innerViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView name;
        TextView about;
        ImageView marker;

        public innerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.userListProfilePicture);
            name = itemView.findViewById(R.id.userListUserName);
            marker = itemView.findViewById(R.id.marker);
            about = itemView.findViewById(R.id.userListLastMessage);

            itemView.setOnClickListener(v -> {
                onClickUser.OnclickUserListener(getAdapterPosition(), marker, profileImage);
            });

        }
    }

}
