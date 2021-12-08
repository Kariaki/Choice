package com.kariaki.choice.ui.MainPage.BottomFragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.contactsync.ContactData;
import com.kariaki.choice.contactsync.ContactSync;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.Chat.ChannelTypes;
import com.kariaki.choice.ui.Chat.ChatPage;
import com.kariaki.choice.ui.GroupChat.CreateGroupChat;
import com.kariaki.choice.ui.MainPage.Adapters.ContactAndFriendsAdapter;
import com.kariaki.choice.ui.MainPage.Pages.Chat;
import com.kariaki.choice.ui.Settings.ChatSettingsActivity;
import com.kariaki.choice.ui.util.Theme;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class BottomContact extends BottomSheetDialogFragment {


    private RecyclerView Contactlist;
    private ContactAndFriendsAdapter adapter;
    private List<ChoiceUser> userDetailList = new ArrayList<>();
    private static final String LIST_OF_USERS = "users";
    private FirebaseDatabase firestoreDB = FirebaseDatabase.getInstance();
    private DatabaseReference userList = firestoreDB.getReference(LIST_OF_USERS);
    private DatabaseReference myContacts;
    private List<String> uniqueID = new ArrayList<>();
    private List<ContactData> contactData = new ArrayList<>();
    SharedPreferences preferences;
    private List<String> phoneNumbers = new ArrayList<>();
    private String MyPhoneNumber;
    private final String shareDefaul = "no user";
    private final String sharedKey = "userDetail";
    private List<String> newPhoneNumbers = new ArrayList<>();
    private List<String> contactName = new ArrayList<>();
    private TextView contactCount, yourContact;
    private RelativeLayout
            changeHolder, contactFriendsCancelProcess;
    ImageView contactFriendsChatSettings, contactFriendsNewGroupChat, contactFriendsGroupMessage;
    private ChoiceViewModel viewModel;
    private RelativeLayout contactFriendsProcessButton, curveHolder;
    private TextView createGroupChatText, broadCastText, chatSettingsText;
    private LinearLayout refresher;
    ProgressBar contactLoading;
    private TextView refreshText;

    EditText searchContacts;
    public static BottomContact getInstance() {

        return new BottomContact();

    }

    public Chat.OpenPageListener OnOpenPageListener;

    public void setOnOpenPageListener(Chat.OpenPageListener onOpenPageListener) {
        OnOpenPageListener = onOpenPageListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myContacts = userList.child(new UserInformation(getContext()).getMainUserID()).child("people");
    }

    ArrayList<String> markedUsersID = new ArrayList<>();

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {

        View contentView = View.inflate(getContext(), R.layout.bottom_contact, null);

        dialog.setContentView(contentView);
        refreshText = contentView.findViewById(R.id.refreshText);
        refresher = contentView.findViewById(R.id.refresher);
        contactLoading = contentView.findViewById(R.id.contactLoading);
        searchContacts=contentView.findViewById(R.id.searchContacts);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        viewModel = ChoiceViewModel.getInstance(getActivity().getApplication());

        contactData = ContactSync.getContacts(getContext());
        viewByIDs(contentView);



        preferences = getActivity().getSharedPreferences(sharedKey, MODE_PRIVATE);
        MyPhoneNumber = preferences.getString(sharedKey, shareDefaul);
        myContacts = userList.child(MyPhoneNumber).child("people");
        myContacts.keepSynced(true);

        adapter = new ContactAndFriendsAdapter();

        adapter.setParticularUser(userDetailList);

        adapter.setShowMarking(false);

        if (markedUsersID.size() == 0) {
            changeHolder.setVisibility(View.VISIBLE);
        } else {


        }


        adapter.setOnClickUser(new ContactAndFriendsAdapter.OnClickUser() {
            @Override
            public void OnclickUserListener(int position, ImageView marker, CircleImageView profile) {
                if (!adapter.isShowMarking()) {

                    String text = (userDetailList.get(position).getUserContactName());
                    String url=userDetailList.get(position).getUserImageUrl();
                    String phone=userDetailList.get(position).getUserPhoneNumber();
                     dismiss();


                    Intent intent = new Intent(getContext(), ChatPage.class);

                    intent.putExtra("phone number", phone);

                    intent.putExtra("channelType", ChannelTypes.ONE_TO_ONE_CHAT);
                    intent.putExtra("name", text);
                    intent.putExtra("url",url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(intent);

                } else {
                    String userID = userDetailList.get(position).getUserPhoneNumber();

                    if (!markedUsersID.contains(userID)) {
                        markedUsersID.add(userID);
                        String text = String.valueOf(markedUsersID.size()) + " selected";
                        contactCount.setText("");
                        yourContact.setText(text);
                        marker.setVisibility(View.VISIBLE);
                    } else {
                        markedUsersID.remove(userID);
                        contactCount.setText("");
                        String text = String.valueOf(markedUsersID.size()) + " selected";
                        yourContact.setText(text);
                        marker.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void OnCheckChangeListener(int position) {
                String userID = userDetailList.get(position).getUserPhoneNumber();

            }
        });

        onClicks();

        loadTheme();

        Contactlist = contentView.findViewById(R.id.Contactlist);
        Contactlist.setLayoutManager(new LinearLayoutManager(getContext()));
        Contactlist.setHasFixedSize(true);
        Contactlist.setAdapter(adapter);


        // ContactFriendInstance contactFriendInstance=ContactFriendInstance.getInstance(getActivity());
        adapter.setParticularUser(userDetailList);
        // userDetailList=contactFriendInstance.getChoiceUsers();
        contactCount.setText(String.valueOf(userDetailList.size()));


        contactFriendsCancelProcess.setOnClickListener(v -> {
            cancelProcess();
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadContacts();
            }
        });

        searchContacts();

    }

    private void searchContacts(){
        myContacts = FirebaseDatabase.getInstance().getReference("users").child(new UserInformation(getContext()).getMainUserID()).child("people");
        myContacts.keepSynced(true);
        searchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String searchWord=s.toString();
                if(searchWord.isEmpty()){
                    loadContacts();

                }else {
                    userDetailList.clear();
                    phones.clear();
                    adapter.notifyDataSetChanged();
                    myContacts.orderByChild("userContactName")
                            .startAt(searchWord)
                            .endAt(s + "\uf8ff")

                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.exists()) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            ChoiceUser user = snapshot1.getValue(ChoiceUser.class);
                                            if (!phones.contains(user.getUserPhoneNumber())) {
                                                userDetailList.add(user);
                                                adapter.notifyDataSetChanged();
                                                phones.add(user.getUserPhoneNumber());
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    List<String> phones = new ArrayList<>();

    private void loadContacts() {
        myContacts = FirebaseDatabase.getInstance().getReference("users").child(new UserInformation(getContext()).getMainUserID()).child("people");
        myContacts.keepSynced(true);

        myContacts
                .orderByChild("userContactName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    contactCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    for (DataSnapshot contacts : snapshot.getChildren()) {
                        ChoiceUser user = contacts.getValue(ChoiceUser.class);
                        if (!phones.contains(user.getUserPhoneNumber())) {
                            userDetailList.add(user);
                            adapter.notifyDataSetChanged();
                            phones.add(user.getUserPhoneNumber());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void viewByIDs(View contentView) {
        contactCount = contentView.findViewById(R.id.contactCount);
        contactFriendsGroupMessage = contentView.findViewById(R.id.contactFriendsGroupMessage);
        contactFriendsProcessButton = contentView.findViewById(R.id.contactFriendsProcessButton);
        contactFriendsCancelProcess = contentView.findViewById(R.id.contactFriendsCancelProcess);
        changeHolder = contentView.findViewById(R.id.changeHolder);
        chatSettingsText = contentView.findViewById(R.id.chatSettingsText);
        createGroupChatText = contentView.findViewById(R.id.createGroupChatText);
        curveHolder = contentView.findViewById(R.id.curveHolder);
        broadCastText = contentView.findViewById(R.id.broadCastText);
        yourContact = contentView.findViewById(R.id.yourContact);
        contactFriendsNewGroupChat = contentView.findViewById(R.id.contactFriendsNewGroupChat);
        contactFriendsChatSettings = contentView.findViewById(R.id.contactFriendsChatSettings);
    }

    String Contacts = "Contacts";

    @Override
    public void onStart() {
        super.onStart();

        if (isHidden()) {
            yourContact.setText(Contacts);
        }


    }

    public void cancelProcess() {
        changeHolder.setVisibility(View.VISIBLE);
        contactCount.setText(String.valueOf(adapter.getItemCount()));
        contactFriendsCancelProcess.setVisibility(View.GONE);
        contactFriendsProcessButton.setVisibility(View.GONE);
        markedUsersID.clear();
        Contactlist.setVisibility(View.VISIBLE);
        adapter.setParticularUser(userDetailList);
        adapter.setShowMarking(false);
        yourContact.setText(Contacts);
        contactCount.setText(String.valueOf(adapter.getItemCount()));
        Contactlist.setAdapter(adapter);
    }

    int processType = -1;
    private static final int CREATE_GROUP_CHAT = 11;
    private static final int CREATE_BROADCAST = 12;
    private static final int ADD_NEW_CONTACT = 13;

    public void onClicks() {

        contactFriendsChatSettings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChatSettingsActivity.class));
        });

        contactFriendsNewGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processType = CREATE_GROUP_CHAT;
                selectionOperation();
            }
        });

        contactFriendsGroupMessage.setOnClickListener(v -> {
            processType = CREATE_BROADCAST;
            selectionOperation();
        });

        contactFriendsProcessButton.setOnClickListener(v -> {
            finishProcess();
        });

    }

    //perform selection operation for creating of group chats and sending message broadcast

    public void selectionOperation() {
        changeHolder.setVisibility(View.GONE);
        yourContact.setText("selected");
        contactFriendsCancelProcess.setVisibility(View.VISIBLE);
        contactFriendsProcessButton.setVisibility(View.VISIBLE);
        contactFriendsProcessButton.setVisibility(View.VISIBLE);
        //dismiss();
        //show(getActivity().getSupportFragmentManager(),"Bottom Contacts");
        adapter.setParticularUser(userDetailList);
        adapter.setShowMarking(true);
        contactCount.setText(String.valueOf(markedUsersID.size()));
        Contactlist.setAdapter(adapter);

    }

    public void finishProcess() {

        if (processType != -1) {

            switch (processType) {

                case CREATE_GROUP_CHAT:
                    if (markedUsersID.size() > 1) {

                        Intent groupChatIntent = new Intent(getActivity(), CreateGroupChat.class);
                        groupChatIntent.putExtra("selectedUsersList", markedUsersID);
                        startActivity(groupChatIntent);

                    } else {

                        Toast.makeText(getActivity()
                                , "members must be at least 3 persons", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case CREATE_BROADCAST:
                    /* if (markedUsersID.size() > 1) {



                        Intent groupChatIntent = new Intent(getActivity(), BroadCastMessage.class);

                        groupChatIntent.putExtra("selectedUsersList", markedUsersID);


                        startActivity(groupChatIntent);

                    } else {

                        Toast.makeText(getActivity()
                                , "members must be at least 3 persons", Toast.LENGTH_SHORT).show();

                    }

                         */

                    break;
            }
            markedUsersID.clear();
            dismiss();

        }

    }


    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_curve));

                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                curveHolder.setBackground(getResources().getDrawable(R.drawable.bottom_curve_dark_mode));

                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {createGroupChatText, broadCastText, chatSettingsText, yourContact
        };
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        adapter.setTextColor(color);
        if (textViews != null)
            for (TextView text : textViews) {
                text.setTextColor(getResources().getColor(color));
            }

    }


}
