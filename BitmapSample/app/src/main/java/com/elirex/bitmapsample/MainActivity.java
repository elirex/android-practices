package com.elirex.bitmapsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.elirex.bitmapsample.views.CustomView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // CustomView customView = new CustomView(this);
        CustomView customView = (CustomView) findViewById(R.id.custom_view);
        customView.setImage(R.drawable.bitmapsample);
    }
}
