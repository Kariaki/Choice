package com.kariaki.choice.ui.Profiles;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;

public class EditProfileOptions extends BottomSheetDialogFragment {


    private LinearLayout changeProfilePicture,EditDisplayName,EditAbout;
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_option, null);
        viewByIDs(contentView);
        onClickEvents();
        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void viewByIDs(View view){
        changeProfilePicture=view.findViewById(R.id.changeProfilePicture);
        EditDisplayName=view.findViewById(R.id.editDisplayName);
        EditAbout=view.findViewById(R.id.editAbout);
    }
    private void onClickEvents(){
        changeProfilePicture.setOnClickListener(v->{

        });
        EditDisplayName.setOnClickListener(v->{
            editDisplayName(v);
        });
        EditAbout.setOnClickListener(v->{
            editAbout(v);
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void editDisplayName(View view){

        Intent intent=new Intent(getActivity(),EditProfilePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        String phone=new UserInformation(getContext()).getMainUserID();
        intent.putExtra("phone",phone);
        startActivity(intent);
    }

    private final int PICK_IMAGE = 101;
    public void editAbout(View view){

        Intent intent=new Intent(getActivity(),EditAboutPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        String phone=new UserInformation(getContext()).getMainUserID();
        intent.putExtra("phone",phone);
        startActivity(intent);
    }

    private void pickImage(){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select pictures"), PICK_IMAGE);
    }

}
