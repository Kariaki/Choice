package com.kariaki.choice.ui.MainPage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.kariaki.choice.model.database.entities.MyChatChannel;
import com.kariaki.choice.R;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListMainViewHolder> {

    List<MyChatChannel> channelIDs = new ArrayList<>();

    public interface OnclickChatPersonListener {
        void OnclickChatItem(int position, View view,TextView name,CircleImageView imageView);

        void nameChoiceUser(int position, CircleImageView profilePicture, TextView name);

        void nameGroupChat(int position, CircleImageView profilePicture, TextView name);

        void getLastMessage(CircleImageView image,int position, ImageView messageState,
                            ImageView isRecordingMic, EmojiTextView last_message, TextView lastMessageTime,
                            TashieLoader isTypingLoader,CircleImageView userMessagePicture,ImageView indicator);

        void chatActivity(int position, TashieLoader loader, ImageView mic, ImageView messageState, EmojiTextView last_message);

        void notification(int position,
                          RelativeLayout chatListNotificationCountHolder, TextView chatListNotificationCount,RelativeLayout rootView);
        void order(int position);

    }

    int color = R.color.whiteGreen;

    public void setColor(int color) {
        this.color = color;
    }

    public ChatListAdapter() {

    }

    private OnclickChatPersonListener listener;

    private FragmentActivity application;

    public void setUsers(List<MyChatChannel> users) {
        this.channelIDs = users;
        notifyDataSetChanged();
    }

    public void setApplication(FragmentActivity application) {
        this.application = application;
    }

    String mainUserID;

    public void setOnclickListener(OnclickChatPersonListener onclickChatPersonListener) {
        listener = onclickChatPersonListener;
    }

    private Context CONTEXT;

    @NonNull
    @Override
    public ChatListMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chated_user_design, parent, false);

        CONTEXT = parent.getContext();

        return new ChatListGroupChatViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatListMainViewHolder holder, int position) {
        holder.bind(channelIDs.get(position), application, color);
        holder.listeners(listener,channelIDs.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return channelIDs.get(position).getChannelType();
    }

    @Override
    public int getItemCount() {
        return channelIDs.size();
    }


}

