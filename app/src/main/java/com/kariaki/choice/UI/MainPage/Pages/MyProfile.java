package com.kariaki.choice.UI.MainPage.Pages;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kariaki.choice.Model.Database.ChoiceViewModel;
import com.kariaki.choice.Model.Database.Entities.ChoiceUser;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.Profiles.EditProfileOptions;
import com.kariaki.choice.UI.Profiles.SettingsPage;
import com.kariaki.choice.UI.Profiles.ViewProfilePicture;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfile newInstance(String param1, String param2) {
        MyProfile fragment = new MyProfile();
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

    private TextView account_phone;
    private EmojiTextView displayName, accountHandle, about_me;
    private ImageView settings;
    private ChoiceViewModel choiceViewModel;
    private CircleImageView profilePicture;
    private Button editProfiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_profile, container, false);
        viewByID(view);
        choiceViewModel = ChoiceViewModel.getInstance(getActivity().getApplication());
        choiceViewModel.getChoiceUser(new UserInformation(getContext()).getPhoneNumber())
                .observe(getViewLifecycleOwner(), new Observer<List<ChoiceUser>>() {
                    @Override
                    public void onChanged(List<ChoiceUser> choiceUsers) {
                        ChoiceUser choiceUser = choiceUsers.get(0);
                        user.add(choiceUser);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Glide.with(getActivity().getApplicationContext()).load(choiceUser.getUserImageUrl()).into(profilePicture);
                                displayName.setText(choiceUser.getUserContactName());
                                accountHandle.setText("@" + choiceUser.getUserName());
                                about_me.setText(choiceUser.getUserAboutMe());
                                account_phone.setText(choiceUser.getUserPhoneNumber());
                            }
                        });
                    }
                });
        onClicks();


        return view;
    }

    List<ChoiceUser> user = new ArrayList<>();

    private void onClicks() {
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewProfilePicture.class);
                intent.putExtra("profileImage", user.get(0).getUserImageUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        editProfiles.setOnClickListener(v -> {

            EditProfileOptions editProfileOptions = new EditProfileOptions();
            editProfileOptions.show(getFragmentManager(), null);
        });
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void viewByID(View view) {
        account_phone = view.findViewById(R.id.account_phone);
        displayName = view.findViewById(R.id.displayName);
        editProfiles = view.findViewById(R.id.editProfiles);
        profilePicture = view.findViewById(R.id.profilePicture);
        settings = view.findViewById(R.id.settings);
        accountHandle = view.findViewById(R.id.accountHandle);
        about_me = view.findViewById(R.id.about_me);
    }
}