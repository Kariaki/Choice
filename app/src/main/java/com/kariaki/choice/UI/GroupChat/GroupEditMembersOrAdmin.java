package com.kariaki.choice.UI.GroupChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.GroupChat.Adapter.GroupChatOptions;
import com.kariaki.choice.UI.GroupChat.Adapter.GroupMembersListAdapter;
import com.kariaki.choice.UI.util.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupEditMembersOrAdmin extends AppCompatActivity {

    RecyclerView groupChatMemberList;
    List<GroupMember> groupMemberList = new ArrayList<>();
    GroupMembersListAdapter listAdapter;
    DatabaseReference groupchat = FirebaseDatabase.getInstance().getReference("GroupChats");
    TextView groupMembersCount, groupmemberText;
    RecyclerView editMemberList;
    ImageView groupchatprofileBackButton;
    DatabaseReference groupMembers;
    LinearLayout editMemberRoot;
    List<String>ID=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit_members_or_admin);

        editMemberList = findViewById(R.id.editMemberList);
        listAdapter = new GroupMembersListAdapter();
        editMemberRoot=findViewById(R.id.editMemberRoot);
        listAdapter.setActivity(this);
        groupmemberText = findViewById(R.id.groupmemberText);

        groupchatprofileBackButton = findViewById(R.id.groupchatprofileBackButton);

        Intent groupID = getIntent();
        String id = groupID.getStringExtra("group chat id");
        int option = groupID.getIntExtra("option", GroupChatOptions.EDIT_MEMBERS);

        listAdapter.setOption(option);
        listAdapter.setGroupChatGroupChatMembers(groupMemberList);

        if (option == GroupChatOptions.EDIT_ADMIN) {
            groupmemberText.setText("Add or remove an admin");
            editAdminAdapter();
        } else {
            editMemberAdapter();
        }


        loadTheme();



        editMemberList.setHasFixedSize(true);
        editMemberList.setLayoutManager(new LinearLayoutManager(this));
        groupMembers = groupchat.child(id).child("members");


        editMemberList.setAdapter(listAdapter);

        groupchat.child(id).child("members")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!ID.contains(snapshot.getKey())) {
                                    GroupMember member = snapshot.getValue(GroupMember.class);

                                    groupMemberList.add(member);
                                    ID.add(snapshot.getKey());
                                   }
                                listAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void showRemoveDialog(DatabaseReference databaseReference) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Do you want to remove this person")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.removeValue();

                    }
                }).create();
        alertDialog.show();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void makeAdmin(DatabaseReference user) {
        Map<String, Object> assignAdmin = new HashMap<>();
        assignAdmin.put("admin", true);
        user.updateChildren(assignAdmin);
    }

    public void removeAdmin(DatabaseReference user) {
        Map<String, Object> assignAdmin = new HashMap<>();
        assignAdmin.put("admin", false);
        user.updateChildren(assignAdmin);
    }

    public void editAdminAdapter() {

        listAdapter.setOnClick(new GroupMembersListAdapter.groupMemberOptionOnClick() {
            @Override
            public void onclickRemoveMember(int position,TextView invite) {
                GroupMember selectedMember = groupMemberList.get(position);
                DatabaseReference rmovedMember = groupMembers.child(selectedMember.memberPhone);
                removeAdmin(rmovedMember);

                // invite.setVisibility(View.VISIBLE);
            }

            @Override
            public void onclickInviteMember(int position,TextView remove) {
                GroupMember selectedMember = groupMemberList.get(position);
                DatabaseReference rmovedMember = groupMembers.child(selectedMember.memberPhone);
                makeAdmin(rmovedMember);
                //remove.setVisibility(View.VISIBLE);
            }
        });

    }

    public void editMemberAdapter() {

        listAdapter.setOnClick(new GroupMembersListAdapter.groupMemberOptionOnClick() {
            @Override
            public void onclickRemoveMember(int position,TextView invite) {
                GroupMember selectedMember = groupMemberList.get(position);
                DatabaseReference rmovedMember = groupMembers.child(selectedMember.memberPhone);
                showRemoveDialog(rmovedMember);
            }

            @Override
            public void onclickInviteMember(int position,TextView remove) {

            }
        });

    }



    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);

                editMemberRoot.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                listAdapter.setColor(R.color.textHeaderColor);
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                listAdapter.setColor(R.color.whiteGreen);
                editMemberRoot.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView []textViews={groupmemberText};
        setTextColors(textViews,currentTheme);

    }
    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }

}
