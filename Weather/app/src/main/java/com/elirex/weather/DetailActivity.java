package com.elirex.weather;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_BUNDLE = "extra_bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            Bundle args = getIntent().getBundleExtra(EXTRA_BUNDLE);
            Fragment fragmentClass = new DetailFragment();
            fragmentClass.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragmentClass,
                            getString(R.string.title_activity_detail))
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
