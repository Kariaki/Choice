package com.kariaki.choice.ui.Profiles.SocialActivities.Pages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.Adapter.PostAdapter;
import com.kariaki.choice.ui.Post.PostInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Posts extends Fragment {

    public Posts() {
        // Required empty public constructor
    }

    RecyclerView postList;
    PostAdapter adapter;

    List<Post> list=new ArrayList<>();
    public void setAdapter(PostAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_posts, container, false);
        postList=view.findViewById(R.id.postList);
        postList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setPosts(list);
        postList.setAdapter(adapter);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupListItem();
    }

    DatabaseReference myPostFolder;
    DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference("post");

    private void setupListItem() {

        myPostFolder = FirebaseDatabase.getInstance().getReference("users").child(new UserInformation(getContext()).getMainUserID()).child("post");
        myPostFolder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         postFolder.child(snapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);
                                            dataSnapshot.getRef().child("postData")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            PostInfo info = dataSnapshot.getValue(PostInfo.class);
                                                            Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(),
                                                                    cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(), cloudPost.getPOST_TIME(), info.getPostIsOnRepeat(), "", info.isAllowPrivateComment());
                                                            if (new UserInformation(getContext()).getMainUserID().equals(post.getOwnerID()))
                                                            {
                                                                list.add(post);
                                                                adapter.notifyDataSetChanged();

                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
