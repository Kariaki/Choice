package com.kariaki.choice.ui.dialogbox;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.ContactPost;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.post.PostTypes;
import com.kariaki.choice.ui.util.Theme;

import static android.content.Context.MODE_PRIVATE;

public class OptionBottomSheet extends BottomSheetDialogFragment {

    public static OptionBottomSheet getInstance() {

        return new OptionBottomSheet();
    }

    public interface OptionClickListener {


        void onCopyText();

        void onCancelRepeat();

        void onHidePost();

        void onRepost();

        void onMutePost();
        void commentPrivately();


    }

    private OptionClickListener listener;

    public void setListener(OptionClickListener listen) {
        this.listener = listen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    LinearLayout curveHolder,commentPrivately;

    TextView mutePostText, repostText, cancelRepeatText, copyPostText, hidePostText,commentPrivatelyText;

    private Post post;
    private TextView shareTitle;


    private boolean share;


    public void setPost(Post post) {
        this.post = post;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.post_option_bottomsheet, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        curveHolder = contentView.findViewById(R.id.curveHolder);
        viewByID(contentView);

        loadTheme();

        setUI();
        clickListeners();

        if(post.isRepeat()){
            cancelRepeat.setVisibility(View.VISIBLE);
        }else {
            cancelRepeat.setVisibility(View.GONE);
        }

        userMuteState();
       if(post.isAllowPrivateComment()&&post.getPostType()!=PostTypes.MERGED_POST) {
            commentPrivately.setVisibility(View.VISIBLE);
        }else {
            commentPrivately.setVisibility(View.GONE);
        }



    }

    ChoiceViewModel viewModel;


    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    LinearLayout mutePost, repost, cancelRepeat, copyPost, hidepost;

    private void viewByID(View view) {
        hidePostText = view.findViewById(R.id.hidePostText);
        copyPostText = view.findViewById(R.id.copyPostText);
        cancelRepeatText = view.findViewById(R.id.cancelRepeatText);
        repostText = view.findViewById(R.id.repostText);
        mutePostText = view.findViewById(R.id.mutePostText);
        hidepost = view.findViewById(R.id.hidepost);
        copyPost = view.findViewById(R.id.copyPost);
        cancelRepeat = view.findViewById(R.id.cancelRepeat);
        repost = view.findViewById(R.id.repost);
        commentPrivately=view.findViewById(R.id.commentPrivately);
        mutePost = view.findViewById(R.id.mutePost);
        commentPrivatelyText=view.findViewById(R.id.commentPrivatelyText);
    }


    @Override
    public void onStart() {
        super.onStart();
        //loadTheme();
    }

    public void setTextColors(int currentTheme) {
        TextView[]changes={ mutePostText, repostText, commentPrivatelyText,cancelRepeatText, copyPostText, hidePostText};
        switch (currentTheme) {
            case Theme.LIGHT:

                curveHolder.setBackground(getContext().getDrawable(R.drawable.bottom_design_light));
                // curveHolder.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                changeTextColors(changes,R.color.textColor);

                break;
            case Theme.DEEP:

                curveHolder.setBackground(getContext().getDrawable(R.drawable.bottom_design_dark));
                //curveHolder.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                changeTextColors(changes,R.color.whiteGreen);



                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        setTextColors(currentTheme);


    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


    void setUI() {
        if (share) {
            LinearLayout[] views = {hidepost,commentPrivately, cancelRepeat, copyPost, mutePost,};
            setVisibility(views);

        } else {
            int postType = post.getPostType();
            switch (postType) {
                case PostTypes
                        .SINGLE_POST:
                case PostTypes.MERGED_POST:
                    LinearLayout[] views = {cancelRepeat, copyPost};

                    setVisibility(views);
                    break;
                case PostTypes.TEXT:
                    String text = "Copy text";
                    copyPostText.setText(text);
                    views = new LinearLayout[]{cancelRepeat};

                    setVisibility(views);
                    break;
                case PostTypes.VIDEO_POST:
                    text = "Copy link";
                    copyPostText.setText(text);
                    views = new LinearLayout[]{cancelRepeat};

                    setVisibility(views);
                    break;

            }
        }
    }

    private void userMuteState(){
        users.child(new UserInformation(getContext()).getMainUserID()).child("people").child(post.getFromUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            ContactPost contactPost=dataSnapshot.getValue(ContactPost.class);
                            ChoiceUser choiceUser=dataSnapshot.getValue(ChoiceUser.class);
                            if(contactPost.isMute()){
                                String text="unmute "+choiceUser.getUserContactName();
                                mutePostText.setText(text);


                            }else {

                                String text="mute "+choiceUser.getUserContactName();
                                mutePostText.setText(text);
                            }
                        }else {
                            users.child(post.getOwnerID())
                                    .addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()) {
                                                        UserDetail detail = dataSnapshot.getValue(UserDetail.class);

                                                        String text = "mute " + detail.getUsername();
                                                        mutePostText.setText(text);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            }
                                    );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                       mutePostText.setText("Mute ");
                    }
                });
    }
    private void setVisibility(LinearLayout[] view) {
        for (LinearLayout v : view) {
            v.setVisibility(View.GONE);
        }
    }

    void clickListeners() {
        mutePost.setOnClickListener(v -> {
            listener.onMutePost();
        });
        cancelRepeat.setOnClickListener(v -> {
            listener.onCancelRepeat();
        });
        repost.setOnClickListener(v -> {
            listener.onRepost();
        });
        copyPost.setOnClickListener(v -> {
            listener.onCopyText();
        });
        hidepost.setOnClickListener(v -> {
            listener.onHidePost();
        });
        commentPrivately.setOnClickListener(v->{
            listener.commentPrivately();
        });
    }

}
