package com.kariaki.choice.ui.MainPage.BottomFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.MainPage.Adapters.MakePostAdapter;
import com.kariaki.choice.ui.MakePost.GalleryItem;
import com.kariaki.choice.ui.MakePost.TextPost;
import com.kariaki.choice.ui.Settings.PostSettings;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BottomMakePost extends Fragment {


    private List<GalleryItem> images = new ArrayList<>();
    private String path;
    RecyclerView bottomGallery;
    ImageView makePostOpenCamera,makePostOpenText;
    MakePostAdapter adapter;
    TextView sendMarkedImages;
    TextView markedImagesCounter;
    int galleryScrollPosition=-1;
    RelativeLayout changeHolder;
    RelativeLayout curveHolder;
    ImageView makePostPostSettings;
    Button sentImages;
    TextView writeTextText,cameraText,postSettingsText;
    boolean showsentImages;

    public void setShowsentImages(boolean showsentImages) {
        this.showsentImages = showsentImages;
    }

    private List<String>markedImages=new ArrayList<>();

    public void setMarkedImages(List<String> markedImages) {
        this.markedImages = markedImages;
    }

    public static BottomMakePost getInstance(){


        return new BottomMakePost();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public interface onClickDone {

        void onClickDone();

    }

    private boolean showMarkings=false;
    private boolean showDone=false;

    public void setShowMarking(List<String>marked){
        if(marked.isEmpty()){
            sentImages.setVisibility(View.INVISIBLE);
        }else {
            sentImages.setVisibility(View.VISIBLE);
        }
    }

    public void setShowDone(boolean showDone) {
        this.showDone = showDone;
    }

    public boolean isShowDone() {
        return showDone;
    }

    public void setShowMarkings(boolean showMarkings) {
        this.showMarkings = showMarkings;
    }

    public void setAdapter(MakePostAdapter adapter) {
        this.adapter = adapter;
    }

    private onClickDone onClickDone;

    public void setGalleryScrollPosition(int galleryScrollPosition) {
        this.galleryScrollPosition = galleryScrollPosition;
    }

    public int getGalleryScrollPosition() {
        return galleryScrollPosition;
    }

    public void setOnClickDone(onClickDone onClickDone) {
        this.onClickDone = onClickDone;
    }

    private boolean showCount=false;
    public void setShowCount(boolean count){
        showCount=count;
    }

    public boolean isShowCount() {
        return showCount;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View contentView=View.inflate(getContext(), R.layout.activity_make_post,null);

        makePostOpenText=contentView.findViewById(R.id.makePostOpenText);
        sendMarkedImages=contentView.findViewById(R.id.sendMarkedImages);
        markedImagesCounter=contentView.findViewById(R.id.markedImagesCounter);
        sentImages=contentView.findViewById(R.id.sentImages);
        postSettingsText=contentView.findViewById(R.id.postSettingsText);
        cameraText=contentView.findViewById(R.id.cameraText);
        writeTextText=contentView.findViewById(R.id.writeTextText);
        changeHolder=contentView.findViewById(R.id.changeHolder);
        makePostPostSettings=contentView.findViewById(R.id.makePostPostSettings);
        makePostOpenCamera=contentView.findViewById(R.id.makePostOpenCamera);
        curveHolder=contentView.findViewById(R.id.curveHolder);

        bottomGallery=contentView.findViewById(R.id.MakePostGallery);

        // curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_curve_dark_mode));

           loadTheme();


        makePostPostSettings.setOnClickListener(v->{
            //startActivity(new Intent(getActivity(), PostSettingsActivity.class));
        });


        if(markedImages.size()>0){

            markedImagesCounter.setVisibility(View.VISIBLE);
            markedImagesCounter.setText("Selected items "+markedImages.size());
        }

        if(showDone){

            sendMarkedImages.setVisibility(View.VISIBLE);
            changeHolder.setVisibility(View.GONE);

        }else {
            sendMarkedImages.setVisibility(View.INVISIBLE);

        }



        makePostOpenText.setOnClickListener(v->{
            startActivity(new Intent(getContext(), TextPost.class));
            //  dismiss();
        });

        makePostOpenCamera.setOnClickListener(v->{
            // startActivity(new Intent(getContext(), ChoiceCamera.class));
        });

        makePostPostSettings.setOnClickListener(v->{
            Intent intent=new Intent(getActivity(), PostSettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        bottomGallery.setLayoutManager(new GridLayoutManager(getContext(),3));
        bottomGallery.setHasFixedSize(true);
        bottomGallery.setAdapter(adapter);

        sendMarkedImages.setOnClickListener(v->{
            onClickDone.onClickDone();
        });

        sentImages.setOnClickListener(v->{
            onClickDone.onClickDone();
        });




        return contentView;

    }

    String selectedItem="selected items ";
    @Override
    public void onStart() {
        super.onStart();

        if(showCount) {
            markedImagesCounter.setVisibility(View.VISIBLE);
            markedImagesCounter.setText(selectedItem + String.valueOf(markedImages.size()));
        }
    }

    public void setImages(List<GalleryItem> images) {
       // this.images = images;
//        Gallery gallery=new Gallery(getActivity());
        this.images= images;


    }



    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);

                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_curve_dark_mode));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView []textViews={markedImagesCounter,writeTextText,postSettingsText,cameraText};
        setTextColors(textViews,currentTheme);

    }
    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }

    BottomSheetBehavior.BottomSheetCallback callback=new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


}
