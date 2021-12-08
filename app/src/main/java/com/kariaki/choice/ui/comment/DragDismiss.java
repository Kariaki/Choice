package com.kariaki.choice.ui.Comment;

import android.os.Bundle;

import com.kariaki.choice.R;
import com.klinker.android.sliding.SlidingActivity;

public class DragDismiss extends SlidingActivity {
   
    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("troubles");
        setContent(R.layout.dragdismiss);
    }
}
