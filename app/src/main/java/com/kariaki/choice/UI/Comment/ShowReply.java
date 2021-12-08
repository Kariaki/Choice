package com.kariaki.choice.UI.Comment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.Database.Entities.CloudMessage;
import com.kariaki.choice.Model.Database.Entities.Message;
import com.kariaki.choice.Model.Database.MessageState;
import com.kariaki.choice.Model.Post;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Chat.ChatPage;
import com.kariaki.choice.UI.Messaging.Adapter.ChatAdapter;
import com.kariaki.choice.UI.Profiles.PartialProfile;
import com.kariaki.choice.UI.Profiles.SettingsPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowReply#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowReply extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowReply() {
        // Required empty public constructor
    }

    public static ShowReply newInstance(String param1, String param2) {
        ShowReply fragment = new ShowReply();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView commentReplies;
    ImageView blurBackground;
    Bitmap backgroundDrawable;
    ChatAdapter adapter;
    Post post;
    int themes;

    public void setThemes(int themes) {
        this.themes = themes;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    Message clickedMessage;

    public void setClickedMessage(Message clickedMessage) {
        this.clickedMessage = clickedMessage;
    }

    String clickedMessageReplyID;

    public void setClickedMessageReplyID(String clickedMessageReplyID) {
        this.clickedMessageReplyID = clickedMessageReplyID;
    }

    public Message getClickedMessage() {
        return clickedMessage;
    }

    public void setBackgroundDrawable(Bitmap backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
    }


    private String postFolder = "post";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_reply, container, false);

        commentReplies = view.findViewById(R.id.commentReplies);
        blurBackground = view.findViewById(R.id.blurBackground);
        blurBackground.setImageBitmap(backgroundDrawable);
        adapter = new ChatAdapter(getContext());

        adapterData();

        getComment(clickedMessageReplyID);
        return view;
    }

    private String postComments = "comments";

    private void getComment(String commentID) {
        DatabaseReference thisPostDocument = FirebaseDatabase.getInstance()
                .getReference(postFolder).child(post.getPostID());

        DatabaseReference thisPostComments = thisPostDocument.child(postComments);
        thisPostComments.keepSynced(false);
        thisPostComments.child(commentID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CloudMessage cloudMessage = snapshot.getValue(CloudMessage.class);
                            addCommenttoList(cloudMessage, MessageState.SENT, snapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    DatabaseReference realtimeUser = FirebaseDatabase.getInstance().getReference("users");
    ChatAdapter.OnclickListener onclickListener = new ChatAdapter.OnclickListener() {
        @Override
        public void OnclickChatProfileImage(int position) {
            Message message = (Message) messages.get(position);
            if (message.getMessageOwnerID().equals(new UserInformation(getContext()).getMainUserID())) {

                startActivity(new Intent(getActivity(), SettingsPage.class));

            } else {
                PartialProfile partialProfile = PartialProfile.getInstance(message.getMessageOwnerID());
                partialProfile.setOnclickMessageBox(() -> {
                    Intent intent = new Intent(getContext(), ChatPage.class);

                    realtimeUser.child(message.getMessageOwnerID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                                    intent.putExtra("name", userDetail.getUsername());
                                    intent.putExtra("profile picture", userDetail.getProfileURL());
                                    intent.putExtra("phone number", userDetail.getPhone());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                });

                partialProfile.show(getFragmentManager(), "partial profile");
            }
        }

        @Override
        public int currentlyPlaying() {
            return 0;
        }

        @Override
        public void playPauseButton(int position, ImageView playPauseButton, ProgressBar voiceNoteProgress, TextView progressDuration, String path) throws IOException {

        }

        @Override
        public boolean showName() {
            return false;
        }

        @Override
        public void onClickPrivateCommentMessage(int position) {

        }

        @Override
        public void downloadFiles(int position, ProgressBar progressBar, ImageView downloadButton, ImageView imagePreview) {

        }

        @Override
        public void onClickImageMessage(int position) {

            //add reply here if exist
        }

        @Override
        public void nameChoiceUser(int position, TextView nameTextView, CircleImageView profileImageView) {
            Message message = messages.get(position);
            // UserNaming userNaming = UserNaming.getInstance();
            //  userNaming.nameThisUser(CommentPage.this, getApplicationContext(), message.getMessageOwnerID(), nameTextView, profileImageView);

            realtimeUser.child(new UserInformation(getActivity()).getMainUserID()).child("people")
                    .child(message.getMessageOwnerID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                ChoiceUser contact = dataSnapshot.getValue(ChoiceUser.class);
                                nameTextView.setText(contact.getUserContactName());
                                Glide.with(getContext().getApplicationContext()).load(contact.getUserImageUrl()).thumbnail(0.4f).into(profileImageView);


                            } else {
                                realtimeUser.child(message.getMessageOwnerID())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                                    nameTextView.setText(detail.getUsername());
                                                    Glide.with(getContext().getApplicationContext()).load(detail.getProfileURL()).thumbnail(.4f).into(profileImageView);

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

        @Override
        public void downloadVoiceNote(int position, ProgressBar progressBar, ImageView downloadButton, ImageView replaceButton) {

        }

        @Override
        public void messageIsSeen(int position, ImageView indicator) {

        }

        @Override
        public void uploadVoiceNote(int position, ProgressBar uploadProgress, ImageView playButton, ImageView send_state) {

        }

        @Override
        public void uploadFiles(int position, ProgressBar uploadProgress, ImageView playButton, ImageView send_state) {

        }

        @Override
        public void setMessageToSeen(int position) {

        }

        @Override
        public void onLongClickMessage(int position, View itemView) {
            Message message = messages.get(position);


        }

        @Override
        public void onSeekBarChange(int position, int progress) {

        }

        @Override
        public void sendMessageText(int position) {

        }

        @Override
        public void setMessageTime(int position, TextView view) {

        }

        @Override
        public void onMessageClickListener(int position, View itemView) {

        }

        @Override
        public void onReplyClick(int position) {

        }

        @Override
        public DatabaseReference replyCounter(int position) {
            return null;
        }
    };

    public int getThemes() {
        return themes;
    }

    private void adapterData() {
        adapter.setMessage(messages);
        adapter.setTheme(getThemes());
        adapter.setOnclickListener(onclickListener);
        commentReplies.setLayoutManager(new LinearLayoutManager(getContext()));
        commentReplies.setHasFixedSize(true);
        commentReplies.setAdapter(adapter);
    }

    List<Message> messages = new ArrayList<>();

    private void addCommenttoList(CloudMessage comment, int state, String key) {
        Message message = new Message(key, post.getPostID(), comment.getMessageTYPE(), comment.getMessageOwnerID(),
                comment.getMessageTIME(),
                comment.getMessageCONTENT(), comment.getMessageURL(), state, state);
        messages.add(message);
        adapter.notifyDataSetChanged();
        // commentIDs.add(String.valueOf(comment.getMessageTIME()));

        // DatabaseReference reference = FirebaseDatabase.getInstance().getReference("post").child(cloudPost.getPostID());


    }

}