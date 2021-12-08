package com.kariaki.choice.ui.Profiles.SocialActivities.Pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kariaki.choice.model.Post;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.Adapter.PostAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Reposts extends Fragment {

    public Reposts() {
        // Required empty public constructor
    }

    RecyclerView postList;
    PostAdapter adapter;

    public void setAdapter(PostAdapter adapter) {
        this.adapter = adapter;
    }
    List<Post>list=new ArrayList<>();

    public void setPostList(List<Post> postList) {
        this.list=postList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reposts, container, false);
        postList=view.findViewById(R.id.postList);
        postList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setPosts(list);
        postList.setAdapter(adapter);
        return view;
    }
}
