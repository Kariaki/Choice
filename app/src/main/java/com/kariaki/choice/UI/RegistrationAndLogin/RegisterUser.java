package com.kariaki.choice.UI.RegistrationAndLogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.MainActivity;
import com.kariaki.choice.Model.UserDetail;
import com.kariaki.choice.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterUser extends AppCompatActivity {

    EditText chooseName,chooseDisplayName;
    RelativeLayout changePhoto;
    TextView errorText;
    int PICK_IMAGE = 101;
    Button finishProcess;
    CircleImageView newProfileImage;
    String phoneNumber;
    SharedPreferences sharedPreferences;
    ImageView previewImage;
    ProgressBar progress;
    TextView limitCheck;
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_register_user);

        chooseName = findViewById(R.id.chooseName);
        errorText = findViewById(R.id.errorText);
        previewImage = findViewById(R.id.previewImage);
        limitCheck = findViewById(R.id.limitCheck);
        finishProcess = findViewById(R.id.finishProcess);
        chooseDisplayName=findViewById(R.id.chooseDisplayName);
        progress = findViewById(R.id.progress);
        newProfileImage = findViewById(R.id.newProfileImage);
        changePhoto = findViewById(R.id.holder);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone number");

        changePhoto.setOnClickListener(v -> {
            if (!allPermissionGranted()) {
                requestNeededPermision();

            } else {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select pictures"), PICK_IMAGE);
            }

        });

        chooseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    finishProcess.setEnabled(true);

                    int size = text.length();
                    if (text.length() >= 3) {
                        searchIfUserNameIsTaken(text);
                    } else {

                        errorText.setText("Name is too short");
                    }
                    String lengthCheck = size + "/" + 15;
                    limitCheck.setText(lengthCheck);

                } else {
                    finishProcess.setEnabled(false);
                    limitCheck.setText("0/15");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    finishProcess.setEnabled(true);

                    int size = text.length();
                    if (text.length() >= 3) {
                        searchIfUserNameIsTaken(text);
                    } else {
                        errorText.setText("Name is too short");
                    }
                    String lengthCheck = size + "/" + 15;
                    limitCheck.setText(lengthCheck);

                } else {
                    finishProcess.setEnabled(false);
                    limitCheck.setText("0/15");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                if (!name.isEmpty() && name.length() >= 3) {


                    if (checkText(name)) {

                        searchIfUserNameIsTaken(name);
                    } else {
                        finishProcess.setEnabled(false);
                    }
                } else {

                    finishProcess.setEnabled(false);
                    errorText.setText("Name is too short");
                }
            }

        });


    }

    private boolean checkText(String text) {

        if (!text.isEmpty()) {
            char[] arrayOfText = text.toCharArray();
            char first = arrayOfText[0];
            boolean output = false;
            if (Character.isLetter(first)) {
                for (char a : arrayOfText) {
                    if (!Character.isLetterOrDigit(a)) {
                        output = false;
                        break;
                    } else {
                        output = true;
                    }
                }
            }


            return output;
        } else {
            return false;
        }
    }

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    private void searchIfUserNameIsTaken(String s) {

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);


                users.orderByChild("username")
                        .equalTo(s)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String text = "name has been taken ";
                                if (dataSnapshot.exists()) {
                                    text = "name has been taken ";
                                    finishProcess.setEnabled(false);
                                    errorText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                    errorText.setText(text);
                                } else {
                                    finishProcess.setEnabled(true);
                                    text = "Available";
                                    errorText.setTextColor(getResources().getColor(android.R.color.background_dark));
                                    errorText.setText(text);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
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
                previewImage.setVisibility(View.GONE);
                Glide.with(this).load(resultUri).into(newProfileImage);

                uri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == 101 && resultCode == RESULT_CANCELED || requestCode == 101 && resultCode == RESULT_OK) {
            finish();
        }
    }


    private void uploadProfilePicture(Uri uri) {

        StorageReference newProfilePicture = FirebaseStorage.getInstance().getReference("ProfilePictures")
                .child(System.currentTimeMillis() + "." + getFileExtension(uri));
        newProfileImage.setVisibility(View.VISIBLE);

        newProfilePicture.putFile(uri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        newProfileImage.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.VISIBLE);
                    }
                }).addOnSuccessListener(taskSnapshot -> {
            newProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String fileURI = uri.toString();
                    progress.setVisibility(View.GONE);
                    String about = "Just joined Choice";
                    String userName = chooseName.getText().toString();
                    UserDetail detail = new UserDetail(userName, phoneNumber, about, fileURI, chooseDisplayName.getText().toString());
                    users.child(phoneNumber).setValue(detail);

                    Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        });


    }

    SharedPreferences.Editor editor;

    public void finishregisteration(View view) {


       // errorText.setText(chooseName.getText().toString());

        if(!chooseName.getText().toString().isEmpty()&&chooseName.getText().toString().length()>=3) {
            finishProcess.setEnabled(false);
            if (uri == null) {
                String str = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQUf9QPA3pe7BY91OIKO_4xqJfLRsPWJyHDRQ&usqp=CAU";

                editor.putBoolean("firstUser", false);
                editor.putString("phone number", phoneNumber);
                editor.apply();
                noPicture(str);
            } else {

                editor.putBoolean("firstUser", false);
                editor.putString("phone number", phoneNumber);
                editor.apply();
                uploadProfilePicture(uri);
            }
        }

    }

    public void noPicture(String uri) {

        String about = "Just joined Choice";
        String userName = chooseName.getText().toString();
        UserDetail detail = new UserDetail(userName, phoneNumber, about, uri, chooseDisplayName.getText().toString());
        users.child(phoneNumber).setValue(detail);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 101);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    String[] allRequiredPermisions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};

    private boolean allPermissionGranted() {

        for (String permission : allRequiredPermisions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestNeededPermision() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, allRequiredPermisions, PackageManager.PERMISSION_GRANTED);

            //

        }


    }
}
