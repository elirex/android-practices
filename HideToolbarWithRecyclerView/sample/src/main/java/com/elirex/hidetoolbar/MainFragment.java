package com.elirex.hidetoolbar;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickwang on 2016/2/26.
 */
public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<String> mContents;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_list);
        mContents = new ArrayList<String>();
        generateFakeData(1000);
        ListAdapter adapter = new ListAdapter(mContents);
        mRecyclerView.addOnScrollListener(new ExtendOnScrollListener(toolbar, null));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);


        return rootView;
    }


    private void generateFakeData(int num) {
        for(int i = 0; i < num; ++i) {
            mContents.add("Item " + (i+1));
        }
    }

}
