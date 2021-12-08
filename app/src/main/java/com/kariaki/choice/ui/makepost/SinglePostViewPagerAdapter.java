package com.kariaki.choice.ui.makepost;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.kariaki.choice.ui.Post.Adapter.ViewPagerAdapter;

public class SinglePostViewPagerAdapter extends PagerAdapter {
    String[] imageURI;


    private Context CONTEXT;

    public interface PageOnClickListener{
        void onPageClickListener(int position);
    }
    private ViewPagerAdapter.PageOnClickListener onClickListener;

    public void setOnClickListener(ViewPagerAdapter.PageOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SinglePostViewPagerAdapter(Context context) {
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

        //View view= LayoutInflater.from(CONTEXT).inflate(R.layout.ab_single_page_post_image,container,false);
        ImageView imageView=new ImageView(CONTEXT);
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        imageView.setOnClickListener(v->{
            onClickListener.onPageClickListener(position);
        });

        Glide.with(CONTEXT.getApplicationContext()).load(imageURI[position]).into(imageView);
        container.addView(imageView);

        return imageView;

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
