package com.kariaki.choice.ui.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.mainpage.adapters.SearchAdapter;
import com.kariaki.choice.ui.profiles.SocialProfile;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends AppCompatActivity {


    EditText searchInput;
    RecyclerView searchResult;
    DatabaseReference choiceUsers = FirebaseDatabase.getInstance().getReference("users");
    SearchAdapter adapter;
    List<UserDetail> userDetailList = new ArrayList<>();
    ImageView backArrow, cancelSearch;
    RelativeLayout rootView;
    LinearLayout infoHolder;
    ChoiceViewModel viewModel;
    TextView infoText;
    ImageView infoImage;
    DatabaseReference connected=FirebaseDatabase.getInstance().getReference(".info/connected");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        searchInput = findViewById(R.id.searchInput);
        cancelSearch = findViewById(R.id.cancelSearch);
        searchResult = findViewById(R.id.searchResult);
        infoImage=findViewById(R.id.infoImage);
        rootView = findViewById(R.id.rootView);
        infoText=findViewById(R.id.infoText);
        infoHolder=findViewById(R.id.infoHolder);
        backArrow = findViewById(R.id.backArrow);
        viewModel = ChoiceViewModel.getInstance(getApplication());
        loadTheme();
        adapter = new SearchAdapter();
        adapter.setCheck(new SearchAdapter.contactCheck() {
            @Override
            public void contactCheck(int position, LinearLayout icon) {
                UserDetail detail = userDetailList.get(position);
                viewModel.getChoiceUser(detail.getPhone())
                        .observe(SearchPage.this, new Observer<List<ChoiceUser>>() {
                            @Override
                            public void onChanged(List<ChoiceUser> choiceUsers) {
                                if (!choiceUsers.isEmpty()) {
                                    icon.setVisibility(View.VISIBLE);
                                }else {
                                    icon.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }

            @Override
            public void searchResultClick(int position) {

                Intent intent=new Intent(SearchPage.this, SocialProfile.class);
                intent.putExtra("user",userDetailList.get(position));
                startActivity(intent);

            }
        });


        adapter.setUserDetailList(userDetailList);
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchResult.setAdapter(adapter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                userDetailList.clear();
                ID.clear();
                adapter.notifyDataSetChanged();

                if (!s.toString().isEmpty()&&!Character.isDigit(s.charAt(0))) {
                    String newString = s.toString();
                    checkToSearch(newString);
                    cancelSearch.setVisibility(View.VISIBLE);
                } else {
                    userDetailList.clear();
                    ID.clear();
                    adapter.notifyDataSetChanged();
                    cancelSearch.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelSearch.setOnClickListener(v -> {
            searchInput.getText().clear();
            cancelSearch.setVisibility(View.GONE);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backPress(View view) {
        onBackPressed();
    }

    List<String>ID=new ArrayList<>();
    private void searchUsers(String s) {


        choiceUsers.orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if(!ID.contains(user.getKey())){
                                    infoHolder.setVisibility(View.GONE);
                                    ID.add(user.getKey());
                                    UserDetail userDetail = user.getValue(UserDetail.class);
                                    userDetailList.add(userDetail);

                                    adapter.notifyDataSetChanged();
                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void searchOnSecondCase(String s){
        char firstChar=s.charAt(0);
        if(Character.isUpperCase(firstChar)){
            firstChar=Character.toLowerCase(firstChar);
        }else {
            firstChar=Character.toUpperCase(firstChar);
        }
        String otherString=firstChar+s.substring(1,s.length());
        choiceUsers.orderByChild("username")
                .startAt(otherString)
                .endAt(s + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if(!ID.contains(user.getKey())){
                                    infoHolder.setVisibility(View.GONE);
                                    ID.add(user.getKey());
                                    UserDetail userDetail = user.getValue(UserDetail.class);
                                    userDetailList.add(userDetail);

                                    adapter.notifyDataSetChanged();
                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void searchOnDisplayName(String s) {



        choiceUsers.orderByChild("displayName")
                .startAt(s)
                .endAt(s + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if(!ID.contains(user.getKey())){
                                    infoHolder.setVisibility(View.GONE);
                                    ID.add(user.getKey());
                                    UserDetail userDetail = user.getValue(UserDetail.class);
                                    userDetailList.add(userDetail);

                                    adapter.notifyDataSetChanged();
                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void searchOnSecondCaseDisplayName(String s){
        char firstChar=s.charAt(0);
        if(Character.isUpperCase(firstChar)){
            firstChar=Character.toLowerCase(firstChar);
        }else {
            firstChar=Character.toUpperCase(firstChar);
        }
        String otherString=firstChar+s.substring(1);
        choiceUsers.orderByChild("displayName")
                .startAt(otherString)
                .endAt(s + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                if(!ID.contains(user.getKey())){
                                    infoHolder.setVisibility(View.GONE);
                                    ID.add(user.getKey());
                                    UserDetail userDetail = user.getValue(UserDetail.class);
                                    userDetailList.add(userDetail);

                                    adapter.notifyDataSetChanged();
                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void checkToSearch(String s){
        connected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean connect=dataSnapshot.getValue(Boolean.class);
                if(connect){
                    searchUsers(s);
                    //searchOnSecondCase(s);
                    searchOnDisplayName(s);
                    searchOnSecondCaseDisplayName(s);
                }else {
                    infoImage.setImageResource(R.drawable.not_connected);
                    YoYo.with(Techniques.Wave).repeat(YoYo.INFINITE).duration(1000).playOn(infoImage);
                    infoText.setText("You are not connected");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                //changeTextColors(text,R.color.textContext);

                rootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                searchInput.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                rootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));


                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


}
