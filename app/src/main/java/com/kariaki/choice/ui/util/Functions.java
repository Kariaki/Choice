package com.kariaki.choice.ui.util;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.post.PostInfo;
import com.kariaki.choice.ui.util.time.TimeFactor;

import java.util.HashMap;
import java.util.Map;

public class Functions {



    public static String fileExtension(String path) {
        char[] filepath = path.toCharArray();
        String output = "";
        for (int i = filepath.length - 1; i >= filepath.length - 3; i--) {
            if (filepath[i] != '.') {
                output += String.valueOf(filepath[i]);
            }
        }
        output = output + ".";
        filepath = output.toCharArray();
        String newoutput = "";
        for (int i = filepath.length - 1; i >= 0; i--) {
            newoutput = newoutput + String.valueOf(filepath[i]);
        }

        return newoutput;
    }
    public static String numberAnalyzer(String phoneNumber) {
        String outputPhoneNumber = "";
        if (!phoneNumber.startsWith("+")) {
            switch (phoneNumber.length()) {
                case 11:
                    String nPhoneNumber = phoneNumber.substring(1);
                    outputPhoneNumber = "+234" + nPhoneNumber;
                    break;
                case 14:
                    outputPhoneNumber = phoneNumber;
                    break;
                case 15:
                    String prefix = phoneNumber.substring(0, 3);
                    String startZero = phoneNumber.substring(5, phoneNumber.length());
                    outputPhoneNumber = prefix + startZero;


            }
        } else {
            outputPhoneNumber = phoneNumber;
        }

        return outputPhoneNumber;
    }


    public static void deleteFile(String urls) {

        if (!urls.isEmpty()) {
            String allURL[] = urls.split(",");
            for (String url : allURL) {
                StorageReference fileToDelete = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                fileToDelete.delete();
            }
        }
    }

    static String online = "Online";

    public static void onLineCheck(Context context, String userID, TextView onLineStatus, long timeStamp) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(userID).child("Connection");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConnectionState connectionState = dataSnapshot.getValue(ConnectionState.class);
                if (dataSnapshot.exists()) {

                    if (connectionState.isOnline()) {
                        onLineStatus.setVisibility(View.VISIBLE);
                        onLineStatus.setText(online);

                    } else {
                        assert connectionState != null;
                        String lastSeen = TimeFactor.getDetailedDate((connectionState.getLastSeen()), (timeStamp));
                        //TimeFactor.getDetailedDate(TimeFactor.now(connectionState.getLastSeen()), timeStamp);
                        onLineStatus.setText(lastSeen);
                        onLineStatus.setVisibility(View.GONE);
                    }

                } else {
                    onLineStatus.setText("");
                    onLineStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void onLineCheck(Context context, String userID, TextView onLineStatus, long timeStamp,ImageView image) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(userID).child("Connection");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConnectionState connectionState = dataSnapshot.getValue(ConnectionState.class);
                if (dataSnapshot.exists()) {

                    if (connectionState.isOnline()) {
                        onLineStatus.setVisibility(View.VISIBLE);
                        image.setVisibility(View.VISIBLE);
                        onLineStatus.setText(online);

                    } else {
                        assert connectionState != null;
                        String lastSeen = TimeFactor.getDetailedDate((connectionState.getLastSeen()), (timeStamp));
                        //TimeFactor.getDetailedDate(TimeFactor.now(connectionState.getLastSeen()), timeStamp);
                        onLineStatus.setText(lastSeen);
                        onLineStatus.setVisibility(View.GONE);
                    }

                } else {
                    onLineStatus.setText("");
                    onLineStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public static void muteContact(String myPhonNumber, String theContact) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(myPhonNumber).child("Connection");

    }

    public static void unMuteContact(String myPhonNumber, String theContact) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(myPhonNumber).child("Connection");

    }

    public static void blockContact(String myPhonNumber, String theContact, TextView blockImageView) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(myPhonNumber).child("chats").child(theContact);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> blockUser = new HashMap<>();
                    blockUser.put("blockUser", true);
                    dataSnapshot.getRef().updateChildren(blockUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    blockImageView.setText(R.string.Unblock);
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void unBlockContact(String myPhonNumber, String theContact, TextView blockImageView) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(myPhonNumber).child("chats").child(theContact);

        Map<String, Object> blockUser = new HashMap<>();
        blockUser.put("blockUser", false);
        databaseReference.updateChildren(blockUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        blockImageView.setText("Block");
                    }
                });
    }


    public static void countLikes(TextView counter, String postID, Post post) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post")
                .child(postID).child("likes");
        databaseReference.keepSynced(true);
        DatabaseReference myTimeLine = FirebaseDatabase.getInstance().getReference("users").child(post.getOwnerID()).child("timeLine").child(postID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    counter.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                } else {
                    counter.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void mergeLikeCount(TextView counter, String postID, Post post, String folder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post")
                .child(postID).child(folder);
        databaseReference.keepSynced(true);
         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    counter.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                } else {
                    counter.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void countComments(TextView counter, String postID) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post")
                .child(postID).child("comments");
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    counter.setText(String.valueOf(dataSnapshot.getChildrenCount()));

                }else {
                    counter.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void viewsCount(TextView counter, String postID) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post")
                .child(postID).child("views");

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String text=String.valueOf(dataSnapshot.getChildrenCount())+" views";
                    counter.setVisibility(View.VISIBLE);
                    counter.setText(text);

                }else {
                    counter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void nameReplier(TextView textView,String userID){
        DatabaseReference allUsers=FirebaseDatabase.getInstance().getReference("users");
        allUsers.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            UserDetail detail=snapshot.getValue(UserDetail.class);
                            String text="replied @"+detail.getUsername();
                            textView.setText(text);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static void reviewReplies(TextView replyTextView,DatabaseReference replyRef){
        replyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int repliesCount=(int)dataSnapshot.getChildrenCount();
                    String text;
                    if(repliesCount==1){
                        text = String.valueOf(repliesCount) + " " + "reply";
                    }else {
                        text = String.valueOf(repliesCount) + " " + "replies";
                    }
                    replyTextView.setText(text);
                }else {
                    String text="reply";
                    replyTextView.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateLifeSpanTransaction(DatabaseReference postInfo, long lifeSpanToAdd) {

        postInfo.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                PostInfo postInfo1 = mutableData.getValue(PostInfo.class);

                long lifespan = postInfo1.getPostLifeSpan();
                lifespan += lifeSpanToAdd;
                postInfo1.setPostLifeSpan(lifespan);
                mutableData.setValue(postInfo1);


                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

    }

}
