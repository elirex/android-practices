package com.elirex.dragswiprecycleview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sheng-Yuan Wang (2015/12/16).
 */
public class RecyclerListFragment extends Fragment {

    private View mRootView;
    private ItemTouchHelper mItemTouchHelper;

    private List<String> mItems;

    public RecyclerListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mItems = generateItems(10);
        RecyclerViewListAdapter adapter = new RecyclerViewListAdapter(mItems);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private List<String> generateItems(int number) {
        List<String> items = new ArrayList<String>();
        for(int i = 0; i < number; ++i) {
           items.add(String.format("Item %d", i +1));
        }
        return items;
    }
}
