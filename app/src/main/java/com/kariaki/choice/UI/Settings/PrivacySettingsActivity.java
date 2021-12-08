package com.kariaki.choice.UI.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.Model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.UI.DialogBox.ChoiceDialogBoxMultiChoice;
import com.kariaki.choice.UI.Settings.Entities.PrivacySettings;
import com.kariaki.choice.UI.util.Theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivacySettingsActivity extends AppCompatActivity {
    AppCompatCheckBox privacySettingsMessageRequestCheckBox, privacySearchCheckBox;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    String phoneNumber;
    RelativeLayout privacySettingsRootView;
    TextView otherUserLastSeen;
    boolean connected = false;
    ChoiceDialogBoxMultiChoice multiChoice;
    int color=R.color.textHeaderColor;
    List<String> message=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        privacySettingsMessageRequestCheckBox = findViewById(R.id.privacySettingsMessageRequestCheckBox);
        privacySearchCheckBox = findViewById(R.id.privacySearchCheckBox);
        privacySettingsRootView=findViewById(R.id.privacySettingsRootView);
        multiChoice=new ChoiceDialogBoxMultiChoice();
        multiChoice.setColor(color);
        multiChoice.setTittle("Show last seen to");
        message= Arrays.asList(multiplaChoice);
        multiChoice.setText(message);


        otherUserLastSeen=findViewById(R.id.otherUserLastSeen);
        UserInformation information = UserInformation.getInstance(this);
        phoneNumber = information.getMainUserID();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }


        loadTheme();
        loadSettings();
        privacySearchCheckBox.setChecked(searchOnlyMyContacts);
        privacySettingsMessageRequestCheckBox.setChecked(allowDm);



    }

    String[] multiplaChoice = new String[]{"Every one", "My contacts only", "Nobody"};

    public void showDialog(View view) {


        multiChoice.setSelected(showLastSeen);
        multiChoice.setItemClickListener(new ChoiceDialogBoxMultiChoice.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showLastSeen=position;
            }

            @Override
            public void onPositiveButtonClick() {
                editSharedPreference(allowDm,showLastSeen,searchOnlyMyContacts);

                multiChoice.dismiss();
            }

            @Override
            public void onNegativeButtonClick() {
                multiChoice.dismiss();
            }
        });
        multiChoice.show(getSupportFragmentManager(),"tag");

    }

    public void checkReceiveMessageRequest(View view) {

        if(!connected){
            Toast.makeText(PrivacySettingsActivity.this, "Network connection not available", Toast.LENGTH_SHORT).show();
        }

        boolean isChecked = privacySettingsMessageRequestCheckBox.isChecked();

        if (isChecked) {
            privacySettingsMessageRequestCheckBox.setChecked(false);
            allowDm=false;
        } else {
            privacySettingsMessageRequestCheckBox.setChecked(true);
            allowDm=true;
        }

    }

    public void checkSearchOnlyMyContacts(View view) {

        if(!connected){
            Toast.makeText(PrivacySettingsActivity.this, "Network connection not available", Toast.LENGTH_SHORT).show();
        }
        boolean isChecked = privacySearchCheckBox.isChecked();

        if (isChecked) {
            privacySearchCheckBox.setChecked(false);
            searchOnlyMyContacts=false;
        } else {
            privacySearchCheckBox.setChecked(true);
            searchOnlyMyContacts=true;
        }
    }

    public void backPress(View view) {
        onBackPressed();
    }

    PrivacySettings privacySettings;

    public void editSharedPreference(boolean allowDm, int showLastSeenTo, boolean searchOnlyMyContacts) {
        SharedPreferences sharedPreferences = getSharedPreferences("privacy", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putInt("showLastSeenTo", showLastSeenTo);
        sharedPreferenceEditor.putBoolean("allowDM", allowDm);
        sharedPreferenceEditor.putBoolean("searchOnlyMyContacts", searchOnlyMyContacts);
        sharedPreferenceEditor.apply();
        privacySettings = new PrivacySettings(allowDm, showLastSeenTo, searchOnlyMyContacts);

    }

    int showLastSeen;
    boolean allowDm;
    boolean searchOnlyMyContacts;

    private void uploadSettings(){
        DatabaseReference privacy = databaseReference.child(phoneNumber).child("Privacy");
        privacy.setValue(privacySettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PrivacySettingsActivity.this, "Settings updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("privacy", MODE_PRIVATE);
        showLastSeen = sharedPreferences.getInt("showLastSeenTo", 0);

        searchOnlyMyContacts = sharedPreferences.getBoolean("searchOnlyMyContacts", false);
        allowDm=sharedPreferences.getBoolean("allowDM",true);
        privacySettings=new PrivacySettings(allowDm,showLastSeen,searchOnlyMyContacts);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editSharedPreference(allowDm,showLastSeen,searchOnlyMyContacts);

        if(connected) {
            uploadSettings();
        }
    }

    public void setTextColors(TextView[]text, int currentTheme){
        switch (currentTheme){
            case Theme.LIGHT:
                changeTextColors(text,R.color.textContext);

                privacySettingsRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text,R.color.whiteGreen);
                privacySettingsRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }
    private void loadTheme(){
        SharedPreferences sharedPreferences=getSharedPreferences("themes",MODE_PRIVATE);

        int currentTheme=sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView []textViews={otherUserLastSeen};
        setTextColors(textViews,currentTheme);

    }
    private void changeTextColors(TextView[]textViews,int color){
        for(TextView text:textViews){
            text.setTextColor(getResources().getColor(color));
        }

    }
}
