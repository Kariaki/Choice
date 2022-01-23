package com.kariaki.choice.ui.profiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.util.Theme;

import java.util.HashMap;
import java.util.Map;

public class EditProfilePage extends AppCompatActivity {

    String phoneNumber;
    EditText editText;
    Button done;

    LinearLayout rootView;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        editText = findViewById(R.id.editText);
        done=findViewById(R.id.done);

        name = findViewById(R.id.name);
        rootView = findViewById(R.id.rootView);
        loadTheme();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().isEmpty()){
                    done.setEnabled(false);
                }else {
                    done.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    done.setEnabled(false);
                }else {
                    done.setEnabled(true);
                }
            }
        });

    }

    public void finishEdit(View view) {
        String text = editText.getText().toString();
        if (!text.isEmpty()) {
            DatabaseReference myInfo = FirebaseDatabase.getInstance().getReference("users").child(phoneNumber);
            Map<String, Object> update = new HashMap<>();
            update.put("displayName", text);

            myInfo.updateChildren(update);
            Toast.makeText(this, "Display name changed", Toast.LENGTH_SHORT).show();
        }
        onBackPressed();

    }

    public void back(View view) {
        onBackPressed();

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
