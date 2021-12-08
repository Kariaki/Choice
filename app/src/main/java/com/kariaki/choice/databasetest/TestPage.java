package com.kariaki.choice.DatabaseTest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.kariaki.choice.R;

public class TestPage extends AppCompatActivity {


    RecyclerView galleryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
        galleryList=findViewById(R.id.galleryList);

    }
}