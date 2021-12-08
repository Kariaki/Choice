package com.kariaki.choice.UI.MainPage.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.MakePost.GalleryItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MakePostAdapter extends RecyclerView.Adapter<MakePostAdapter.innerViewHolder> {

    Context CONTEXT;
    List<GalleryItem> imagepath;

    public interface OnclickListener {

        void onClickImage(int position, ImageView MakePostGalleryMark);
    }

    public interface onMarkClickListener {
        void onMarkClick(int position, boolean isChecked);

    }

    public void setShowMarkings(boolean showMarking) {
        this.showMarkings = showMarking;

    }

    public boolean isShowMarkings() {
        return showMarkings;
    }

    OnclickListener mListener;
    onMarkClickListener onMarkClickListener;
    private boolean showMarkings = false;

    public void setOnItemClickListener(OnclickListener listener) {
        mListener = listener;

    }

    public void setOnMarkClickListener(onMarkClickListener listener) {
        onMarkClickListener = listener;
    }

    public MakePostAdapter(Context context, List<GalleryItem> paths) {
        this.imagepath = paths;
        this.CONTEXT = context;

    }

    List<String> markedImages = new ArrayList<>();

    public void setMarkedImages(List<String> markedImages) {
        this.markedImages = markedImages;
    }

    @NonNull
    @Override
    public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(CONTEXT).inflate(R.layout.makepostlayout, parent, false);

        return new innerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {


        //Bitmap bitmap=BitmapFactory.decodeFile(imagepath.get(position));
        //holder.imageView.setImageBitmap(bitmap);


        GalleryItem item = imagepath.get(position);

        if (markedImages.contains(item.getFileURL())) {
            holder.MakePostGalleryMark.setVisibility(View.VISIBLE);
        } else {
            holder.MakePostGalleryMark.setVisibility(View.INVISIBLE);
        }


        switch (imagepath.get(position).getFileType()) {

            case 1:
                holder.isVideoHolder.setVisibility(View.INVISIBLE);

                File file = new File(imagepath.get(position).getFileURL());

                Uri uri = Uri.fromFile(file);
                  Glide.with(CONTEXT).load(uri)
                    .placeholder(R.drawable.image_thumnail).fitCenter().into(holder.imageView);


                break;

            case 2:

                holder.isVideoHolder.setVisibility(View.VISIBLE);

                Glide.with(CONTEXT).load(imagepath.get(position).getFileURL()).placeholder(R.drawable.image_thumnail).into(holder
                        .imageView);
                break;

        }


    }

    @Override
    public int getItemCount() {
        return imagepath.size();
    }

    class innerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView isVideoHolder, MakePostGalleryMark;

        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.makepostImage);

            isVideoHolder = itemView.findViewById(R.id.isVideoHolder);
            MakePostGalleryMark = itemView.findViewById(R.id.MakePostGalleryMark);

//            mListener.onClickImage(getAdapterPosition(),MakePostGalleryMark);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onClickImage(getAdapterPosition(), MakePostGalleryMark);

                }
            });


        }
    }


}
