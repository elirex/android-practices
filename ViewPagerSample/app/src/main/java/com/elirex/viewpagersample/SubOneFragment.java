package com.elirex.viewpagersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/11.
 */
public class SubOneFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.list_view);
        listView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<String> items = new ArrayList<String>();
        for(int i = 1; i <= 30; ++i) {
            items.add("SubOneFragment Item-" + i);
        }

        CustomAdapter adapter = new CustomAdapter(items);
        listView.setAdapter(adapter);
        return view;
    }
}
