package com.kariaki.choice.UI.Comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kariaki.choice.R;
import com.klinker.android.sliding.SlidingActivity;

import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

public class DragDismiss extends SlidingActivity {
   
    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("troubles");
        setContent(R.layout.dragdismiss);
    }
}
