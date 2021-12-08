package com.kariaki.choice.UI.Post.Adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.UI.Post.PostTypes;

public abstract class PostMainViewHolder extends RecyclerView.ViewHolder {
    public PostMainViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindPostType(PostTypes types, Context context, int color);

    public abstract void listeners(PostAdapter.OnclickListeners listeners);


}
