package com.kariaki.choice.ui.profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.settings.AccountSettings;
import com.kariaki.choice.ui.settings.ChatSettingsActivity;
import com.kariaki.choice.ui.settings.NotificationSettings;
import com.kariaki.choice.ui.settings.PostSettings;
import com.kariaki.choice.ui.settings.PrivacySettingsActivity;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Theme;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsPage extends AppCompatActivity {

    private static final String LIST_OF_USERS = "users";
    DatabaseReference userDetail = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
    private CircleImageView profilePicture;
    private Toolbar profileToolbar;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
    String myPhoneNumber;
    ImageView profilePageBackgroundImage;
    RelativeLayout profilePageRootView;
    UserDetail mainUserDetails;
    EditFromBottom editFromBottom;

    TextView privacySettingsText, postSettingsText, chatSettingsText, accountSettingsText;
    TextView[] texttoChange;
    Switch darkModeSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new IosEmojiProvider());
        setContentView(R.layout.activity_profile);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);


        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);
        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        switch (currentTheme){
            case Theme.LIGHT:
                darkModeSwitch.setChecked(false);
                break;
                case Theme.DEEP:
                    darkModeSwitch.setChecked(true);
                    break;
        }


        darkModeSwitch.setOnClickListener(v -> {
            setTheme();
        });


        firstCall();

    }



    TextView notificationSettingsText, darkModeText;

    private void firstCall() {
        profilePageRootView = findViewById(R.id.profilePageRootView);
        privacySettingsText = findViewById(R.id.privacySettingsText);
        darkModeText = findViewById(R.id.darkModeText);
        postSettingsText = findViewById(R.id.postSettingsText);
        chatSettingsText = findViewById(R.id.chatSettingsText);
        notificationSettingsText = findViewById(R.id.notificationSettingsText);
        profilePicture = findViewById(R.id.profilePicture);
        accountSettingsText = findViewById(R.id.accountSettingsText);
        editFromBottom = new EditFromBottom();
        texttoChange = new TextView[]
                {privacySettingsText, postSettingsText, chatSettingsText, darkModeText,
                        accountSettingsText, notificationSettingsText};
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.LIGHT);

        setTextColors(texttoChange, currentTheme);


        UserInformation information = new UserInformation(this);
        String phone = information.getMainUserID();
        myPhoneNumber = phone;

        profilePicture = findViewById(R.id.profilePicture);

        editFromBottom.setClickListeners(new EditFromBottom.onClickListeners() {
            @Override
            public void onClickConfirm(String text) {
                Map<String, Object> about = new HashMap<>();
                about.put("about", text);
                userDetail.child(phone).updateChildren(about)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsPage.this, "Failed to update about ", Toast.LENGTH_SHORT).show();
                            }
                        });
                editFromBottom.dismiss();
            }

            @Override
            public void onClickCancel() {
                editFromBottom.dismiss();
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(60)
                    .start(this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(profilePicture);
                uploadProfilePicture(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadProfilePicture(Uri uri) {

        StorageReference newProfilePicture = FirebaseStorage.getInstance().getReference("ProfilePictures")
                .child(System.currentTimeMillis() + "." + Functions.fileExtension(uri.getPath()));
       // profileProgress.setVisibility(View.VISIBLE);

        newProfilePicture.putFile(uri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                       // profileProgress.setVisibility(View.VISIBLE);
                    }
                }).addOnSuccessListener(taskSnapshot -> {
            newProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String fileURI = uri.toString();
                    Map<String, Object> update = new HashMap<>();
                    update.put("profileURL", fileURI);
                    userDetail.child(myPhoneNumber).updateChildren(update)

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // profileProgress.setVisibility(View.GONE);
                                    Toast.makeText(SettingsPage.this, "Profile image changed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SettingsPage.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
        });


    }

    public void openChatSettings(View view) {
        Intent intent = (new Intent(this, ChatSettingsActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    public void openPrivacySettings(View view) {
        Intent intent = (new Intent(this, PrivacySettingsActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openPostSettings(View view) {

        Intent intent = (new Intent(this, PostSettings.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void backPress(View view) {
        onBackPressed();
    }

    public void changeTheme(View view) {

        setTheme();
        if(darkModeSwitch.isChecked()) {
            darkModeSwitch.setChecked(false);
        }else {
            darkModeSwitch.setChecked(true);

        }

    }

    private void setTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        switch (currentTheme) {
            case Theme.LIGHT:
                currentTheme = Theme.DEEP;
               // darkModeSwitch.setChecked(false);
                break;
            case Theme.DEEP:
                currentTheme = Theme.LIGHT;
               // darkModeSwitch.setChecked(true);
                break;
        }
        editor.putInt("currentTheme", currentTheme);
        editor.apply();
        setTextColors(texttoChange, currentTheme);
    }

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textColor);

                profilePageRootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                profilePageRootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                break;

        }
    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void openNotificationSettings(View view) {

        Intent intent = (new Intent(this, NotificationSettings.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openAccountSettings(View view) {

        Intent intent = (new Intent(this, AccountSettings.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // socialActivityStats();
    }


}
