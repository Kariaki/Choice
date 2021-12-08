package com.kariaki.choice.ui.Profiles;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Theme;

import java.util.HashMap;
import java.util.Map;

public class EditAboutPage extends AppCompatActivity {

    String phoneNumber;
    EditText editText;
    Button done;
    LinearLayout rootView;
    ImageView check;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_about_page);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        editText = findViewById(R.id.editText);
        name = findViewById(R.id.name);
        check=findViewById(R.id.check);
        rootView = findViewById(R.id.rootView);
        done = findViewById(R.id.done);
        loadTheme();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    done.setEnabled(false);
                }else {
                    done.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    done.setEnabled(false);
                }else {
                    done.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void finishEdit(View view) {
        String text = editText.getText().toString();
        if (!text.isEmpty()) {
            DatabaseReference myInfo = FirebaseDatabase.getInstance().getReference("users").child(phoneNumber);
            Map<String, Object> update = new HashMap<>();
            update.put("about", text);
            myInfo.updateChildren(update)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    check.setVisibility(View.VISIBLE);
                    editText.getText().clear();
                }
            });
            Toast.makeText(this, "About info has been changed", Toast.LENGTH_SHORT).show();
        }
        onBackPressed();

    }

    public void back(View view) {
        onBackPressed();

    }

    public void revealFab() {

        int cx = check.getWidth() / 2;
        int cy = check.getHeight() / 2;
        float finalRadium = (float) Math.hypot(cx, cy);
        Animator animator = ViewAnimationUtils.createCircularReveal(check, cx, cy, 0, finalRadium);
        check.setVisibility(View.VISIBLE);
        animator.start();

    }

    public void hideFab() {
        int cx = check.getWidth() / 2;
        int cy = check.getHeight() / 2;
        float initialRadius = (float) Math.hypot(cx, cy);
        Animator animator = ViewAnimationUtils.createCircularReveal(check, cx, cy, initialRadius, 0);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                check.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setTextColors(TextView[] text, int currentTheme) {
        switch (currentTheme) {
            case Theme.LIGHT:
                //changeTextColors(text,R.color.textContext);

                rootView.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                editText.setTextColor(getResources().getColor(R.color.textColor));
                name.setTextColor(getResources().getColor(R.color.textColor));
                //curveHolder2.setBackgroundColor(getResources().getColor(R.color.whiteGreen));


                break;
            case Theme.DEEP:
                rootView.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                editText.setTextColor(getResources().getColor(R.color.whiteGreen));

                name.setTextColor(getResources().getColor(R.color.whiteGreen));
                break;

        }
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);
        TextView[] textViews = {};
        setTextColors(textViews, currentTheme);


    }

}
