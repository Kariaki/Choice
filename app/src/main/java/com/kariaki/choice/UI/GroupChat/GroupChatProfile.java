package com.kariaki.choice.UI.GroupChat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.DialogBox.ChoiceDialogBox;
import com.kariaki.choice.UI.DialogBox.ChoiceNewDialogBox;
import com.kariaki.choice.UI.GroupChat.Adapter.GroupChatOptions;
import com.kariaki.choice.UI.Profiles.EditFromBottom;
import com.kariaki.choice.UI.Profiles.ViewProfilePicture;
import com.kariaki.choice.UI.util.Theme;
import com.kariaki.choice.UI.util.Time.TimeFactor;
import com.kariaki.choice.UI.util.UserNaming;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatProfile extends AppCompatActivity {



    TextView groupchatProfileGroupChatName, GroupChatCreatorName,
            groupchatCreatedTime, groupMembersCount,muteGroupText,addAdminText,editMemberText,addMemberText;
    CircleImageView groupprofilePicture,groupChatCreatorProfileImage;
    RelativeLayout addMember, viewMembers,editGroupAdmin,groupChatProfileRootView;
    String groupChatID;
    ImageView muteGroupIcon,groupchatprofileBackButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGroupChat = database.getReference("GroupChats");
    DatabaseReference currentGroupChat;
    DatabaseReference allUsers = database.getReference("users");
    DatabaseReference me;
    ImageView changeGroupIcon;
    ProgressBar groupChatProgressBar;
    RelativeLayout editGroupMembers;
    EmojiTextView groupDescription;
    ImageView editGroupDescription;

    GroupChatInformation groupChatInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_profile);
        viewByIDs();
        Intent groupChatInfo = getIntent();
        groupChatID = groupChatInfo.getStringExtra("groupChatID");
        groupDescription=findViewById(R.id.groupDescription);
        editGroupDescription=findViewById(R.id.editGroupDescription);
        groupChatProgressBar=findViewById(R.id.groupChatProgressBar);
        //isAdmin();
        allGroupChat.child(groupChatID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GroupChatInformation information = dataSnapshot.getValue(GroupChatInformation.class);
                            groupChatInformation=information;
                            groupChatDetails(information);
                            long timeCreated=information.getTime_created();
                          String time=  TimeFactor.getDetailedDate(timeCreated,System.currentTimeMillis());
                          groupchatCreatedTime.setText(time);
                          String description=information.getDescription();
                          if(description!=null){
                              groupDescription.setText(description);
                          }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        loadTheme();
        checkIfAmAdmin();

    }


    private void groupChatDetails(GroupChatInformation groupChatInformation) {

        Glide.with(getApplicationContext()).load(groupChatInformation.getImageUrl()).thumbnail(0.45f).diskCacheStrategy(DiskCacheStrategy.ALL).into(groupprofilePicture);

        groupchatProfileGroupChatName.setText(groupChatInformation.getGroupChatName());
        UserNaming userNaming = UserNaming.getInstance();
        UserInformation information = new UserInformation(this);

       // CircleImageView circleImageView = new CircleImageView(this);

        if (groupChatInformation.getCreator().equals(information.getMainUserID())) {
            GroupChatCreatorName.setText("You");
            editGroupAdmin.setVisibility(View.VISIBLE);
            viewMembers.setVisibility(View.GONE);
            addMember.setVisibility(View.VISIBLE);
            changeGroupIcon.setVisibility(View.VISIBLE);
        } else {

            editGroupAdmin.setVisibility(View.GONE);

            changeGroupIcon.setVisibility(View.GONE);
            userNaming.nameThisUser(this, this, groupChatInformation.getCreator(), GroupChatCreatorName, groupChatCreatorProfileImage);
        }

        allGroupChat.child(groupChatID).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String member_text = groupMembersCount.getText().toString() + "\t" + dataSnapshot.getChildrenCount();
                            groupMembersCount.setText(member_text);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        groupMembersCount.setText("");

                    }
                });

    }
    private void checkIfAmAdmin(){
        allGroupChat.child(groupChatID).child("members").child(new UserInformation(this).getMainUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            GroupMember member=dataSnapshot.getValue(GroupMember.class);
                            if(!member.isAdmin()){
                                viewMembers.setVisibility(View.VISIBLE);
                                editGroupMembers.setVisibility(View.GONE);
                                addMember.setVisibility(View.GONE);
                                editGroupDescription.setVisibility(View.INVISIBLE);

                            }else {
                                viewMembers.setVisibility(View.GONE);
                                addMember.setVisibility(View.VISIBLE);
                                editGroupMembers.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void exitThisGroup(View view) {

        currentGroupChat = allGroupChat.child(groupChatID);
        UserInformation information = new UserInformation(this);
        String myID = information.getMainUserID();
        me = allUsers.child(myID);
        DatabaseReference groupChatMember = currentGroupChat.child("members");
        DatabaseReference groupChatinMyChat = me.child("chats").child(groupChatID);
        showExitDialog(groupChatMember, groupChatinMyChat);


    }

    ChoiceNewDialogBox dialogBox;
    private void showExitDialog(DatabaseReference removeMe, DatabaseReference removeFromMyChats) {
        dialogBox=new ChoiceNewDialogBox();
        String tittle="Exit";
        String message="Are you sure you want to ext this group chat?";
        dialogBox.setTextColors(R.color.textContext);
        dialogBox.setMessage(message);
        dialogBox.setTittle(tittle);
        dialogBox.setListeners(new ChoiceDialogBox.dialogButtons() {
            @Override
            public void onClickPositiveButton() {
                removeMe.removeValue();
                removeFromMyChats.removeValue();
            }

            @Override
            public void onClickNegativeButton() {
                dialogBox.dismiss();
            }
        });
        dialogBox.show(getSupportFragmentManager(),"dialog dialog");

    }
    private void showMuteGroupDialog(DatabaseReference muteGroup,Map<String,Object>data){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Do you want to mute this group?")
                .setMessage("You will no longer receive notification from this group chat")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    muteGroup.updateChildren(data);
                    muteGroupIcon.setImageResource(R.drawable.notification_close);
                    muteGroupText.setText("UnMute");

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    public void muteGroup(View view){


    }


    public void addMember(View view) {

        bottomContactList.setGroupChatID(groupChatID);
        bottomContactList.show(getSupportFragmentManager(),"bottom group list");
    }
    AddContactBottomContactList bottomContactList=new AddContactBottomContactList();

    public void viewMembers(View view) {

        Intent intent = new Intent(this, GroupChatMembers.class);
        intent.putExtra("group chat id", groupChatID);
        startActivity(intent);

    }


    public void isAdmin() {
        String id = new UserInformation(this).getMainUserID();
        allGroupChat.child(groupChatID).child("members").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GroupMember groupMember = dataSnapshot.getValue(GroupMember.class);
                            assert groupMember != null;
                            if (!groupMember.isAdmin()) {
                                addMember.setVisibility(View.VISIBLE);
                                changeGroupIcon.setVisibility(View.VISIBLE);
                                editGroupAdmin.setVisibility(View.GONE);
                                viewMembers.setVisibility(View.GONE);
                                editGroupMembers.setVisibility(View.VISIBLE);

                            } else {
                                addMember.setVisibility(View.GONE);
                                editGroupAdmin.setVisibility(View.GONE);
                                changeGroupIcon.setVisibility(View.INVISIBLE);
                                editGroupAdmin.setVisibility(View.GONE);
                                viewMembers.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void openViewProfilePage(View view) {

        Intent intent = new Intent(this, ViewProfilePicture.class);

        intent.putExtra("profileImage", groupChatInformation.getImageUrl());
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, groupprofilePicture,
                ViewCompat.getTransitionName(groupprofilePicture));
        startActivity(intent, optionsCompat.toBundle());

    }

    public void backPress(View view) {
        onBackPressed();
    }

    private final int PICK_IMAGE = 101;

    public void changeGroupIcon(View view){

        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select pictures"), PICK_IMAGE);

    }


    private void uploadProfilePicture(Uri uri) {

        StorageReference newProfilePicture = FirebaseStorage.getInstance().getReference("ProfilePictures")
                .child(System.currentTimeMillis() + getFileExtension(uri));
        groupChatProgressBar.setVisibility(View.VISIBLE);


        newProfilePicture.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            newProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String fileURI = uri.toString();
                    Map<String, Object> update = new HashMap<>();
                    update.put("imageUrl", fileURI);
                    allGroupChat.child(groupChatID).updateChildren(update)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    groupChatProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(GroupChatProfile.this, "Profile image changed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    groupChatProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(GroupChatProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
        });


    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(60)
                    .start(this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(groupprofilePicture);
                uploadProfilePicture(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void editMembers(View view){

        Intent intent=new Intent(this,GroupEditMembersOrAdmin.class);
        intent.putExtra("group chat id",groupChatID);
        intent.putExtra("option", GroupChatOptions.EDIT_MEMBERS);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    public void editAdmin(View view){

        Intent intent=new Intent(this,GroupEditMembersOrAdmin.class);
        intent.putExtra("group chat id",groupChatID);
        intent.putExtra("option",GroupChatOptions.EDIT_ADMIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }



    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);
                color=R.color.textContext;

                groupChatProfileRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                color=R.color.whiteGreen;
                groupChatProfileRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    private void loadTheme(){

        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);
        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView []textViews={groupMembersCount,muteGroupText,groupchatCreatedTime,addAdminText,addMemberText,editMemberText,
        groupchatProfileGroupChatName,groupChatProfileText,GroupChatCreatorName,groupDescription};
        setTextColors(textViews,currentTheme);

    }

    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }


    TextView groupChatProfileText;
    private void viewByIDs() {

        GroupChatCreatorName = findViewById(R.id.GroupChatCreatorName);
        addAdminText=findViewById(R.id.addAdminText);
        editGroupMembers=findViewById(R.id.editGroupMembers);
        groupChatProfileText=findViewById(R.id.groupChatProfileText);
        addMemberText=findViewById(R.id.addMemberText);
        editMemberText=findViewById(R.id.editMemberText);
        groupchatProfileGroupChatName = findViewById(R.id.groupchatProfileGroupChatName);
        groupchatCreatedTime = findViewById(R.id.groupchatCreatedTime);
        groupprofilePicture = findViewById(R.id.groupprofilePicture);
        addMember = findViewById(R.id.addMember);
        changeGroupIcon=findViewById(R.id.changeGroupIcon);
        groupChatProfileRootView=findViewById(R.id.groupChatProfileRootView);
        muteGroupText=findViewById(R.id.muteGroupText);
        groupChatCreatorProfileImage=findViewById(R.id.groupChatCreatorProfileImage);
        muteGroupIcon = findViewById(R.id.muteGroupIcon);
        editGroupAdmin=findViewById(R.id.editGroupAdmin);
        groupMembersCount = findViewById(R.id.groupMembersCount);
        groupchatprofileBackButton=findViewById(R.id.groupchatprofileBackButton);
        viewMembers = findViewById(R.id.viewMembers);

    }


    public  Date getDateDate(long milliSeconds, String format) {

        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssZ");

        milliSeconds=(milliSeconds);
        String dateString=formatter.format(new Date(milliSeconds));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date value=null;
        try {
            value=formatter.parse(dateString);
            SimpleDateFormat newFormat=new SimpleDateFormat(format);
            newFormat.setTimeZone(TimeZone.getDefault());
            dateString=newFormat.format(value);
        }catch (ParseException e){
            e.printStackTrace();
            dateString="";
        }
        return value;

    }

    public void editDescription(View view){
        String ID=new UserInformation(GroupChatProfile.this).getMainUserID();
        allGroupChat.child(groupChatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupChatInformation information=dataSnapshot.getValue(GroupChatInformation.class);
                        if(information.getCreator().equalsIgnoreCase(ID)){
                            editGroupInfo();
                        }else {
                            editDescription2(ID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
    private void editDescription2(String ID){
        allGroupChat.child(groupChatID).child("members").child(ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            GroupMember member=dataSnapshot.getValue(GroupMember.class);
                            if(member.isAdmin()){
                                editGroupInfo();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    EditFromBottom editAbout;
    int color=R.color.textContext;

    private void editGroupInfo(){
        editAbout=new EditFromBottom();
        editAbout.setClickListeners(new EditFromBottom.onClickListeners() {
            @Override
            public void onClickConfirm(String text) {
                Map<String,Object>update=new HashMap<>();
                update.put("description",text);
                allGroupChat.child(groupChatID).updateChildren(update);
                groupDescription.setText(text);
                editAbout.dismiss();
            }

            @Override
            public void onClickCancel() {
                editAbout.dismiss();

            }
        });
        editAbout.show(getSupportFragmentManager(),"edit edit about");
    }

}
