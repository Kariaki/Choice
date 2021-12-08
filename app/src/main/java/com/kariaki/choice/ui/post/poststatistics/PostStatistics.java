package com.kariaki.choice.ui.Post.PostStatistics;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Post.PostTypes;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostStatistics extends BottomSheetDialogFragment {

    LinearLayout curveHolder;
    private CloudPost cloudPost;
    ChoiceViewModel viewModel;
    RecyclerView statsList;
    PostStatsAdapter adapter;
    TextView responseText;
    TextView response;

    LinearLayout responseHolder;
    List<UserDetail> userDetailList = new ArrayList<>();

    public void setCloudPost(CloudPost cloudPost) {
        this.cloudPost = cloudPost;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.post_statistics, null, false);
        curveHolder = view.findViewById(R.id.curveHolder);
        dialog.setContentView(view);
        statsList = view.findViewById(R.id.statsList);
        responseHolder=view.findViewById(R.id.responseHolder);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        loadTheme();
        viewModel = ChoiceViewModel.getInstance(getActivity().getApplication());
        adapter = new PostStatsAdapter();
        adapter.setUserDetailList(userDetailList);
        response = view.findViewById(R.id.response);
        responseText = view.findViewById(R.id.responseText);
        adapter.setActionListeners(new PostStatsAdapter.actionListeners() {

            @Override
            public void onLikeAction(int position, ImageView icon) {
                UserDetail detail = userDetailList.get(position);
                DatabaseReference post = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());
                DatabaseReference likes = post.child("likes").child(detail.getPhone());
                //  DatabaseReference comments=post.child("comments");
                DatabaseReference like_one = post.child("like_one").child(detail.getPhone());
                DatabaseReference like_two = post.child("like_two").child(detail.getPhone());
                stats(likes, icon);
                stats(like_one, icon);
                stats(like_two, icon);

            }

            @Override
            public void onViewAction(int position, ImageView icon) {
                UserDetail detail = userDetailList.get(position);
                DatabaseReference post = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());

                //  DatabaseReference comments=post.child("comments");
                DatabaseReference views = post.child("views").child(detail.getPhone());

                stats(views, icon);

            }
        });

        statsList.setLayoutManager(new LinearLayoutManager(getContext()));
        statsList.setAdapter(adapter);

        statistics(cloudPost);

        if (userDetailList.isEmpty()) {
            responseText.setVisibility(View.VISIBLE);
        } else {
            responseText.setVisibility(View.GONE);
        }

    }


    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_curve_dark_mode));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        setTextColors(null, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        // adapter.setTextColor(color);
        if (textViews != null)
            for (TextView text : textViews) {
                text.setTextColor(getResources().getColor(color));
            }

    }

    private void statistics(CloudPost cloudPost) {
        int postType = cloudPost.getPostType();
        String postID = cloudPost.getPostID();
        stats(postID, postType);
    }

    private void stats(String id, int type) {
        DatabaseReference post = FirebaseDatabase.getInstance().getReference("post").child(id);
        DatabaseReference likes = post.child("likes");
        //  DatabaseReference comments=post.child("comments");
        DatabaseReference views = post.child("views");
        DatabaseReference like_one = post.child("like_one");
        DatabaseReference like_two = post.child("like_two");

        // collectStats(comments);
        collectStats(views);
        if (type != PostTypes.MERGED_POST) {

            collectStats(likes);
        } else {
            collectStats(like_one);
            collectStats(like_two);
        }

        if (!userDetailList.isEmpty()) {
            String text = String.valueOf(userDetailList.size()) + "\t" + "response";
            response.setText(text);
        }

    }

    List<String> userID = new ArrayList<>();

    private void collectStats(DatabaseReference folder) {
        folder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        if (!userID.contains(user.getKey())) {
                            userID.add(user.getKey());
                            nameUser(user.getKey());
                            responseText.setVisibility(View.GONE);
                            responseHolder.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference userList = FirebaseDatabase.getInstance().getReference("users");

    private void nameUser(String userID) {
        viewModel.getChoiceUser(userID)
                .observe(getActivity(), new Observer<List<ChoiceUser>>() {
                    @Override
                    public void onChanged(List<ChoiceUser> choiceUsers) {
                        if (!choiceUsers.isEmpty()) {
                            ChoiceUser user = choiceUsers.get(0);
                            UserDetail userDetail = new UserDetail(user.getUserName(), user.getUserPhoneNumber(), user.getUserAboutMe()
                                    , user.getUserImageUrl(),"");

                            userDetailList.add(userDetail);
                            adapter.notifyDataSetChanged();
                        } else {
                            userList.child(userID)
                                    .addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                                                        userDetailList.add(userDetail);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            }
                                    );
                        }
                    }
                });
    }

    private void stats(DatabaseReference user, ImageView icon) {
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    icon.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
