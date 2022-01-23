package com.kariaki.choice.ui.profiles;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.MyChatChannel;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.dialogbox.ChoiceDialogBox;
import com.kariaki.choice.ui.settings.entities.PrivacySettings;
import com.kariaki.choice.ui.settings.Settings;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Theme;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PartialProfile extends BottomSheetDialogFragment {

    private static final String LIST_OF_USERS = "users";
    DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
    static String USER_ID;

    public static PartialProfile getInstance(String userID) {
        USER_ID = userID;




        return new PartialProfile();

    }

    private boolean isFromChat = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface OnClickMessageBox {
        void OnClickMessage();
    }

    private OnClickMessageBox messageBox;

    public void setOnclickMessageBox(OnClickMessageBox messageBox) {
        this.messageBox = messageBox;
    }

    CircleImageView partialProfilePicture;
    TextView partialProfileUserName, partialProfileUserLastSeen, partialProfileUserOnLineStatus, profileHandleName;
    String profileUrl;
    EmojiTextView partialProfileAboutUser;

    RelativeLayout partialProfileRootView, openFullProfile;
    UserInformation information;
    List<ChoiceUser> inContact = new ArrayList<>();
    LinearLayout messageButton;
    DatabaseReference myChatWithThisPerson;
    DatabaseReference thisUserPrivacy;

    public void setFromChat(boolean fromChat) {
        isFromChat = fromChat;
    }

    public boolean isFromChat() {
        return isFromChat;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.partial_profile, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        viewByID(contentView);
        myChatWithThisPerson = FirebaseDatabase.getInstance().getReference("users").child(new UserInformation(getActivity()).getMainUserID())
                .child("chats").child(USER_ID);
        thisUserPrivacy = allUsers.child(USER_ID).child("Privacy");

        nameChoiceUser(USER_ID, partialProfileUserName, partialProfilePicture, partialProfileAboutUser);


        partialProfilePicture.setOnClickListener(v -> {
            openViewProfilePage();
        });

        messageButton.setOnClickListener(v -> {
            messageBox.OnClickMessage();
        });

        openFullProfile.setOnClickListener(v -> {
            openSocialProfile(USER_ID);

        });

        loadTheme();
        changeShowLastSeenIfNeeded();

    }

    TextView postsCount, repostCount, repeatsCount;

    public void openSocialProfile(String userID) {

        Intent intent = new Intent(getActivity(), SocialProfile.class);
        allUsers.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetail detail = snapshot.getValue(UserDetail.class);
                            intent.putExtra("user", detail);
                            intent.putExtra("from chat", isFromChat);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void viewByID(View contentView) {
        postsCount = contentView.findViewById(R.id.postsCount);
        repostCount = contentView.findViewById(R.id.repostCount);
        repeatsCount = contentView.findViewById(R.id.repeatsCount);
        //partialProfileBackgroundImage=contentView.findViewById(R.id.partialProfileBackgroundImage);
        partialProfilePicture = contentView.findViewById(R.id.partialProfilePicture);
        partialProfileUserName = contentView.findViewById(R.id.partialProfileUserName);
        openFullProfile = contentView.findViewById(R.id.openFullProfile);

        partialProfileUserLastSeen = contentView.findViewById(R.id.partialProfileUserLastSeen);
        partialProfileRootView = contentView.findViewById(R.id.partialProfileRootView);
        partialProfileAboutUser = contentView.findViewById(R.id.partialProfileAboutUser);
        profileHandleName = contentView.findViewById(R.id.profileHandleName);
        messageButton = contentView.findViewById(R.id.messageButton);

        partialProfileUserOnLineStatus = contentView.findViewById(R.id.partialProfileUserOnLineStatus);
    }

    private void showLastSeen() {
        Functions.onLineCheck(getContext(), USER_ID, partialProfileUserOnLineStatus, System.currentTimeMillis());

        String online = partialProfileUserOnLineStatus.getText().toString();
        String onlineKeyWord = "";
        if (online.equals("Online")) {
            onlineKeyWord = "Currently " + online;
            partialProfileUserOnLineStatus.setText(onlineKeyWord);
        } else {
            onlineKeyWord = "Last online " + online;
            partialProfileUserOnLineStatus.setText(onlineKeyWord);
        }
    }

    public void openViewProfilePage() {

        Intent intent = new Intent(getContext(), ViewProfilePicture.class);

        intent.putExtra("profileImage", profileUrl);

        startActivity(intent);

    }

    private void nameChoiceUser(String userPhoneNumber, TextView nameView,
                                CircleImageView userProfileView
            , TextView about) {
        ChoiceViewModel viewModel = ChoiceViewModel.getInstance(getActivity().getApplication());

        allUsers.child(new UserInformation(getContext()).getMainUserID()).child("people")
                .child(USER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ChoiceUser choiceUser = snapshot.getValue(ChoiceUser.class);
                            inContact.add(choiceUser);

                            ChoiceUser myContact = choiceUser;
                            nameView.setText(myContact.getUserContactName());
                            about.setText(myContact.getUserAboutMe());
                            loadUI();
                            loadPrivacySettingsForContact();
                            profileUrl = myContact.getUserImageUrl();
                            profileHandleName.setText(myContact.getUserName());

                            Glide.with(getContext().getApplicationContext()).load(myContact.getUserImageUrl()).thumbnail(0.3f).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.small_menu_button_background).into(userProfileView);

                        } else {
                            allUsers.child(USER_ID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                                nameView.setText(detail.getUsername());
                                                about.setText(detail.getAbout());
                                                profileUrl = detail.getProfileURL();
                                                Glide.with(getContext().getApplicationContext()).
                                                        load(detail.getProfileURL()).thumbnail(0.3f).into(partialProfilePicture);
                                                profileHandleName.setText(detail.getUsername());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            notInContactUIState();
                            loadPrivacySettingForUser();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void loadUI() {

        myChatWithThisPerson
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            MyChatChannel channel = dataSnapshot.getValue(MyChatChannel.class);
                            messageButton.setEnabled(true);

                        } else {

                            notInContactUIState();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void notInContactUIState() {

    }


    @Override
    public void onStart() {
        super.onStart();
        loadTheme();
    }


    private void lastSeenStateNotInContact(PrivacySettings privacySettings) {

        if (privacySettings.getShowLastSeenTo() == Settings.EVERYONE) {
            showLastSeen();
        } else {

            partialProfileUserOnLineStatus.setVisibility(View.INVISIBLE);
            partialProfileUserOnLineStatus.setVisibility(View.INVISIBLE);
        }
    }

    private void lastSeenStateInContact(PrivacySettings privacySettings) {

        if (privacySettings.getShowLastSeenTo() == Settings.NOBODY) {

            partialProfileUserOnLineStatus.setVisibility(View.INVISIBLE);
            partialProfileUserOnLineStatus.setVisibility(View.INVISIBLE);
        } else {
            showLastSeen();
        }
    }

    private void loadPrivacySettingForUser() {

        thisUserPrivacy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PrivacySettings settings = dataSnapshot.getValue(PrivacySettings.class);
                    lastSeenStateNotInContact(settings);
                    if (!settings.isAllowDmFromNoneContacts()) {
                        messageButton.setEnabled(false);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadPrivacySettingsForContact() {

        thisUserPrivacy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PrivacySettings settings = dataSnapshot.getValue(PrivacySettings.class);
                    lastSeenStateInContact(settings);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

    private void changeShowLastSeenIfNeeded() {

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (!connected) {

                    partialProfileUserLastSeen.setVisibility(View.INVISIBLE);
                    partialProfileUserOnLineStatus.setVisibility(View.INVISIBLE);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ChoiceDialogBox dialogBox;

    int color = R.color.textContext;

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);
                color = R.color.textColor;

                partialProfileRootView.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                color = R.color.whiteGreen;

                partialProfileRootView.setBackground(getResources().getDrawable(R.drawable.bottom_curve_second));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {postsCount, repostCount, repeatsCount, partialProfileUserLastSeen,
                partialProfileUserOnLineStatus, partialProfileAboutUser
        };
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


}
