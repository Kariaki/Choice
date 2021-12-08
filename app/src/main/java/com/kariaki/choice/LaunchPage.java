package com.kariaki.choice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kariaki.choice.UI.RegistrationAndLogin.RegisterUser;
import com.kariaki.choice.UI.util.Theme;

public class LaunchPage extends AppCompatActivity {

    ImageView logo;
    Handler handler;

    private final int RC_SIGN_IN = 0;

    private final String shareDefaul = "no user";
    private final String sharedKey = "userDetail";
    private static final String LIST_OF_USERS = "users";
    SharedPreferences preferences;
    private DatabaseReference userCollection = FirebaseDatabase.getInstance().getReference(LIST_OF_USERS);
    private FirebaseUser user;
    private DatabaseReference documentReference;
    private static String CURRENTUSER;



    private RelativeLayout rootView;
    private TextView choiceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);




        //startActivity(new Intent(this, ChoiceCamera.class));



        handler = new Handler();
        logo = findViewById(R.id.logo);
        choiceName = findViewById(R.id.choiceName);
        rootView = findViewById(R.id.rootView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadTheme();
        if (user != null) {
            CURRENTUSER = user.getPhoneNumber();
        }

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        boolean firstUser = preferences.getBoolean("firstUser", true);
        editor = preferences.edit();

        if (firstUser) {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                            .setTheme(R.style.MyThemeNoActionBar)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);



        } else {

            openHomePage();
            /*
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 200);

             */

        }




}

    SharedPreferences.Editor editor;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // editor=preferences.edit();
        if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            finish();
        } else if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // editor.putBoolean("firstUser",false);
            // editor.apply();
            final SharedPreferences.Editor sharedEditor = preferences.edit();
            FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
            String phone = newUser.getPhoneNumber();

            sharedEditor.putString(sharedKey, newUser.getPhoneNumber());
            Intent startRegisteration = new Intent(LaunchPage.this, RegisterUser.class);
            userCollection.child(phone)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                Intent intent = new Intent(LaunchPage.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                editor.putBoolean("firstUser", false);
                                editor.putString("phone number", phone);
                                editor.apply();

                                startActivityForResult(intent, 101);

                            } else {

                                startRegisteration.putExtra("phone number", phone);
                                startRegisteration.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivityForResult(startRegisteration, 1000);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        } else if (requestCode == RC_SIGN_IN && resultCode == RESULT_CANCELED) {

            finish();
        }
        if (requestCode == 1000 && resultCode == RESULT_CANCELED) {
            finish();
        } else if (requestCode == 2 && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    public void openHomePage() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo,
                Objects.requireNonNull(ViewCompat.getTransitionName(logo)));
        startActivityForResult(intent, 101);


    }


    public void setTextColors(TextView[] text, int currentTheme) {

        /*
        if(rootView!=null) {
            rootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

            switch (currentTheme) {
                case Theme.LIGHT:

                    getWindow().setBackgroundDrawableResource(R.color.whiteGreen);
                    choiceName.setTextColor(getResources().getColor(R.color.textHeaderColor));

                    break;
                case Theme.DEEP:

                    rootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                    getWindow().setBackgroundDrawableResource(R.color.darkGreen);
                    choiceName.setTextColor(getResources().getColor(R.color.whiteGreen));


                    break;

            }

        }


         */
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {};
        setTextColors(textViews, currentTheme);

    }

}
