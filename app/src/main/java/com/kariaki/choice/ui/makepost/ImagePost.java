package com.kariaki.choice.ui.makepost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kariaki.choice.R;
import com.kariaki.choice.ui.mainpage.adapters.MakePostAdapter;
import com.kariaki.choice.ui.mainpage.bottomfragments.BottomMakePost;
import com.kariaki.choice.ui.Post.Adapter.AddImageAdapter;
import com.kariaki.choice.ui.Post.Adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagePost extends AppCompatActivity {


    private RecyclerView addedImages;
    private List<String> paths = new ArrayList<>();
    private ViewPager postImageView;
    private ImageButton cropButton;
    private LinearLayout.LayoutParams newParam;

    private List<GalleryItem> images = new ArrayList<>();
    private ImageButton addPostImage;
    ArrayList<String> pathToImage = new ArrayList<>();
    private int galleryScrollPosition = 0;
    private ViewPagerAdapter viewPagerAdapter;
    private final int markImageRequestCode = 1212;
    MakePostAdapter makePostAdapter;

    private AddImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialization();

        getAllImages();


        Intent intent = getIntent();
        pathToImage = intent.getStringArrayListExtra("yourImages");
        galleryScrollPosition = intent.getIntExtra("scrollLocation", 0);

        String[] allImagesInArray = new String[pathToImage.size()];
        allImagesInArray = pathToImage.toArray(allImagesInArray);


        viewPagerAdapter.setImageURI(allImagesInArray);
        viewPagerAdapter.notifyDataSetChanged();
        postImageView.setAdapter(viewPagerAdapter);

        viewPagerAdapter.setOnClickListener(new ViewPagerAdapter.PageOnClickListener() {
            @Override
            public void onPageClickListener(int position) {

            }
        });
        newParam = new LinearLayout.LayoutParams(0, 0);

        makePostAdapter=new MakePostAdapter(this,images);
        bottomMakePost = BottomMakePost.getInstance();

        makePostAdapter.setShowMarkings(true);
        makePostAdapter.setMarkedImages(pathToImage);

        makePostAdapter.setOnItemClickListener(new MakePostAdapter.OnclickListener() {

            @Override
            public void onClickImage(int position, ImageView toggleButton) {
                GalleryItem galleryItem = images.get(position);


                if (pathToImage.contains(galleryItem.getFileURL())) {


                    pathToImage.remove(galleryItem.getFileURL());
                    adapter.notifyDataSetChanged();

                    toggleButton.setVisibility(View.INVISIBLE);
                    updateViewPager(pathToImage);

                } else {

                    if(pathToImage.size()<4) {
                        pathToImage.add(galleryItem.getFileURL());
                        adapter.notifyDataSetChanged();

                        toggleButton.setVisibility(View.VISIBLE);
                        updateViewPager(pathToImage);
                    }else {
                        Toast.makeText(ImagePost.this, "Limit has been reached", Toast.LENGTH_SHORT).show();
                    }
                }

                makePostAdapter.setMarkedImages(pathToImage);
            }

        });

        if(pathToImage.size()<1){
            onBackPressed();
        }

        makePostAdapter.setOnMarkClickListener(new MakePostAdapter.onMarkClickListener() {
            @Override
            public void onMarkClick(int position, boolean isChecked) {
                GalleryItem galleryItem=images.get(position);
                if(isChecked){
                    GalleryItem newGalleryItem=new GalleryItem(galleryItem.getFileURL(),galleryItem.getBitmap(),galleryItem
                            .getFileType(),false);
                    images.set(position,newGalleryItem);
                    pathToImage.remove(galleryItem.getFileURL());
                    updateViewPager(pathToImage);

                }else {

                    GalleryItem newGalleryItem=new GalleryItem(galleryItem.getFileURL(),galleryItem.getBitmap(),galleryItem
                            .getFileType(),true);
                    images.set(position,newGalleryItem);
                    pathToImage.add(galleryItem.getFileURL());
                    updateViewPager(pathToImage);


                }
            }

        });

        bottomMakePost.setAdapter(makePostAdapter);

        bottomMakePost.setShowDone(true);
        bottomMakePost.setMarkedImages(pathToImage);
        bottomMakePost.setShowCount(true);
        bottomMakePost.setOnClickDone(new BottomMakePost.onClickDone() {

            @Override
            public void onClickDone() {

            }
        });

        //added images
        addedImages = findViewById(R.id.addedImageCollection);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.canScrollHorizontally();
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        addedImages.setLayoutManager(layoutManager);
        adapter.setFilterImages(pathToImage);
        addedImages.setHasFixedSize(true);
        addedImages.setAdapter(adapter);
        adapterItemClick();


    }

    BottomMakePost bottomMakePost;
    List<String> markedImages = new ArrayList<>();

    public void updateViewPager(List<String>imageList){
        String[] allImagesInArray = new String[imageList.size()];
        allImagesInArray = imageList.toArray(allImagesInArray);


        viewPagerAdapter.setImageURI(allImagesInArray);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void backPress(View view) {
        finish();
    }

    private void initialization() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        postImageView = findViewById(R.id.PostImageViewPager);
        addPostImage = findViewById(R.id.addPostImage);
        cropButton = findViewById(R.id.makePostCropButton);

        //adapters

        adapter = new AddImageAdapter();

    }


    private void adapterItemClick() {

        addPostImage.setOnClickListener(v->{

            int position=galleryScrollPosition;
            GalleryItem oldGalleryItem=images.get(position);
            GalleryItem newGalleryItem=new GalleryItem(oldGalleryItem.getFileURL(),oldGalleryItem.getBitmap(),oldGalleryItem.getFileType(),
                    true);
            images.set(position,newGalleryItem);
           // bottomMakePost.show(getSupportFragmentManager(),"Bottom makePost Gallery");
            FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.imagePostRoot,bottomMakePost).addToBackStack(null).commit();

        });

        adapter.setOnClickItemListener(new AddImageAdapter.OnClickItemListener() {
            @Override
            public void OnclickItem(int position) {

                postImageView.setCurrentItem(position);

            }
        });



    }


    String path = "";

    private void getAllImages() {

        Uri uri;
        Cursor cursor;
        int column_index_data, columnFolderName;
        uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MIME_TYPE,
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


        cursor = managedQuery(uri, projection, selection, null, MediaStore.MediaColumns.DATE_ADDED);


        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        int type = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inSampleSize = 4;
        bitmapOption.inPurgeable = true;

        cursor.moveToLast();

        int i = 0;
        while (cursor.moveToPrevious()) {
            i++;

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));

            if (i == cursor.getCount()) {
                break;
            }
            path = cursor.getString(column_index_data);

            GalleryItem galleryItem = new GalleryItem(path, null, cursor.getInt(type),false);

            images.add(galleryItem);


        }
    }


    public void finishPost(View view) {

        Intent finishPostIntent = new Intent(this, FinishPost.class);
        String[] sendingImages = new String[paths.size()];
        sendingImages = pathToImage.toArray(sendingImages);

        finishPostIntent.putExtra("pathToImages", sendingImages);

        startActivity(finishPostIntent);

    }

}
