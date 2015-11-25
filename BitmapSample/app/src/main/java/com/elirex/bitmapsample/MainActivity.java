package com.elirex.bitmapsample;

import android.animation.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewAnimationUtils;

import com.elirex.bitmapsample.views.CustomView;

public class MainActivity extends AppCompatActivity {

    private CustomView mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // CustomView customView = new CustomView(this);
        mCustomView = (CustomView) findViewById(R.id.custom_view);
        mCustomView.setImage(R.drawable.bitmapsample);
    }

}
