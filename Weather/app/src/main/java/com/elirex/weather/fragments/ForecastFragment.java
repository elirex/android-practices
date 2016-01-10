package com.elirex.weather.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.elirex.weather.networks.FetchWeatherTask;
import com.elirex.weather.R;
import com.elirex.weather.activities.DetailActivity;

import java.util.List;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2015/11/22.
 */
public class ForecastFragment extends Fragment implements
        FetchWeatherTask.OnWeatherDataListener {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private View mRootView;
    private ListView mListView;
    private ArrayAdapter<String> mForecastAdapter;
    private SwipeRefreshLayout mRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUIComponents();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), this);
        weatherTask.execute(getLocation());
    }

    private void setupUIComponents() {
        mListView = (ListView) mRootView.findViewById(R.id.listview_forecast);
        mListView.setOnItemClickListener(onItemClickListener);
        mRefresh = (SwipeRefreshLayout) mRootView
                .findViewById(R.id.refresh_listview_forecast);
        mRefresh.setOnRefreshListener(onRefreshForecastListListener);
        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,  R.id.list_item_forecast_textview);
        mListView.setAdapter(mForecastAdapter);
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String forecast = mForecastAdapter.getItem(position);
                    Bundle args = new Bundle();
                    args.putString(DetailFragment.EXTRA_FORECAST, forecast);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_BUNDLE, args);
                    startActivity(intent);
                }
            };

    private SwipeRefreshLayout.OnRefreshListener onRefreshForecastListListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateWeather();
                }
            };

    private String getLocation() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        return prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.title_activity_detail));
    }

    @Override
    public void onData(List<String> list) {
        mRefresh.setRefreshing(false);
        if(list == null) return;
        mForecastAdapter.clear();
        mForecastAdapter.addAll(list);
        mForecastAdapter.notifyDataSetChanged();
    }
}
