package com.kariaki.choice.ui.mainpage.pages.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.BounceLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.comment.CommentPage;
import com.kariaki.choice.ui.dashboard.PostDashBoardAdapter;
import com.kariaki.choice.ui.dialogbox.ChoiceDialogBox;
import com.kariaki.choice.ui.dialogbox.ChoiceNewDialogBox;
import com.kariaki.choice.ui.mainpage.adapters.MakePostAdapter;
import com.kariaki.choice.ui.mainpage.bottomfragments.BottomMakePost;
import com.kariaki.choice.ui.mainpage.util.MainFunctions;
import com.kariaki.choice.ui.makepost.GalleryItem;
import com.kariaki.choice.ui.makepost.ImagePost;
import com.kariaki.choice.ui.makepost.VideoPost;
import com.kariaki.choice.ui.post.PostInfo;
import com.kariaki.choice.ui.post.poststatistics.PostStatistics;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Gallery;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyContent extends Fragment {

    public MyContent() {
        // Required empty public constructor
    }

    public interface onClickListener{
        void clickListener();
    }
    public onClickListener listener;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    RecyclerView myPostDashBoard;
    DatabaseReference myPost = FirebaseDatabase.getInstance().getReference("post");
    DatabaseReference liveUsers = FirebaseDatabase.getInstance().getReference("users");
    List<CloudPost> cloudPosts = new ArrayList<>();
    PostDashBoardAdapter adapter;
    UserInformation information;
    CoordinatorLayout mynotificationRoot;
    ChoiceViewModel viewModel;
    //RelativeLayout dialogBackground;
    ChoiceNewDialogBox dialogBox;
    LinearLayout infoHolder;
    PostStatistics statistics;
    LinearLayout newContentHolder;
    BounceLoader bounceLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_notification, container, false);
        myPostDashBoard = view.findViewById(R.id.myPostDashBoard);
        mynotificationRoot = view.findViewById(R.id.mynotificationRoot);
        newContentHolder = view.findViewById(R.id.newContentHolder);
        infoHolder = view.findViewById(R.id.infoHolder);
        bounceLoader = view.findViewById(R.id.bounceLoader);
        viewModel = viewModel.getInstance(getActivity().getApplication());
        // dialogBackground = view.findViewById(R.id.dialogBackground);


        adapter = new PostDashBoardAdapter();
        information = new UserInformation(Objects.requireNonNull(getContext()));
        dialogBox = new ChoiceNewDialogBox();

        GridLayoutManager manager =
                new GridLayoutManager(getContext(), 2);


        //adapter.setDashBoardPost(cloudPosts);
        viewModel.getAllMyPost()
                .observe(getActivity(), new Observer<List<CloudPost>>() {
                    @Override
                    public void onChanged(List<CloudPost> local_cloudPosts) {

                        cloudPosts = local_cloudPosts;
                        adapter.setDashBoardPost(local_cloudPosts);
                        if (local_cloudPosts.size() == 0) {

                            infoHolder.setVisibility(View.VISIBLE);
                        } else {
                            infoHolder.setVisibility(View.INVISIBLE);
                        }
                        if (adapter.getItemCount() != 0) {
                            bounceLoader.setVisibility(View.INVISIBLE);
                        }

                    }
                });
        myPostDashBoard.setLayoutManager(manager);
        myPostDashBoard.setHasFixedSize(true);

        loadTheme();
        adapter.setOnclickDashBoardListener(new PostDashBoardAdapter.OnClickDashBoardListener() {
            @Override
            public void postStatistics(int position) {
                statistics = new PostStatistics();
                statistics.setCloudPost((CloudPost) cloudPosts.get(position));
                statistics.show(getFragmentManager(), "post statistics");

            }

            @Override
            public void onClickDelete(int position) {
                CloudPost thispost = (CloudPost) cloudPosts.get(position);
                String postID = thispost.getPostID();
                String postURL = thispost.getPOST_URL();

                DatabaseReference myPostPost = liveUsers.child(information.getMainUserID()).child("post").child(thispost.getPostID());

                dialogBox.setTittle("Delete post?");
                dialogBox.setMessage(getString(R.string.delete_post));
                dialogBox.setListeners(new ChoiceDialogBox.dialogButtons() {
                    @Override
                    public void onClickPositiveButton() {
                        cloudPosts.remove(position);
                        myPostPost.removeValue();
                        myPost.child(postID).removeValue();

                        viewModel.deleteMyPost(thispost);
                        if (!postURL.isEmpty()) {
                            Functions.deleteFile(postURL);
                        }
                        dialogBox.dismiss();

                        if (adapter.getItemCount() == 0) {

                            infoHolder.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onClickNegativeButton() {
                        dialogBox.dismiss();
                    }
                });

                dialogBox.show(getFragmentManager(), "oops!!!");


            }

            @Override
            public void onClickPostDashItem(int position) {
                Intent intent = new Intent(getActivity(), CommentPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                CloudPost post = (CloudPost) cloudPosts.get(position);
                Post dpost = new Post(post.getPostID(), post.getOwnerID(), post.getPOST_CAPTION(), post.getPOST_URL()
                        , post.getPOST_TYPE(), post.getPOST_TIME(), false, post.getOwnerID(), false);
                intent.putExtra("postData", dpost);
                myPost.child(post.getPostID()).child("notification")
                        .removeValue();
                MainFunctions.updateNotification(new UserInformation(getContext()).getMainUserID(), post.getPostID());

                DatabaseReference badgeCount = liveUsers.child(new UserInformation(getActivity()).getMainUserID()).child("badge").child(dpost.getPostID());
                badgeCount.removeValue();


                startActivity(intent);


            }

            @Override
            public void onClickComment(int position) {

            }

            @Override
            public void notificationDashBoard(int position, RelativeLayout holder, TextView counter) {
                CloudPost cloudPost = (CloudPost) cloudPosts.get(position);
                myPost.child(cloudPost.getPostID()).child("notification")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    holder.setVisibility(View.VISIBLE);
                                    counter.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                } else {
                                    holder.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        });

        myPostDashBoard.setAdapter(adapter);

        newContentHolder.setOnClickListener(v -> {

            //openGallery();
           // YoYo.with(TechniquesB).duration(200).playOn(newContentHolder);
            listener.clickListener();
            //Toast.makeText(getContext(), "yeyeye", Toast.LENGTH_SHORT).show();
        });
        newContentHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        return view;
    }

    List<GalleryItem> images = new ArrayList<>();
    MakePostAdapter makePostAdapter;
    ArrayList<String> markedImages = new ArrayList<>();

    private void openGallery() {

        BottomMakePost bottomMakePost = new BottomMakePost();
        bottomMakePost.setShowMarkings(false);


        Gallery gallery = new Gallery(getActivity());

        images = gallery.getImages();
        bottomMakePost.setImages(images);
        makePostAdapter = new MakePostAdapter(getActivity(), images);
        makePostAdapter.setMarkedImages(markedImages);
        makePostAdapter.setOnItemClickListener(makePostItemClickLister);
        makePostAdapter.setShowMarkings(false);
        bottomMakePost.setAdapter(makePostAdapter);
        //   bottomMakePost.show(getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        
        transaction.replace(R.id.mynotificationRoot, bottomMakePost).addToBackStack(null).commit();

    }

    Intent sendingIntent;
    MakePostAdapter.OnclickListener makePostItemClickLister = new MakePostAdapter.OnclickListener() {

        @Override
        public void onClickImage(int position, ImageView marker) {

            switch (images.get(position).getFileType()) {

                case 1:

                    sendingIntent = new Intent(getActivity(), ImagePost.class);
                    markedImages.add(images.get(position).getFileURL());

                    sendingIntent.putStringArrayListExtra("yourImages", markedImages);
                    sendingIntent.putExtra("scrollLocation", position);
                    startActivity(sendingIntent);
                    markedImages.clear();

                    break;
                case 2:

                    Intent videoIntent = new Intent(getActivity(), VideoPost.class);
                    videoIntent.putExtra("videoURL", images.get(position).getFileURL());
                    startActivity(videoIntent);

                    break;
            }


        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadTheme();
        // myPostDashBoard.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        myPostDashBoard.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {

                DatabaseReference myDashBoard = liveUsers.child(information.getMainUserID()).child("post");

                myDashBoard.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                retrievMypost(snapshot.getRef());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();
    }

    List<String> postIDs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        viewModel = ChoiceViewModel.getInstance(getActivity().getApplication());

    }

    //List<String> hasMyPostNotification = InAppNotification.getHasMyPostNotification();

    private void retrievMypost(DatabaseReference key) {


        myPost.child(key.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    CloudPost post = dataSnapshot.getValue(CloudPost.class);
                    assert post != null;

                    DatabaseReference finalPasspost = dataSnapshot.getRef().child("postData");
                    finalPasspost.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            if (dataSnapshot1.exists()) {
                                PostInfo postInfo = dataSnapshot1.getValue(PostInfo.class);
                                post.setPOST_LIFE_SPAN(postInfo.getPostLifeSpan());
                                viewModel.insertMyPost(post);
                                assert postInfo != null;
                                if (TimeFactor.lifeSpanTimer(postInfo.getPostLifeSpan())) {
                                    String url = post.getPOST_URL();
                                    Functions.deleteFile(url);
                                    dataSnapshot.getRef().removeValue();

                                    key.removeValue();
                                    viewModel.deleteMyPost(post);

                                } else {


                                  /*  if (post.getOwnerID().equals(information.getMainUserID()) && !postIDs.contains(post.getPostID())) {
                                        cloudPosts.add(0, post);
                                        postIDs.add(0, post.getPostID());
                                        adapter.notifyDataSetChanged();
                                    }

                                   */
                                }
                            } else {
                                key.removeValue();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });

                } else {
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void setTextColors(int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                adapter.setColor(R.color.textColor);

                break;
            case Theme.DEEP:
                adapter.setColor(R.color.whiteGreen);
                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);
        adapter.notifyDataSetChanged();


    }
}
