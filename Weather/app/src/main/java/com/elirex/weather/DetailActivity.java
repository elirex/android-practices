package com.elirex.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_BUNDLE = "extra_bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.fragment_detail));

        if(savedInstanceState == null) {
            Bundle args = getIntent().getBundleExtra(EXTRA_BUNDLE);
            Fragment fragmentClass = new DetailFragment();
            fragmentClass.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragmentClass,
                            getString(R.string.fragment_detail))
                    .commit();
        }

    }

}
