package com.kariaki.choice.ui.post.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kariaki.choice.R;

public class ViewPagerAdapter extends PagerAdapter {
    String[] imageURI;


    private Context CONTEXT;

    public interface PageOnClickListener {
        void onPageClickListener(int position);
    }

    private PageOnClickListener onClickListener;

    public void setOnClickListener(PageOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ViewPagerAdapter(Context context) {
        CONTEXT = context;
        //this.imageURI=imageURI;
    }

    public void setImageURI(String[] imageURI) {
        this.imageURI = imageURI;
        notifyDataSetChanged();


    }

    @Override
    public int getCount() {
        // return imageURI.length;
        return imageURI.length;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(CONTEXT).inflate(R.layout.ab_single_page_post_image, container, false);
        SubsamplingScaleImageView imageView = view.findViewById(R.id.singlePostImage);

        Glide.with(CONTEXT.getApplicationContext()).load(imageURI[position]).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                BitmapDrawable drawable = (BitmapDrawable) resource;
                imageView.setImage(ImageSource.bitmap(drawable.getBitmap()));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        container.addView(view);

        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals((View) object);

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
