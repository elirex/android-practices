package com.elirex.viewpagersample;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new FirstFragment());
        fragments.add(new SecondFragment());
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("FirstFragment");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("SecondFragment");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
