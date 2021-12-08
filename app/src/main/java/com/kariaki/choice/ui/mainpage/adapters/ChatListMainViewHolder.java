package com.kariaki.choice.ui.MainPage.Adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.model.database.entities.MyChatChannel;


abstract class ChatListMainViewHolder extends RecyclerView.ViewHolder {
    public ChatListMainViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void bind(MyChatChannel channelID, FragmentActivity application, int color);
    abstract void listeners(ChatListAdapter.OnclickChatPersonListener listener, MyChatChannel order);

}
