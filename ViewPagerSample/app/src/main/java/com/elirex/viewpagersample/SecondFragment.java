package com.elirex.viewpagersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ScrollingTabContainerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/11.
 */
public class SecondFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        List<Fragment> subFragments = new ArrayList<Fragment>();
        subFragments.add(new SubOneFragment());
        subFragments.add(new SubTwoFragment());
        subFragments.add(new SubThreeFragment());

        CustomViewPager viewPager = (CustomViewPager) view.findViewById(R.id.sub_viewpager);
        viewPager.setSliding(false);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), subFragments);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sub_tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("SubOne");
        tabLayout.getTabAt(1).setText("SubTwo");
        tabLayout.getTabAt(2).setText("SubThree");

        return view;
    }
}
