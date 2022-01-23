package com.kariaki.choice.ui.post.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kariaki.choice.R;

import java.util.ArrayList;
import java.util.List;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.innerViewholder>{


    List<String>filterImages=new ArrayList<>();

    public interface OnClickItemListener{
        void OnclickItem(int position);
    }
    public OnClickItemListener onClickItemListener;

    public void setOnClickItemListener(OnClickItemListener listener){
        onClickItemListener=listener;
    }

    private int mPosition;
    public void setFilterImages(List<String> filterImages) {
        this.filterImages = filterImages;
    }

    @NonNull
    @Override
    public innerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_add_image,parent,false);

        return new innerViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewholder holder, int position) {

        Uri uri=Uri.parse(filterImages.get(position));
        holder.imageFilter.setImageURI(uri);
        mPosition=position;


    }

    public int getPosition(){
        return mPosition;
    }
    @Override
    public int getItemCount() {
        return filterImages.size();
    }

    public class innerViewholder extends RecyclerView.ViewHolder{


        public ImageView imageFilter;
        public innerViewholder(@NonNull View itemView) {
            super(itemView);
            imageFilter=itemView.findViewById(R.id.filterPostImage);
            imageFilter.setOnClickListener(v->{
                onClickItemListener.OnclickItem(getAdapterPosition());
            });
        }
    }
}
