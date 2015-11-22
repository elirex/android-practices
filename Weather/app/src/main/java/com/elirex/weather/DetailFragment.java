package com.elirex.weather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class DetailFragment extends Fragment {

    public static final String EXTRA_FORECAST = "extra_weather_detail";

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView detail = (TextView) mRootView.findViewById(R.id.detail);
        Bundle args = getArguments();
        String detailStr = args.getString(EXTRA_FORECAST);
        detail.setText(detailStr);
        return mRootView;
    }



}
