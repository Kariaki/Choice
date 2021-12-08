package com.kariaki.choice.UI.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.Contact;
import com.kariaki.choice.Model.Database.Entities.GroupChatInformation;
import com.kariaki.choice.Model.Database.Entities.MyChatChannel;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.MainPage.Pages.Chat;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserNaming {

    private String userPhoneNumber;
    private String name;
    private DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference("users");
    private UserInformation information;
    private String profileImage;
    private String aboutUser;
    private static DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    private UserNaming(){

    }
    private static UserNaming userNaming;

    public static UserNaming getInstance(){
        if(userNaming==null){
            userNaming=new UserNaming();

        }
        return userNaming;
    }


    public  void nameThisUser(String userPhoneNumber, Context context,
                              TextView nameView,
                              CircleImageView userProfileView){
        this.userPhoneNumber=userPhoneNumber;
        information=new UserInformation(context);
        allUsers.child(information.getMainUserID()).child("people").child(userPhoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            ChoiceUser contact=dataSnapshot.getValue(ChoiceUser.class);
                            name=contact.getUserContactName();
                            //setName(name);
                            nameView.setText(name);
                            allUsers.child(userPhoneNumber)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                                                profileImage=detail.getProfileURL();
                                                setProfileImage(detail.getProfileURL());
                                                aboutUser=detail.getAbout();
                                                setAboutUser(detail.getAbout());

                                                Glide.with(context.getApplicationContext())

                                                        .load(detail.getProfileURL()).into(userProfileView);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }else {
                            allUsers.child(userPhoneNumber)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                                                name=detail.getUsername();
                                                setName(detail.getUsername());
                                                profileImage=detail.getProfileURL();
                                                setProfileImage(detail.getProfileURL());
                                                aboutUser=detail.getAbout();
                                                setAboutUser(detail.getAbout());
                                                nameView.setText(name);
                                                //aboutView.setText(aboutUser);
                                               Glide.with(context.getApplicationContext()).load(detail.getProfileURL()).into(userProfileView);



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


    public void nameThisUser(Activity application,Context context,
                             String userPhoneNumber,
                             TextView nameView,CircleImageView userProfileView){
        ChoiceViewModel viewModel= ChoiceViewModel.getInstance(application.getApplication());

        viewModel.getChoiceUser(userPhoneNumber).observe((LifecycleOwner) application, new Observer<List<ChoiceUser>>() {
            @Override
            public void onChanged(List<ChoiceUser> choiceUsers) {

                if(choiceUsers!=null&&choiceUsers.size()>0){
                    ChoiceUser myContact=choiceUsers.get(0);
                    nameView.setText(myContact.getUserContactName());

                    Glide.with(context.getApplicationContext()).load(myContact.getUserImageUrl()).thumbnail(0.3f).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.imageplaceholder).into(userProfileView);

                }else {
                    allUsers.child(userPhoneNumber)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                                        name=detail.getUsername();
                                        setName(detail.getUsername());
                                        profileImage=detail.getProfileURL();
                                        setProfileImage(detail.getProfileURL());
                                        aboutUser=detail.getAbout();
                                        setAboutUser(detail.getAbout());

                                        nameView.setText(name);
                                        //aboutView.setText(aboutUser);
                                        Picasso.get().load(detail.getProfileURL())
                                                .networkPolicy(NetworkPolicy.OFFLINE)
                                                .fit()
                                                .into(userProfileView);



                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

            }
        });



    }

    public  void nameThisUser(String userPhoneNumber, Context context,
                              TextView nameView, TextView aboutUserView,
                              CircleImageView userProfileView){
        this.userPhoneNumber=userPhoneNumber;
        information=new UserInformation(context);
        allUsers.child(information.getMainUserID()).child("people").child(userPhoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Contact contact=dataSnapshot.getValue(Contact.class);
                            name=contact.getContactName();
                            //setName(name);
                            nameView.setText(name);
                            allUsers.child(userPhoneNumber)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                                                profileImage=detail.getProfileURL();
                                                setProfileImage(detail.getProfileURL());
                                                aboutUser=detail.getAbout();
                                                setAboutUser(detail.getAbout());
                                                aboutUserView.setText(aboutUser);
                                                Picasso.get().load(detail.getProfileURL())
                                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                                        .fit()
                                                        .into(userProfileView);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }else {
                            allUsers.child(userPhoneNumber)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                                                name=detail.getUsername();
                                                setName(detail.getUsername());
                                                profileImage=detail.getProfileURL();
                                                setProfileImage(detail.getProfileURL());
                                                aboutUser=detail.getAbout();
                                                setAboutUser(detail.getAbout());
                                                aboutUserView.setText(aboutUser);
                                                nameView.setText(name);
                                                //aboutView.setText(aboutUser);
                                                Picasso.get().load(detail.getProfileURL())
                                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                                        .fit()
                                                        .into(userProfileView);




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


    DatabaseReference groupChatFolder=FirebaseDatabase.getInstance().getReference("GroupChats");

    public void nameThisGroupChat(String groupChatName,Context context,
                                  TextView nameView,CircleImageView groupChatImage){

        groupChatFolder.child(groupChatName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GroupChatInformation information=dataSnapshot.getValue(GroupChatInformation.class);
                   Glide.with(context).load(information.getImageUrl()).into(groupChatImage);

                    String chatName=information.getGroupChatName();
                    if(chatName!=null){
                        nameView.setText(chatName);
                    }else {
                        nameView.setText(groupChatName);
                    }

                   // nameView.setText(information);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadProfilePicture(String id, Context context, Chat.OpenPageListener listener, MyChatChannel thisChat, String text){
        users.child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                          /*  Picasso.get().load(detail.getProfileURL())
                                    .fit()
                                    .into(imageView);

                           */
                          Glide.with(context).load(detail.getProfileURL()).into(new CustomTarget<Drawable>() {
                              @Override
                              public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                  listener.openPage(thisChat,resource,text);

                              }

                              @Override
                              public void onLoadCleared(@Nullable Drawable placeholder) {

                              }
                          });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void loadProfilePicture(String id,CircleImageView imageView,Context context){
       DatabaseReference dbRef= users.child(id).child("profileURL");
       dbRef.keepSynced(true);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                            String url=dataSnapshot.getValue(String.class);
                           Glide.with(context.getApplicationContext()).load(url).into(imageView);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static  void getUserName(String userID,TextView nameTextView){
        users.child(userID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            UserDetail detail=dataSnapshot.getValue(UserDetail.class);
                            assert detail != null;
                            nameTextView.setText(detail.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public static  void getUserName(String userID,String myID,TextView nameTextView){
        users.child(myID)
                .child("people").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Contact contact=dataSnapshot.getValue(Contact.class);
                           // nameTextView.setText(contact.getPhoneNumber());
                        }else {
                          DatabaseReference dref=  users.child(userID).child("username");
                          dref.keepSynced(true);
                                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                String detail=dataSnapshot.getValue(String.class);
                                                nameTextView.setText(detail);

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


    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
