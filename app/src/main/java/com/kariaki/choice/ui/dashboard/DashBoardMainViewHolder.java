package com.kariaki.choice.ui.Dashboard;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.ui.Post.PostTypes;

public abstract class DashBoardMainViewHolder extends RecyclerView.ViewHolder {
    public DashBoardMainViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void onBind(PostTypes post, int color);
    abstract void listeners(PostDashBoardAdapter.OnClickDashBoardListener listener);
}
