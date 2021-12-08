package com.kariaki.choice.ui.GroupChat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.GroupChat.Adapter.GroupMembersListAdapter;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

public class GroupChatMembers extends AppCompatActivity {

    RecyclerView groupChatMemberList;
    List<GroupMember> groupMemberList=new ArrayList<>();
    GroupMembersListAdapter listAdapter;
    DatabaseReference groupchat= FirebaseDatabase.getInstance().getReference("GroupChats");
    TextView groupMembersCount,groupmemberText;
    ImageView groupchatprofileBackButton;
    LinearLayout chatMemberRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_members);
        groupChatMemberList=findViewById(R.id.groupChatMemberList);
        groupMembersCount=findViewById(R.id.groupMembersCount);
        groupmemberText=findViewById(R.id.groupmemberText);
        chatMemberRootView=findViewById(R.id.chatMemberRootView);
        groupchatprofileBackButton=findViewById(R.id.groupchatprofileBackButton);
        listAdapter=new GroupMembersListAdapter();
        listAdapter.setActivity(this
        );
        Intent groupID=getIntent();
        int type=groupID.getIntExtra("type",-1);
        String id=groupID.getStringExtra("group chat id");
        if(type==-1) {

            groupchat.child(id).child("members")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                groupMembersCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                for (DataSnapshot member : dataSnapshot.getChildren()) {
                                    GroupMember thisMember = member.getValue(GroupMember.class);
                                    groupMemberList.add(thisMember);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }else {

        }
        listAdapter.setGroupChatGroupChatMembers(groupMemberList);
        groupChatMemberList.setAdapter(listAdapter);
        groupChatMemberList.setLayoutManager(new LinearLayoutManager(this));
        groupChatMemberList.setHasFixedSize(true);
        loadTheme();

    }

    public void goBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                chatMemberRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                chatMemberRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    private void loadTheme(){

        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView []textViews={groupMembersCount,groupmemberText};
        setTextColors(textViews,currentTheme);

    }

    private void changeTextColors(TextView[]textViews,int color){

        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }
}
