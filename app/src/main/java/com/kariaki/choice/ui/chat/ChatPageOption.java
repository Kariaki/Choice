package com.kariaki.choice.ui.Chat;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.GroupChatInformation;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.DialogBox.ChoiceDialogBox;
import com.kariaki.choice.ui.util.Theme;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ChatPageOption extends BottomSheetDialogFragment {

    boolean GroupChat;

    public boolean isGroupChat() {
        return GroupChat;
    }

    int themes;

    public void setThemes(int themes) {
        this.themes = themes;
    }

    public int getThemes() {

        return themes;
    }

    private String chatID;

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public interface leaveGroupClickListener {
        void leaveGroupClick();
    }

    private leaveGroupClickListener leaveGroupClickListener;

    public void setLeaveGroupClickListener(ChatPageOption.leaveGroupClickListener leaveGroupClickListener) {
        this.leaveGroupClickListener = leaveGroupClickListener;
    }

    public void setGroupChat(boolean groupChat) {
        GroupChat = groupChat;
    }

    String myPhone;
    TextView nameText;
    DatabaseReference thisUser;

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.chat_page_option, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        myPhone = new UserInformation(getContext()).getPhoneNumber();
        thisUser = FirebaseDatabase.getInstance().getReference("users").child(myPhone).child("chats").child(chatID);
        viewByID(contentView);
        nameText = contentView.findViewById(R.id.nameText);
        if (isGroupChat()) {

            block.setVisibility(View.GONE);
            getGroupName();
        } else {
            leaveGroup.setVisibility(View.GONE);
            getUserName();
        }
        UIState();
        onClickListeners();
        loadTheme();
    }


    LinearLayout mainRoot;

    private void viewByID(View view) {
        mainRoot = view.findViewById(R.id.mainRoot);
        block = view.findViewById(R.id.block);
        mute = view.findViewById(R.id.mute);
        blockText = view.findViewById(R.id.blockText);
        muteText = view.findViewById(R.id.muteText);
        leaveGroupText = view.findViewById(R.id.leaveGroupText);
        leaveGroup = view.findViewById(R.id.leaveGroup);
    }

    public void setTextColors(TextView[] text, int currentTheme) {

        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);


                //   mainRoot.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_light));

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                //mainRoot.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                mainRoot.setBackground(getContext().getDrawable(R.drawable.bottom_design_dark));


                break;

        }
    }

    LinearLayout block, mute, leaveGroup;
    TextView blockText, muteText, leaveGroupText;

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);
        TextView[] textViews = {muteText, blockText, leaveGroupText};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


    private void getUserName() {
        DatabaseReference findUserName = FirebaseDatabase.getInstance().getReference("users")
                .child(myPhone).child("people").child(chatID);
        findUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChoiceUser choiceUser = snapshot.getValue(ChoiceUser.class);
                    nameText.setText(choiceUser.getUserContactName());
                } else {
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(chatID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        UserDetail detail = snapshot.getValue(UserDetail.class);
                                        nameText.setText(detail.getDisplayName());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupName() {
        DatabaseReference groupChat = FirebaseDatabase.getInstance().getReference("GroupChats")
                .child(chatID);
        groupChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GroupChatInformation chatInformation = snapshot.getValue(GroupChatInformation.class);
                    nameText.setText(chatInformation.getGroupChatName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void muteUserClick() {
        thisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Channels channels = snapshot.getValue(Channels.class);
                    if (channels.isMute()) {
                        DatabaseReference chat = snapshot.getRef();
                        Map<String, Object> update = new HashMap<>();
                        update.put("mute", !channels.isMute());
                        chat.updateChildren(update);

                        String mute = "Mute " + nameText.getText().toString();
                        muteText.setText(mute);
                        dismiss();
                        String toast = "You've unmuted " + nameText.getText().toString();
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

                    } else {
                        if (isGroupChat()) {

                            String text = "Are you want to mute this Group? You will no longer receive notifications from this chat";
                            muteDialog(channels.isMute(), snapshot, text);
                        } else {
                            String text = "Are you want to mute this person? You will no longer receive notifications from this chat";
                            muteDialog(channels.isMute(), snapshot, text);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void muteDialog(boolean ismute, DataSnapshot snapshot, String text) {
        ChoiceDialogBox choiceDialogBox = new ChoiceDialogBox();
        choiceDialogBox.setTittle("Mute");
        choiceDialogBox.setMessage(text);
        choiceDialogBox.setListeners(new ChoiceDialogBox.dialogButtons() {
            @Override
            public void onClickPositiveButton() {
                DatabaseReference chat = snapshot.getRef();
                Map<String, Object> update = new HashMap<>();
                update.put("mute", !ismute);

                chat.updateChildren(update);
                muteText.setText("Unmute");
                choiceDialogBox.dismiss();
            }

            @Override
            public void onClickNegativeButton() {
                choiceDialogBox.dismiss();
            }
        });
        choiceDialogBox.show(getFragmentManager(), null);

    }


    private void UIState() {
        thisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Channels channels = snapshot.getValue(Channels.class);
                    String unmute = "Unmute " + nameText.getText().toString();
                    String unblcok = "Unblock " + nameText.getText().toString();
                    String mute = "Mute " + nameText.getText().toString();
                    String block = "Block " + nameText.getText().toString();
                    if (channels.isMute()) {
                        muteText.setText(unmute);
                    } else {
                        muteText.setText(mute);
                    }
                    if (channels.isBlockUser()) {
                        blockText.setText(unblcok);
                    } else {
                        blockText.setText(block);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blockUserClick() {
        thisUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Channels channels = snapshot.getValue(Channels.class);
                    if (channels.isMute()) {
                        DatabaseReference chat = snapshot.getRef();
                        Map<String, Object> update = new HashMap<>();
                        update.put("blockUser", !channels.isMute());
                        chat.updateChildren(update);

                        String mute = "Block" + nameText.getText().toString();
                        muteText.setText(mute);
                        dismiss();
                        String toast = "You've unmuted " + nameText.getText().toString();
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

                    } else {
                        String text = "Are you want to block this person? You will no longer receive messages from this chat";
                        muteDialog(channels.isMute(), snapshot, text);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void blockUserDialog(String text, DataSnapshot snapshot, boolean isBlock) {
        ChoiceDialogBox choiceDialogBox = new ChoiceDialogBox();
        choiceDialogBox.setTittle("Mute");
        choiceDialogBox.setMessage(text);
        choiceDialogBox.setListeners(new ChoiceDialogBox.dialogButtons() {
            @Override
            public void onClickPositiveButton() {
                DatabaseReference chat = snapshot.getRef();
                Map<String, Object> update = new HashMap<>();
                update.put("blockUser", !isBlock);
                chat.updateChildren(update);
                muteText.setText("UnBlock");
                choiceDialogBox.dismiss();
            }

            @Override
            public void onClickNegativeButton() {
                choiceDialogBox.dismiss();
            }
        });
        choiceDialogBox.show(getFragmentManager(), null);

    }

    private void onClickListeners() {
        mute.setOnClickListener(v -> {
            muteUserClick();
        });
        block.setOnClickListener(v -> {
            blockUserClick();
        });
        leaveGroup.setOnClickListener(v -> {
            leaveGroupClickListener.leaveGroupClick();
        });
    }

}
