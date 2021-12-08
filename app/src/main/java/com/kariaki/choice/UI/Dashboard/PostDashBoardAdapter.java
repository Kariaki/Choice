package com.kariaki.choice.UI.Dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kariaki.choice.Model.CloudPost;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Post.PostTypes;

import java.util.ArrayList;
import java.util.List;

public class PostDashBoardAdapter extends RecyclerView.Adapter<DashBoardMainViewHolder> {

    private List<CloudPost>dashBoardPost=new ArrayList<>();

    public void setDashBoardPost(List<CloudPost> dashBoardPost) {
        this.dashBoardPost = dashBoardPost;
        notifyDataSetChanged();
    }

    public PostDashBoardAdapter(){

    }


    private int color=R.color.textHeaderColor;

    public void setColor(int color) {
        this.color = color;
    }

    public interface OnClickDashBoardListener{

        void onClickDelete(int position);
        void onClickPostDashItem(int position);
        void onClickComment(int position);
        void postStatistics(int position);
        void notificationDashBoard(int position, RelativeLayout holder, TextView counter);
    }
    private OnClickDashBoardListener dashBoardListener;

    public void setOnclickDashBoardListener(OnClickDashBoardListener listener){
        dashBoardListener=listener;
    }

    @NonNull
    @Override
    public DashBoardMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case PostTypes.SINGLE_POST:
            case PostTypes.VIDEO_POST:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_dash_board_single_image,parent,false);
                return new DashBoardSingleImageViewHolder(view);
            case PostTypes.TEXT:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_dash_board_text_post,parent,false);
                return new DashBoardTextPostViewHolder(view);
                case PostTypes.MERGED_POST:
                    view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_dash_board_merge_post,parent,false);

                    return new DashBoardMergePostViewHolder(view);


            default:
                return null;

        }
            }

    @Override
    public void onBindViewHolder(@NonNull DashBoardMainViewHolder holder, int position) {
        holder.onBind(dashBoardPost.get(position),color);
        holder.listeners(dashBoardListener);


    }

    @Override
    public int getItemCount() {
        return dashBoardPost.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dashBoardPost.get(position).getPostType();
    }
}
